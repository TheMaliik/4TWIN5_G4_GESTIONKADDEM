pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    // Perform SonarQube analysis
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        echo "Déploiement vers Nexus (tests ignorés)..."
                        sh 'mvn deploy -DskipTests'
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Deploy to Nexus : ${duration}s"
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker build -t guesmimelek/kaddem:0.0.1 .'
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
                            docker push guesmimelek/kaddem:0.0.1
                        '''
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '📊 Setting up application with existing monitoring infrastructure...'
                    sh '''
                        # Check if Prometheus container exists
                        if [ "$(docker ps -q -f name=prometheus)" ]; then
                            echo "Updating Prometheus configuration..."
                            
                            # Copy prometheus.yml to existing container if needed
                            if [ -f "prometheus.yml" ]; then
                                docker cp prometheus.yml prometheus:/etc/prometheus/
                                
                                # Check if Prometheus was started with --web.enable-lifecycle
                                LIFECYCLE_ENABLED=$(docker inspect --format='{{.Args}}' prometheus | grep -- "--web.enable-lifecycle" && echo true || echo false)
                                
                                if [ "$LIFECYCLE_ENABLED" = "true" ]; then
                                    # Reload Prometheus configuration (without restarting container)
                                    curl -X POST http://localhost:9090/-/reload
                                    echo "✅ Updated Prometheus configuration and reloaded"
                                else
                                    echo "⚠️ Warning: Prometheus lifecycle API not enabled. Restarting container."
                                    # Restart Prometheus to apply new configuration
                                    docker restart prometheus
                                    echo "✅ Restarted Prometheus to apply new configuration"
                                fi
                            fi
                        else
                            echo "Prometheus container not found. Starting new containers..."
                        fi
                        
                        # Only bring up the app service, don't touch monitoring
                        docker compose down    
                        docker compose pull
                        docker compose up -d
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def pipelineStatus = currentBuild.result ?: 'UNKNOWN'
                def bannerColor = pipelineStatus.toUpperCase() == 'SUCCESS' ? 'green' : 'red'

                def body = """<html>
                <body>
                <div style="border: 4px solid ${bannerColor}; padding: 10px;">
                <h2>${jobName} - Build ${buildNumber}</h2>
                <div style="background-color: ${bannerColor}; padding: 10px;">
                <h3 style="color: white;">Pipeline Status: ${pipelineStatus.toUpperCase()}</h3>
                </div>
                <p>Check the <a href="${env.BUILD_URL}">console output</a>.</p>
                </div>
                </body>
                </html>"""

                emailext(
                    subject: "${jobName} - Build ${buildNumber} - ${pipelineStatus.toUpperCase()}",
                    body: body,
                    to: 'guesmimelek928@gmail.com',
                    from: 'jenkins@example.com',
                    replyTo: 'jenkins@example.com',
                    mimeType: 'text/html',
                    attachmentsPattern: 'trivy-report.html'
                )
            }
        }
    }
}
