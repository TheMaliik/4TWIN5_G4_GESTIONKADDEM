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
                        script {
                          echo '📦 Déploiement sur Nexus...'
                        withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                              // sh 'mvn deploy -Dusername=$NEXUS_USER -Dpassword=$NEXUS_PASS'
                                
                                
                                sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:Ghaith1234@localhost:8081/repository/maven-releases/'
                        
                        }

                           }
                      }
                  }
          stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                   sh 'docker build -t ghaithoueslati/kaddem:0.0.1 .'

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
                            docker push ghaithoueslati/kaddem:0.0.1
                        '''
                    }
                }
            }
        }

    }
   
}
