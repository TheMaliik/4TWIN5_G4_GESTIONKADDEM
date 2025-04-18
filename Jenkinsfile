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
                        # Function to safely stop and remove a container
                        safe_remove_container() {
                            local container_name=$1
                            if [ "$(docker ps -q -f name=$container_name)" ]; then
                                echo "Stopping and removing existing $container_name container..."
                                docker stop $container_name || true
                                docker rm $container_name || true
                            fi
                        }

                        # Handle Prometheus configuration first
                        if [ -f "prometheus.yml" ] && [ "$(docker ps -q -f name=prometheus)" ]; then
                            # Backup existing Prometheus configuration
                            docker cp prometheus:/etc/prometheus/prometheus.yml prometheus.yml.backup || true
                            
                            # Stop and remove existing Prometheus container
                            safe_remove_container "prometheus"
                        fi

                        # Stop and remove existing Grafana container
                        safe_remove_container "grafana"

                        # Stop and remove existing app containers
                        safe_remove_container "ghaithdevops-app-timesheet-1"
                        safe_remove_container "ghaithdevops-mysqldb-1"

                        # Bring up all services with --force-recreate
                        docker compose up -d --force-recreate

                        # Restore Prometheus configuration if backup exists
                        if [ -f "prometheus.yml.backup" ]; then
                            echo "Restoring Prometheus configuration..."
                            # Stop Prometheus before copying the file
                            docker stop prometheus || true
                            
                            # Copy the configuration file
                            docker cp prometheus.yml.backup prometheus:/etc/prometheus/prometheus.yml
                            
                            # Start Prometheus again
                            docker start prometheus
                            echo "✅ Updated Prometheus configuration and restarted container"
                            
                            # Clean up backup
                            rm prometheus.yml.backup
                        fi

                        # Verify all containers are running
                        echo "Verifying container status..."
                        docker ps | grep -E "prometheus|grafana|mysqldb|app-timesheet"
                    '''
                }
            }
        }
    }
}
