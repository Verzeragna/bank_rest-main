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
                script {
                    echo "Starting Maven build..."
                    // returnStatus: true позволяет не падать на ненулевом коде возврата
                    def status = sh(script: 'mvn -B -DskipTests clean package', returnStatus: true)
                    if (status != 0) {
                        echo "WARNING: Maven build finished with status ${status}, but continuing..."
                    } else {
                        echo "Maven build completed successfully."
                    }
                }
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test' 
            }
        }
    }
}
