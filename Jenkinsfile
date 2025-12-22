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
                sh '''
                  mvn -B clean test
                '''
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Dockerfile Lint') {
            steps {
                sh '''
                  docker run --rm -i hadolint/hadolint < Dockerfile
                '''
            }
        }

        stage('Build Docker Image (local)') {
            steps {
                sh """
                  docker build \
                    -t ${DOCKERHUB_REPO}:${IMAGE_TAG} \
                    .
                """
            }
        }

        stage('Image Vulnerability Scan') {
            steps {
                sh '''
                  docker run --rm \
                    -v /var/run/docker.sock:/var/run/docker.sock \
                    aquasec/trivy:latest image \
                    --exit-code 1 \
                    --severity CRITICAL,HIGH \
                    ${DOCKERHUB_REPO}:${IMAGE_TAG}
                '''
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'durgarao418',
                    passwordVariable: 'Durga@418'
                )]) {
                    sh '''
                      echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                      docker push ${DOCKERHUB_REPO}:${IMAGE_TAG}
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Image pushed: ${DOCKERHUB_REPO}:${IMAGE_TAG}"
        }
        failure {
            echo "Pipeline failed — image not pushed"
        }
        always {
            sh 'docker logout || true'
        }
    }
}
