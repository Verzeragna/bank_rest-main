pipeline {
    agent any
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
    }
}
