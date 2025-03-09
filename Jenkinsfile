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
                // Run unit tests
                sh 'mvn test'
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

        stage('Deploy to Nexus') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Déploiement vers Nexus (tests ignorés)..."
                        mvn deploy -DskipTests
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Deploy to Nexus : ${duration}s"
                    }
                }
            }
        }
    }
}
