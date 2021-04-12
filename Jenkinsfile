#!/usr/bin/groovy
pipeline {
    agent any
    options {
        timeout(time: 25, unit: 'MINUTES')
        timestamps()
    }
    environment {
        MAVEN_OPTS = "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -Dorg.slf4j.simpleLogger.showDateTime=true -Djava.awt.headless=true -DinstallAtEnd=true -DdeployAtEnd=true"
        MAVEN_CLI_OPTS = "-U -B -e -fae -V"
    }
    stages {
        stage('Test-API') {
            steps {
                echo "Building and running tests for urban-waffle-api"
                dir('urban-waffle-api') {
                    sh "mvn-cd-build mvn -P '!default,repo-proxy' ${MAVEN_CLI_OPTS} clean test install"
                }
            }
        }
        stage('Code Quality Checks') {
            steps {
                sh "mvn-cd-build mvn -P '!default,repo-proxy' ${MAVEN_CLI_OPTS} pre-site -Ddetekt.phase=pre-site"
                publishHTML (target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'reports/detekt/',
                        reportFiles: '*.html',
                        reportName: "Detekt Code Quality Report"
                ])
            }
        }
        stage('Deploy-API') {
            when {
                branch 'main'
            }
            steps {
                echo "Deploying urban-waffle-api"
                dir('urban-waffle-api') {
                    sh "mvn-cd-build mvn -P '!default,repo-proxy' ${MAVEN_CLI_OPTS} deploy -DskipTests"
                }
            }
        }
        stage('Test') {
            steps {
                echo "Building and running tests"
                sh "mvn-cd-build mvn -P '!default,repo-proxy' ${MAVEN_CLI_OPTS} clean test"
            }
        }
        stage('Prepare-Copy-Dependencies') {
            when {
                branch 'main'
            }
            steps {
                echo "Copying dependencies and creating slim jar"
                sh "mvn-cd-build mvn -P '!default,repo-proxy' ${MAVEN_CLI_OPTS} -DskipTests -DexcludeScope=provided dependency:copy-dependencies package"
            }
        }
        stage('Push-Docker-Images') {
            when {
                branch 'main'
            }
            steps {
                echo "Pushing jobs image to docker registry"
                sh "mvn-cd-build 'cd urban-waffle-jobs/target && MODULE=urban-waffle-jobs buildctl image'"
                echo "Pushing service image to docker registry"
                sh "mvn-cd-build 'cd urban-waffle-service/target && MODULE=urban-waffle-service buildctl image'"
            }
        }
        stage('Deploy-All-Dev-Service') {
            when {
                branch 'main'
            }
            steps {
                echo "Deploying cronjobs to dev k8s"
                sh "mvn-cd-build 'udaan-k8s-build-cronjob dev'"
                echo "Deploying service to dev k8s"
                sh "mvn-cd-build 'cd urban-waffle-service/target && buildctl deploy dev'"
            }
        }
        stage('Deploy-All-Prod-Service') {
            when {
                anyOf {
                    allOf {
                        branch 'main'
                        expression { sh(script: "git log --format=%B -1 | grep '#deploy-to-prod'", returnStatus: true) == 0 }
                    }
                    // Canary for any commit with #canary
                    expression { sh(script: "git log --format=%B -1 | grep '#canary'", returnStatus: true) == 0 }
                }
            }
            steps {
                echo "Deploying jobs to prod k8s"
                sh "mvn-cd-build 'udaan-k8s-build-cronjob prod'"
                echo "Deploying service to prod k8s"
                sh "mvn-cd-build 'cd urban-waffle-service/target && buildctl deploy prod'"
            }
        }
    }
    post {
        success {
            slackSend color: 'good', message: "<${env.BUILD_URL}|${currentBuild.fullDisplayName}> ${currentBuild.currentResult}"
        }
        failure {
            slackSend color: 'danger', message: "<${env.BUILD_URL}|${currentBuild.fullDisplayName}> ${currentBuild.currentResult}"
        }
        unstable {
            slackSend color: 'warning', message: "<${env.BUILD_URL}|${currentBuild.fullDisplayName}> ${currentBuild.currentResult}"
        }
    }
}
