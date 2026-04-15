pipeline {
    agent any
    tools{
        jdk 'jdk21'
        maven 'maven3'
    }

    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        SONARQUBE_SERVER = 'SonarQubeServer'  // The name of the SonarQube server configured in Jenkins
        // Store the token securely in Jenkins
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub'
        DOCKER_IMAGE = 'thanh0201/javashoppingcartwithlocalizationuidb'
        DOCKER_TAG = 'latest'
    }

    stages {

        stage('Setup maven') {
            steps {
                script {
                    def mvnHome = tool name: 'maven3', type: 'maven'
                    env.PATH = "${mvnHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/THANH0201/javashoppingcartwithlocalizationuidb.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean package -DskipTests'
                    } else {
                        bat 'mvn clean package -DskipTests'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                        bat """
                            ${tool 'SonarScanner'}\\bin\\sonar-scanner ^
                            -Dsonar.projectKey=devops-demo ^
                            -Dsonar.projectName=DevOps-Demo ^
                            -Dsonar.sources=src/main/java ^
                            -Dsonar.tests=src/test/java ^
                            -Dsonar.java.binaries=target/classes ^
                            -Dsonar.host.url=http://localhost:9000 ^
                            -Dsonar.login=%SONAR_TOKEN%
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    if (isUnix()) {
                        sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    } else {
                        bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    }
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                    }
                }
            }
        }
    }

    post {
        always {
            junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
            recordCoverage tools: [[parser: 'JACOCO']]
        }
    }
}
