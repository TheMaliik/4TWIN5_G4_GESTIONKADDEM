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
                                       sh 'mvn deploy -Dusername=$NEXUS_USER -Dpassword=$NEXUS_PASS'

                                 }
                             }
                         }
                     }

    }
   
}