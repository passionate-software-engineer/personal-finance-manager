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
        stage('Deploy') {
          when{
            branch 'deploy' // TODO change to master
          }
          parallel {
            stage('BACKEND') {
                steps {
                     sh '''
                     cd backend/build/libs
                     scp -i "~/.ssh/piotr-key-aws.pem" backend-1.0.jar ec2-user@ec2-13-59-117-184.us-east-2.compute.amazonaws.com:/home/ec2-user/app/backend-1.0.jar.new
                     ssh -i "~/.ssh/piotr-key-aws.pem" ec2-user@ec2-13-59-117-184.us-east-2.compute.amazonaws.com <<'ENDSSH'
# must be formatted like that - command will pass whitespaces to remote server otherwise
# TODO move that to script, then it will be more natural
cd app
chmod 500 backend-1.0.jar.new
kill $(ps -ef | grep "[b]ackend-1.0.jar" | awk '{print $2}')
mv backend-1.0.jar backend-1.0.jar.bak
mv backend-1.0.jar.new backend-1.0.jar
nohup java -jar backend-1.0.jar -Dspring.profile=aws >> /dev/null 2>> /dev/null &
ENDSSH
                       '''
                }
            }
            stage(‘FRONTEND’) {
                steps {
                     sh '''
                        cd frontend
                        ng build --configuration=aws
                        cd dist/frontend
                        aws s3 cp --profile pfm --recursive --acl "public-read" . s3://personal-finance-manager
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
        stage(‘Deploy’) {



    post {
        always {
            archiveArtifacts artifacts: 'backend/build/jacocoHtml/**/*, backend/build/reports/checkstyle/**/*'
            junit 'backend/build/test-results/**/*.xml'
        }
    }
}
