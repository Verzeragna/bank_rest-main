pipeline {
    agent any 
    tools {
        maven 'maven'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') { 
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'mvn -B -DskipTests clean package' 
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test' 
            }
        }
    }
    post {
        success {
            echo 'Сборка прошла успешно!'
        }
        failure {
            echo 'Сборка завершилась с ошибкой!'
        }
    }
}
