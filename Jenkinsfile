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
                    echo '📊 Setting up monitoring with Prometheus and Grafana...'
                    // Ensure prometheus.yml exists in the workspace
                    sh '''
                        docker compose down
                        docker compose pull
                        docker compose up -d
                    '''
                }
            }
        }
    }

     
    }
   
