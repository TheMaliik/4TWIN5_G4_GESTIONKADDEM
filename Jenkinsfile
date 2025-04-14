pipeline {
    agent any
    
    // Define environment variables for email recipients
    environment {
        EMAIL_RECIPIENTS = 'rima.benabdallah@gmail.com'// Replace with your email address
    }
    
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
                     //   withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                              // sh 'mvn deploy -Dusername=$NEXUS_USER -Dpassword=$NEXUS_PASS'

                        sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:admin@localhost:8081/repository/maven-releases/'
                        
                      //   }

                           }
                      }
                  }
             
        stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker build -t rimabenabdallah/kaddem:0.0.1 .'
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
                            docker push rimabenabdallah/kaddem:0.0.1
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
     
    }
    
    // Add email notification for build results
    post {
        always {
            echo 'Sending email notification...'
            emailext (
                subject: "Build ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>Build Status: ${currentBuild.result}</p>
                        <p>Build Number: ${env.BUILD_NUMBER}</p>
                        <p>Build URL: ${env.BUILD_URL}</p>
                        <p>Project: ${env.JOB_NAME}</p>""",
                to: "${env.EMAIL_RECIPIENTS}",
                attachLog: true,
                compressLog: true,
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']]
            )
        }
        success {
            echo 'Build successful! 🎉'
        }
        failure {
            echo 'Build failed! 🚨'
        }
    }
}
