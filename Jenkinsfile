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
                    echo '📊 Setting up application ...'
                    sh '''
                        docker compose down    
                        docker compose pull
                        docker compose up -d
                    '''
                }
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
                        to: 'melek.guesmi@esprit.tn',
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
                to: 'melek.guesmi@esprit.tn',
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'</p>
                <p>Check console output at <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>""",
                mimeType: 'text/html'
            )
        }
    }
}
