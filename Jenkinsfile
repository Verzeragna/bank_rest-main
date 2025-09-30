pipeline {
    agent any
    environment {
        BOT_TOKEN = bot8234051771:AAFDF7Ee1X7L9wZ55cQAzLg1SRB_ATeUBcM
    }
    tools {
        maven 'maven' // Имя Maven из Global Tool Configuration Jenkins
    }
    stages {
        stage('Info') {
            steps {
                echo "Checking Java and Maven versions..."
                sh 'java -version || true'
                sh 'mvn -version || true'
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Starting Maven build..."
                sh 'mvn -B -DskipTests clean package'
                echo "Build stage completed."
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                sh 'mvn test'
                echo "Test stage completed."
            }
        }
        
        stage('Save artifact') {
            steps {
                archiveArtifacts(artifacts: 'target/*.jar')
            }
            post {
                success {
                    curl -X POST -H 'Content-type: application/json' \
                    --data '{"addlication": "bank_rest-main", "message": "Сборка прошла успешно" }' \
                    https://api.telegram.org/$BOT_TOKEN/sendMessage
                }
            }
        }
    }
}
