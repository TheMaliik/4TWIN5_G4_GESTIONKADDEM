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
                   sh 'docker build -t ghaith339/kaddem:0.0.1 .'

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
                            docker push ghaith339/kaddem:0.0.1
                        '''
                    }
                }
            }
        }
            stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '🚀 Deploying with Docker Compose...'
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
                    sh 'docker compose up -d'
                }
            }
        }  
       

    }
   
}
