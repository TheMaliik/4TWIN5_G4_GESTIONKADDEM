pipeline {
    agent any

    tools {
        maven 'M2_HOME'
    }

    environment {
        DOCKER_IMAGE = "ahmedbenhmida/kaddem-app"
        DOCKER_TAG = "latest"
    }

    stages {
        stage('Git Checkout') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'AhmedBenHmida-4TWIN5-G4']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/TheMaliik/4TWIN5_G4_GESTIONKADDEM.git',
                            credentialsId: 'gitt'
                        ]]
                    ])
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test -Dtest=EtudiantServiceImplTest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Kaddem-sq') {
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
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
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
                    sh "docker run -d -p 8080:8080 --name kaddem-container --restart=always ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
    }
}
