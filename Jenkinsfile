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
                sh 'mvn -B -DskipTests clean package || true' 
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test' 
            }
        }
    }
}
