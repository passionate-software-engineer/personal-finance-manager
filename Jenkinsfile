pipeline {
    agent {
        label "pfm-docker-java-11"
    }
    parameters {
        string(name: 'APP_URL', defaultValue: 'http://personal-finance-manager.s3-website.us-east-2.amazonaws.com', description: 'Application (frontend) URL')
        string(name: 'APP_S3_BUCKET', defaultValue: 's3://personal-finance-manager', description: 'S3 bucket for frontend upload')
        string(name: 'EC2_INSTANCE', defaultValue: 'ec2-3-120-209-115.eu-central-1.compute.amazonaws.com', description: 'EC2 instance for backend service')
    }
    options {
        timeout(time: 20, unit: 'MINUTES')
        timestamps()
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
               ./scripts/run_e2e.sh
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
                           scp -o StrictHostKeyChecking=no backend-1.0.jar ec2-user@$EC2_INSTANCE:/home/ec2-user/app/backend-1.0.jar.new
                           scp ../../../scripts/start_backend.sh ec2-user@$EC2_INSTANCE:/home/ec2-user/app/start_app.sh
                           ssh ec2-user@$EC2_INSTANCE "cd app && source ~/.bash_profile && ./start_app.sh"
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
                        aws s3 cp --recursive --acl "public-read" . $APP_S3_BUCKET
                        '''
                }
            }
         }
      }
      stage('App startup') {
        when{
          branch 'master'
        }
        steps {
          sh '''
             ./scripts/wait_until_app_is_ready.sh $EC2_INSTANCE
             '''
        }
      }
      stage('E2E after deploy') {
        when{
          branch 'master'
        }
        steps {
          sh '''
             webdriver-manager update
             protractor frontend/e2e/protractor.conf.js --baseUrl $APP_URL
             '''
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
