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
                        sh '''
                        echo "Déploiement vers Nexus (tests ignorés)..."
                        mvn deploy -DskipTests
                        '''
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

        stage("Deploy with Docker Compose") {
            steps {
                sh '''
                    docker compose down
                    docker compose pull
                    docker compose up -d
                '''
            }
        }
    }

    post {
        success {
            emailext(
                subject: "Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Good news! The pipeline completed successfully.",
                recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']]
            )
        }
        failure {
            emailext(
                subject: "Pipeline Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Unfortunately, the pipeline has failed. Please check the console output for more details.",
                recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}
