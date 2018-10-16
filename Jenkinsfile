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
                           ./gradlew dependencyUpdates -Drevision=release -DoutputDir=build/reports/dependencyUpdates
                           '''
                    }
                }
                stage('FRONTEND') {
                    steps {
                        sh '''
                           cd frontend
                           npm install
                           ng build
                           ncu > ncu_output.txt
                           '''
                    }
                }
            }
        }
        stage('E2E') {
          steps {
            sh '''
               ./run_e2e.sh
               '''
          }
        }
        stage('Deploy') {
          when{
            branch 'master'
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
nohup java -jar backend-1.0.jar --spring.profiles.active=aws >> application.log 2>> application.log &
ENDSSH
                       '''
                }
            }
            stage('FRONTEND') {
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
            archiveArtifacts artifacts: 'backend/build/reports/**/*, frontend/ncu_output.txt'
            junit 'backend/build/test-results/**/*.xml'
        }
    }
}
