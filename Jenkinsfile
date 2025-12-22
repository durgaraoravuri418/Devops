pipeline {
    agent any

    environment {
        APP_NAME = "spring-app"
        DOCKERHUB_REPO = "yourdockerhubusername/spring-app"
        IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mv
