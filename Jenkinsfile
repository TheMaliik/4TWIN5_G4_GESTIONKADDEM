pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
              
                sh 'mvn clean compile'
            }
        }

        stage(' Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                   
                    sh 'mvn sonar:sonar'
                }
            }
        }
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:admin@localhost:8081/repository/maven-releases/'
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker build -t samirtemtem/kaddem:0.0.1 .'
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    echo '🚀 Pushing Docker Image to DockerHub...'
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PAT')]) {
                        sh '''
                            echo "$DOCKER_PAT" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push samirtemtem/kaddem:0.0.1
                        '''
                    }
                }
            }
        }

        stage("Deploy with Docker Compose") {
            steps {
                script {
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
                        
                        # Only bring up the app service, don't touch monitoring
                        docker compose up -d mysql spring-boot-app
                    '''
                }
            }
        }
        
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
                        def prometheusData = sh(script: 'curl -s http://192.168.33.10:8089/kaddem/actuator/prometheus | grep -E "jvm_memory_used_bytes|process_uptime_seconds|http_server_requests_seconds_count" | head -10', returnStdout: true).trim()
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
                        to: 'samir.temtem@esprit.tn',
                        subject: "${BUILD_STATUS}: Job '${JOB_NAME} [${BUILD_NUMBER}]'",
                        body: emailBody,
                        mimeType: 'text/html',
                        attachLog: true
                    )
                }
            }
        }
    }
    
    // Configure post-build actions for notification in case of failure
    post {
        failure {
            emailext (
                to: 'samir.temtem@esprit.tn',
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                <p>Check console output at <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>""",
                mimeType: 'text/html'
            )
        }
    }
}
   
