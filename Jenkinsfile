pipeline {
    agent any
    stages {
        stage('Build') { 
            steps {
                sh 'java -version'
                sh 'javac -version'
                sh 'mvn -version'
                sh 'mvn -B -DskipTests clean package' 
            }
        }
    }
}
