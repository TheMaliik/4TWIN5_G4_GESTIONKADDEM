pipeline {
    agent any  // Use any available Jenkins agent

    tools {
        maven 'M2_HOME'  // Use the configured Maven installation in Jenkins
    }

    environment {
        // Define environment variables for Docker and Nexus repository
        DOCKER_IMAGE = "ahmedbenhmida/kaddem-app"
        DOCKER_TAG = "latest"
        NEXUS_URL = "http://localhost:8081/repository/maven-releases"
        GROUP_ID = "tn.esprit.spring"
        ARTIFACT_ID = "kaddem"
        VERSION = "0.0.1-SNAPSHOT"
        PACKAGING = "jar"
    }

    stages {
        stage('Git Checkout') {
            steps {
                script {
                    // Clone the Git repository from GitHub
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'AhmedBenHmida-4TWIN5-G4']], // Branch name
                        userRemoteConfigs: [[
                            url: 'https://github.com/TheMaliik/4TWIN5_G4_GESTIONKADDEM.git', // GitHub repo URL
                            credentialsId: 'gitt'  // Jenkins credentials ID for GitHub
                        ]]
                    ])
                }
            }
        }

        stage('Build JAR') {
            steps {
                // Build the Maven project and package the JAR, skipping tests
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                // Run specific unit tests in the project
                sh 'mvn test -Dtest=EtudiantServiceImplTest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Run SonarQube analysis for code quality checks
                withSonarQubeEnv('Kaddem-sq') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                // Deploy the JAR to the Nexus repository
                //sh 'mvn clean deploy'
                //sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:nexus@localhost:8081/repository/maven-releases/'
                sh 'mvn deploy -DaltDeploymentRepository=nexus::default::http://admin:nexus@localhost:8081/repository/maven-snapshots/'

            }
        }

        stage('Download JAR from Nexus') {
            steps {
                // Download the JAR from Nexus to ensure the latest version is used for Docker
                sh 'wget --http-user=admin --http-password=nexus -O target/${ARTIFACT_ID}-${VERSION}.${PACKAGING} ${NEXUS_URL}/$(echo ${GROUP_ID} | sed "s/\\./\\//g")/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.${PACKAGING}'
            }
        }

        stage('Docker Build') {
            steps {
                // Build the Docker image using the downloaded JAR
                sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
            }
        }

        stage('Docker Login') {
            steps {
                // Authenticate to Docker Hub using Jenkins credentials
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    '''
                }
            }
        }

        stage('Docker Push') {
            steps {
                // Push the Docker image to Docker Hub
                sh 'docker push ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Check if the container is already running
                    def containerExists = sh(script: "docker ps -q -f name=kaddem-container", returnStdout: true).trim()

                    if (containerExists) {
                        // Stop and remove the existing container before deploying the new one
                        sh 'docker stop kaddem-container && docker rm kaddem-container'
                    }

                    // Run the new container using the latest Docker image
                    sh "docker run -d -p 8088:8080 --name kaddem-container --restart=always ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
    }
}
