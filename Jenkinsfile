pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests specifically for EtudiantRestController
                sh 'mvn test -Dtest=EtudiantRestControllerTest'
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

        stage('Deploy') {
            steps {
                // Deploy the application, skipping tests
                sh 'mvn deploy -DskipTests'
            }
        }
    }
}
