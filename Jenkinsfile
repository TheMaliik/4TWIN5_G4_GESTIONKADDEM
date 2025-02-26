pipeline {
    agent any // Use any available agent for execution

    tools {
        maven 'M2_HOME' // Define Maven tool
    }

    environment {
        DOCKER_IMAGE = "ahmedbenhmida/kaddem-app" // Define Docker image name
        DOCKER_TAG = "latest" // Define Docker image tag
    }

    stages {
        stage('Git Checkout') {
            steps {
                // Clone the repository from GitHub using credentials
                git branch: 'AhmedBenHmida-4TWIN5-G4',
                    url: 'https://github.com/TheMaliik/4TWIN5_G4_GESTIONKADDEM.git',
                    credentialsId: 'gitt'
            }
        }

        stage('Build') {
            steps {
                // Clean project, package the application, and skip tests
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests for a specific service implementation
                sh 'mvn test -Dtest=EtudiantServiceImplTest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Perform static code analysis using SonarQube
                withSonarQubeEnv('Kaddem-sq') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                // Build Docker image using the Dockerfile in the project root
                sh 'docker build -t $DOCKER_IMAGE:$DOCKER_TAG .'
            }
        }

        stage('Docker Login') {
            steps {
                // Authenticate with DockerHub using Jenkins credentials
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Docker Push') {
            steps {
                // Push the newly built Docker image to DockerHub
                sh 'docker push $DOCKER_IMAGE:$DOCKER_TAG'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Check if a container named "kaddem-container" is running
                    def containerExists = sh(script: "docker ps -q -f name=kaddem-container", returnStdout: true).trim()

                    if (containerExists) {
                        // Stop and remove existing container if it's running
                        sh 'docker stop kaddem-container && docker rm kaddem-container'
                    }

                    // Run a new container with the latest image
                    sh '''
                    docker run -d -p 8080:8080 \
                        --name kaddem-container \
                        --restart=always \  // Restart container automatically on failure
                        $DOCKER_IMAGE:$DOCKER_TAG
                    '''
                }
            }
        }
    }
}
