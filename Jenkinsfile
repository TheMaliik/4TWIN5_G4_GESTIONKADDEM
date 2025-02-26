pipeline {
    agent any
    
  environment {
        DOCKER_IMAGE = "guesmimelek/kaddem-app"
        DOCKER_TAG = "latest"
    }
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

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DockerHub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    '''
                }
            }
        }

        stage('Docker Push') {
            steps {
                sh 'docker push ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def containerExists = sh(script: "docker ps -q -f name=kaddem-container", returnStdout: true).trim()

                    if (containerExists) {
                        sh 'docker stop kaddem-container && docker rm kaddem-container'
                    }

                    // Corrected single-line command
                    sh "docker run -d -p 8088:8080 --name kaddem-container --restart=always ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
    }

    
}
