pipeline {
    agent {
        label "master"
    }
    options {
        timeout(time: 10, unit: 'MINUTES')
    }
    triggers {
        cron('H 0 * * *')
    }
    stages {
        stage('Build') {
            parallel {
                stage('BACKEND') {
                    steps {
                        sh '''
                           cd backend
                           ./gradlew build
                           '''
                    }
                }
                stage('FRONTEND') {
                    steps {
                        sh '''
                           cd frontend
                           npm install
                           ng build
                           '''
                    }
                }
                stage('E2E') {
                    steps {
                        sh '''
                           cd frontend-test
                           ./gradlew build
                           '''
                    }
                }
            }

        }
        stage('Sonar') {
            parallel {
                stage('BACKEND') {
                    steps {
                        sh '''
                           cd backend
                           sonar-scanner \
                           -Dsonar.projectKey=backend \
                           -Dsonar.sources=. \
                           -Dsonar.java.source=1.8 \
                           -Dsonar.java.binaries=build/classes \
                           -Dsonar.host.url=http://piotr-sonar.eu.ngrok.io/ \
                           -Dsonar.login=28e3478c5aab55128d7caf542d3d0a32646c1345
                           '''
                    }
                }
                stage('FRONTEND') {
                    steps {
                        sh '''
                           cd frontend
                           sonar-scanner \
                           -Dsonar.projectKey=frontend \
                           -Dsonar.sources=src \
                           -Dsonar.host.url=http://piotr-sonar.eu.ngrok.io/ \
                           -Dsonar.login=28e3478c5aab55128d7caf542d3d0a32646c1345
                           '''
                    }
                }
                stage('E2E') {
                    steps {
                        sh '''
                           cd frontend-test
                           sonar-scanner \
                           -Dsonar.projectKey=frontend-test \
                           -Dsonar.sources=. \
                           -Dsonar.java.source=1.8 \
                           -Dsonar.java.binaries=build/classes \
                           -Dsonar.host.url=http://piotr-sonar.eu.ngrok.io/ \
                           -Dsonar.login=28e3478c5aab55128d7caf542d3d0a32646c1345
                           '''
                    }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'backend/build/jacocoHtml/**/*, backend/build/reports/checkstyle/**/*'
            junit 'backend/build/test-results/**/*.xml'
        }
    }
}
