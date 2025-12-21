pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    tools {
        maven 'Maven 3.8.1'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean & Compile') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target\\surefire-reports\\*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target\\*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ JAR build successful'
        }
        failure {
            echo '❌ Build failed'
        }
        always {
            cleanWs()
        }
    }
}
