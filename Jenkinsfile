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
                bat 'mvn -B clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Dockerfile Lint') {
            steps {
                bat '''
                docker run --rm -i hadolint/hadolint < Dockerfile
                '''
            }
        }

        stage('Build Docker Image (Local)') {
            steps {
                bat """
                docker build -t %DOCKERHUB_REPO%:%IMAGE_TAG% .
                """
            }
        }

        stage('Image Vulnerability Scan') {
            steps {
                bat """
                docker run --rm ^
                  -v //var/run/docker.sock:/var/run/docker.sock ^
                  aquasec/trivy:latest image ^
                  --exit-code 1 ^
                  --severity CRITICAL,HIGH ^
                  %DOCKERHUB_REPO%:%IMAGE_TAG%
                """
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker push %DOCKERHUB_REPO%:%IMAGE_TAG%
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Image pushed successfully: ${DOCKERHUB_REPO}:${IMAGE_TAG}"
        }
        failure {
            echo "❌ Pipeline failed. Image NOT pushed."
        }
        always {
            bat 'docker logout || exit 0'
        }
    }
}
