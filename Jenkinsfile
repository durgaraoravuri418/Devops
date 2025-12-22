pipeline {
    agent any

    environment {
        APP_NAME = "spring-app"
        DOCKERHUB_REPO = "durgarao418/spring-app"
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
                bat """
                type Dockerfile | docker run --rm -i hadolint/hadolint
                """
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
                  -v %WORKSPACE%\\.trivycache:/root/.cache ^
                  aquasec/trivy:latest image ^
                  --timeout 10m ^
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
                    usernameVariable: 'durgarao418',
                    passwordVariable: 'Durga@418'
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
            bat 'docker logout || echo Docker already logged out'
        }
    }
}
