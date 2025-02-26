pipeline {
    agent any
    tools {
            maven 'M2_HOME'
        }

    stages {
        stage('Git Checkout') {
                steps {
                    git branch: 'AhmedBenHmida-4TWIN5-G4',
                        url: 'https://github.com/TheMaliik/4TWIN5_G4_GESTIONKADDEM.git',
                        credentialsId: 'gitt'
                }
            }
        stage('Build') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests specifically for EtudiantRestController
                sh 'mvn test -Dtest=EtudiantServiceImplTest'
            }
        }



    }
}
