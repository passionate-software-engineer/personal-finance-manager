pipeline {
    agent {
        label "pfm-docker-java-11"
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
                    sshagent(credentials : ['AWS_PRIVATE_KEY']) {
                        sh '''
                           cd backend/build/libs
                           scp -o StrictHostKeyChecking=no backend-1.0.jar ec2-user@ec2-13-59-117-184.us-east-2.compute.amazonaws.com:/home/ec2-user/app/backend-1.0.jar.new
                           scp ../../../start_backend.sh ec2-user@ec2-13-59-117-184.us-east-2.compute.amazonaws.com:/home/ec2-user/app/start_backend.sh
                           ssh ec2-user@ec2-13-59-117-184.us-east-2.compute.amazonaws.com "./start_backend" 
                           '''
                    }
                }
            }
            stage('FRONTEND') {
                environment {
                    AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
                    AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
                }
                steps {
                     sh '''
                        cd frontend
                        ng build --configuration=aws
                        cd dist/frontend
                        aws s3 cp --recursive --acl "public-read" . s3://personal-finance-manager
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
