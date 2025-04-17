pipeline {
    agent any  // Use any available Jenkins agent

    tools {
        maven 'M2_HOME'  // Use the configured Maven installation in Jenkins
    }

    environment {
        // Define environment variables for Docker and Nexus repository
        DOCKER_IMAGE = "ahmedbenhmida/kaddem-app"
        DOCKER_TAG = "latest"
        NEXUS_URL = "http://localhost:8081/repository/maven-releases"
        GROUP_ID = "tn.esprit.spring"
        ARTIFACT_ID = "kaddem"
        VERSION = "0.0.1"
        PACKAGING = "jar"
    }

    stages {
        stage('Git Checkout') {
            steps {
                script {
                    // Clone the Git repository from GitHub
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'AhmedBenHmida-4TWIN5-G4']], // Branch name
                        userRemoteConfigs: [[
                            url: 'https://github.com/TheMaliik/4TWIN5_G4_GESTIONKADDEM.git', // GitHub repo URL
                            credentialsId: 'gitt'  // Jenkins credentials ID for GitHub
                        ]]
                    ])
                }
            }
        }

        stage('Build JAR') {
            steps {
                // Build the Maven project and package the JAR, skipping tests
                //sh 'mvn clean package -DskipTests'
                sh 'mvn clean install'
            }
        }

        stage('Run Unit Tests') {
            steps {
                // Run specific unit tests in the project
                sh 'mvn test -Dtest=EtudiantServiceImplTest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Run SonarQube analysis for code quality checks
                withSonarQubeEnv('Kaddem-sq') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                // Deploy the JAR to the Nexus repository
                //sh 'mvn clean deploy'
                sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:nexus@localhost:8081/repository/maven-releases/'
                //sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:nexus@localhost:8081/repository/maven-snapshots/'

            }
        }

        stage('Download JAR from Nexus') {
            steps {
                // Ensure the target directory exists before download
             //   sh 'mkdir -p target'

                // Download the JAR from Nexus to ensure the latest version is used for Docker
                sh 'wget --http-user=admin --http-password=nexus -O target/${ARTIFACT_ID}-${VERSION}.${PACKAGING} ${NEXUS_URL}/$(echo ${GROUP_ID} | sed "s/\\./\\//g")/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.${PACKAGING}'

                // Print working directory and contents of target
           //     sh 'pwd'               // Print current directory
           //     sh 'ls -lah'           // List the contents of the current directory
           //    sh 'ls -lah target/'    // List the contents of the target directory
           //     sh 'chmod -R 755 target/'  // Ensure correct permissions for the target directory and files
            }
        }

        stage('Docker Build') {
            steps {
                // Verify JAR exists before building Docker image
               // sh 'if [ -f "target/kaddem-0.0.1.jar" ]; then mv target/kaddem-0.0.1.jar kaddem.jar; else echo "JAR file not found"; exit 1; fi'

                // List current directory to ensure the JAR is renamed
              //  sh 'ls -lah target/'  // List files in the target directory

                // Build the Docker image using the local JAR
                //sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'

                // Build the Docker image using the downloaded JAR
                sh '''
                docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                '''
            }
        }

        stage('Docker Login') {
            steps {
                // Authenticate to Docker Hub using Jenkins credentials
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    '''
                }
            }
        }

        stage('Docker Push') {
            steps {
                // Push the Docker image to Docker Hub
                sh 'docker push ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }

/*
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down || true'  // Ensure cleanup before running
                    echo '📊 Setting up application with existing monitoring infrastructure...'
                                        sh '''
                                            # Only update app containers, preserving monitoring containers
                                            # Copy prometheus.yml to existing container if needed
                                            if [ -f "prometheus.yml" ] && [ "$(docker ps -q -f name=prometheus)" ]; then
                                                # Update the prometheus.yml file in the existing container
                                                docker cp prometheus.yml prometheus:/etc/prometheus/

                                                # Check if Prometheus was started with --web.enable-lifecycle
                                                LIFECYCLE_ENABLED=$(docker inspect --format='{{range .Args}}{{if eq . "--web.enable-lifecycle"}}true{{end}}{{end}}' prometheus)

                                                if [ "$LIFECYCLE_ENABLED" = "true" ]; then
                                                    # Reload Prometheus configuration (without restarting container)
                                                    curl -X POST http://localhost:9090/-/reload
                                                    echo "✅ Updated Prometheus configuration and reloaded"
                                                else
                                                    echo "⚠️ Warning: Prometheus lifecycle API not enabled. Need to restart container."
                                                    # Restart Prometheus to apply new configuration
                                                    docker restart prometheus
                                                    echo "✅ Restarted Prometheus to apply new configuration"
                                                fi
                                            fi
                                        '''
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose down || true'
                    echo '📊 Setting up application with existing monitoring infrastructure...'
                    sh '''
                        if [ -f "prometheus.yml" ] && [ "$(docker ps -q -f name=prometheus)" ]; then
                            echo "📦 Stopping Prometheus container to update config..."
                            docker stop prometheus
                            docker cp prometheus.yml prometheus:/etc/prometheus/
                            docker start prometheus
                            echo "✅ Prometheus restarted with new configuration"
                        fi
                    '''
                    sh 'docker-compose up -d'
                }
            }
        }
*/
        stage("Deploy with Docker Compose") {
            steps {
                script {
                    echo '📊 Setting up application with existing monitoring infrastructure...'
                    sh '''
                        # Only update app containers, preserving monitoring containers
                        # Copy prometheus.yml to existing container if needed
                        if [ -f "prometheus.yml" ] && [ "$(docker ps -q -f name=prometheus)" ]; then
                            # Safely copy the prometheus.yml file to a temporary path
                                docker cp prometheus.yml prometheus:/etc/prometheus/prometheus_temp.yml

                            # Replace the original file (handle resource busy safely)
                            docker exec prometheus sh -c 'mv -f /etc/prometheus/prometheus_temp.yml /etc/prometheus/prometheus.yml'

                            # Check if Prometheus was started with --web.enable-lifecycle
                            LIFECYCLE_ENABLED=$(docker inspect --format='{{range .Args}}{{if eq . "--web.enable-lifecycle"}}true{{end}}{{end}}' prometheus)

                            if [ "$LIFECYCLE_ENABLED" = "true" ]; then
                                # Reload Prometheus configuration (without restarting container)
                                curl -X POST http://localhost:9090/-/reload
                                echo "✅ Updated Prometheus configuration and reloaded"
                            else
                                echo "⚠️ Warning: Prometheus lifecycle API not enabled. Need to restart container."
                                # Restart Prometheus to apply new configuration
                                docker restart prometheus
                                echo "✅ Restarted Prometheus to apply new configuration"
                            fi
                        fi

                        if [ "$(docker ps -a -q -f name=mysqldb)" ]; then
                            echo "🔁 Container mysqldb already exists, restarting..."
                            docker start mysqldb || true
                        else
                            docker compose up -d mysqldb
                        fi
                        if [ "$(docker ps -a -q -f name=kaddem-app)" ]; then
                            echo "🔁 Container kaddem-app already exists, restarting..."
                            docker start kaddem-app || true
                        else
                            docker compose up -d kaddem-app
                        fi
                    '''
                }
            }
        }

/*
        stage('Send Email Notification') {
                    steps {
                        script {
                            echo '📧 Preparing build report email...'

                            // Get build information
                            def BUILD_STATUS = currentBuild.currentResult
                            def BUILD_URL = env.BUILD_URL
                            def JOB_NAME = env.JOB_NAME
                            def BUILD_NUMBER = env.BUILD_NUMBER
                            def BUILD_DURATION = currentBuild.durationString

                            // Collect basic metrics
                            def metricsSummary = ""
                            try {
                                def prometheusData = sh(script: 'curl -s http://192.168.33.10:9100/metrics | grep -E "jvm_memory_used_bytes|process_uptime_seconds|http_server_requests_seconds_count" | head -10', returnStdout: true).trim()
                                metricsSummary = prometheusData.replaceAll("\n", "<br>")
                            } catch (Exception e) {
                                metricsSummary = "Unable to fetch metrics: ${e.message}"
                            }

                            // Create HTML email content
                            def emailBody = """
                            <html>
                            <body style="font-family: Arial, sans-serif;">
                                <h2 style="color: #2e6c80;">Jenkins Build Report - ${JOB_NAME}</h2>
                                <table style="border-collapse: collapse; width: 100%; border: 1px solid #ddd;">
                                    <tr style="background-color: #f2f2f2;">
                                        <th style="padding: 12px; text-align: left; border: 1px solid #ddd;">Attribute</th>
                                        <th style="padding: 12px; text-align: left; border: 1px solid #ddd;">Value</th>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Status</strong></td>
                                        <td style="padding: 12px; border: 1px solid #ddd; color: ${BUILD_STATUS == 'SUCCESS' ? 'green' : 'red'};">${BUILD_STATUS}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Build Number</strong></td>
                                        <td style="padding: 12px; border: 1px solid #ddd;">${BUILD_NUMBER}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Duration</strong></td>
                                        <td style="padding: 12px; border: 1px solid #ddd;">${BUILD_DURATION}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Build URL</strong></td>
                                        <td style="padding: 12px; border: 1px solid #ddd;"><a href="${BUILD_URL}">${BUILD_URL}</a></td>
                                    </tr>
                                </table>

                                <h3 style="color: #2e6c80; margin-top: 20px;">Application Metrics</h3>
                                <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 5px; font-family: 'Courier New', monospace; font-size: 12px;">
                                ${metricsSummary}
                                </pre>

                                <p style="margin-top: 20px; font-style: italic; color: #666;">This is an automated email sent by Jenkins.</p>
                            </body>
                            </html>
                            """

                            // Send email using Mailtrap
                            emailext (
                                to: 'benhmida.ahmed1@esprit.tn',
                                subject: "${BUILD_STATUS}: Job '${JOB_NAME} [${BUILD_NUMBER}]'",
                                body: emailBody,
                                mimeType: 'text/html',
                                attachLog: true
                            )
                        }
                    }

            }
*/

    }// End of stages
/*
    // Configure post-build actions for notification in case of failure
    post {
        failure {
            emailext (
                to: 'benhmida.ahmed1@esprit.tn',
                subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                body: "Pipeline failed: ${env.BUILD_URL}",
                mimeType: 'text/plain'
            )
        }
    }
*/
}
