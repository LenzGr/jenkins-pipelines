library changelog: false, identifier: "lib@master", retriever: modernSCM([
    $class: 'GitSCMSource',
    remote: 'https://github.com/Percona-Lab/jenkins-pipelines.git'
])

pipeline {
  agent {
    label 'min-centos-7-x64'
  }
  environment {
      PATH = '/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/home/ec2-user/.local/bin'
  }
  parameters {
        choice(
            name: 'PLATFORM',
            description: 'For what platform (OS) need to test',
            choices: pdpxcOperatingSystems()
        )
        choice(
            name: 'FROM_REPO',
            description: 'PDPXC will be upgraded from this repository'
            choices: [
                'release',
                'testing',
                'experimental'
            ]
        )
        choice(
            name: 'TO_REPO',
            description: 'Repo for testing',
            choices: [
                'testing',
                'experimental',
                'release'
            ]
        )
        string(
            defaultValue: '8.0.29',
            description: 'From this version pdmysql will be updated',
            name: 'FROM_VERSION'
        )
        string(
            defaultValue: '8.0.30',
            description: 'To this version pdmysql will be updated',
            name: 'VERSION'
        )
        string(
            defaultValue: '8.0.30',
            description: 'PXB version for test',
            name: 'PXB_VERSION'
        )
        string(
            defaultValue: '2.4.4',
            description: 'Proxysql version for test',
            name: 'PROXYSQL_VERSION'
        )
        string(
            defaultValue: '2.5.10',
            description: 'HAProxy version for test',
            name: 'HAPROXY_VERSION'
        )
        string(
            defaultValue: '3.5.0',
            description: 'Percona toolkit version for test',
            name: 'PT_VERSION'
        )
        string(
            defaultValue: '1.0',
            description: 'replication-manager.sh version',
            name: 'REPL_MANAGER_VERSION'
        )
        string(
            defaultValue: 'master',
            description: 'Branch for testing repository',
            name: 'TESTING_BRANCH'
        )
  }
  options {
          withCredentials(moleculePdpxcJenkinsCreds())
          disableConcurrentBuilds()
  }
  stages {
        stage ('Test install: minor repo') {
            when {
                expression { env.TO_REPO != 'release' }
            }
            steps {
                script {
                    try {
                        build job: 'pdpxc', parameters: [
                        string(name: 'PLATFORM', value: "${env.PLATFORM}"),
                        string(name: 'REPO', value: "${env.TO_REPO}"),
                        string(name: 'VERSION', value: "${env.VERSION}"),
                        string(name: 'TESTING_BRANCH', value: "${env.TESTING_BRANCH}"),
                        string(name: 'SCENARIO', value: "pdpxc"),
                        string(name: 'PROXYSQL_VERSION', value: "${env.PROXYSQL_VERSION}"),
                        string(name: 'PXB_VERSION', value: "${env.PXB_VERSION}"),
                        string(name: 'PT_VERSION', value: "${env.PT_VERSION}"),
                        string(name: 'HAPROXY_VERSION', value: "${env.HAPROXY_VERSION}"),
                        string(name: 'REPL_MANAGER_VERSION', value: "${env.REPL_MANAGER_VERSION}"),
                        booleanParam(name: 'MAJOR_REPO', value: false)
                        ]
                    }
                    catch (err) {
                        currentBuild.result = "FAILURE"
                        echo "Stage 'Test install' failed, but we continue"
                    }
                }
            }
        }
        stage ('Test setup: minor repo') {
            when {
                expression { env.TO_REPO == 'release' }
            }
            steps {
                script {
                    try {
                        build job: 'pdpxc', parameters: [
                        string(name: 'PLATFORM', value: "${env.PLATFORM}"),
                        string(name: 'REPO', value: "${env.TO_REPO}"),
                        string(name: 'VERSION', value: "${env.VERSION}"),
                        string(name: 'TESTING_BRANCH', value: "${env.TESTING_BRANCH}"),
                        string(name: 'SCENARIO', value: "pdpxc_setup"),
                        string(name: 'PROXYSQL_VERSION', value: "${env.PROXYSQL_VERSION}"),
                        string(name: 'PXB_VERSION', value: "${env.PXB_VERSION}"),
                        string(name: 'PT_VERSION', value: "${env.PT_VERSION}"),
                        string(name: 'HAPROXY_VERSION', value: "${env.HAPROXY_VERSION}"),
                        string(name: 'REPL_MANAGER_VERSION', value: "${env.REPL_MANAGER_VERSION}"),
                        booleanParam(name: 'MAJOR_REPO', value: false)
                        ]
                    }
                    catch (err) {
                        currentBuild.result = "FAILURE"
                        echo "Stage 'Test setup' failed, but we continue"
                    }
                }
            }
        }
        stage ('Test setup: major repo') {
            when {
                expression { env.TO_REPO == 'release' }
            }
            steps {
                script {
                    try {
                        build job: 'pdpxc', parameters: [
                        string(name: 'PLATFORM', value: "${env.PLATFORM}"),
                        string(name: 'REPO', value: "${env.TO_REPO}"),
                        string(name: 'VERSION', value: "${env.VERSION}"),
                        string(name: 'TESTING_BRANCH', value: "${env.TESTING_BRANCH}"),
                        string(name: 'SCENARIO', value: "pdpxc_setup"),
                        string(name: 'PROXYSQL_VERSION', value: "${env.PROXYSQL_VERSION}"),
                        string(name: 'PXB_VERSION', value: "${env.PXB_VERSION}"),
                        string(name: 'PT_VERSION', value: "${env.PT_VERSION}"),
                        string(name: 'HAPROXY_VERSION', value: "${env.HAPROXY_VERSION}"),
                        string(name: 'REPL_MANAGER_VERSION', value: "${env.REPL_MANAGER_VERSION}"),
                        booleanParam(name: 'MAJOR_REPO', value: true)
                        ]
                    }
                    catch (err) {
                        currentBuild.result = "FAILURE"
                        echo "Stage 'Test setup' failed, but we continue"
                    }
                }
            }
        }
        stage ('Test minor upgrade') {
            when {
                expression { env.TO_REPO != 'release' }
            }
            steps {
                script {
                    try {
                        build job: 'pdpxc-upgrade', parameters: [
                        string(name: 'PLATFORM', value: "${env.PLATFORM}"),
                        string(name: 'FROM_REPO', value: "${env.FROM_REPO}"),
                        string(name: 'FROM_VERSION', value: "${env.FROM_VERSION}"),
                        string(name: 'TO_REPO', value: "${env.TO_REPO}"),
                        string(name: 'VERSION', value: "${env.VERSION}"),
                        string(name: 'TESTING_BRANCH', value: "${env.TESTING_BRANCH}"),
                        string(name: 'PROXYSQL_VERSION', value: "${env.PROXYSQL_VERSION}"),
                        string(name: 'PXB_VERSION', value: "${env.PXB_VERSION}"),
                        string(name: 'PT_VERSION', value: "${env.PT_VERSION}"),
                        string(name: 'HAPROXY_VERSION', value: "${env.HAPROXY_VERSION}"),
                        string(name: 'REPL_MANAGER_VERSION', value: "${env.REPL_MANAGER_VERSION}"),
                        ]
                    }
                    catch (err) {
                        currentBuild.result = "FAILURE"
                        echo "Stage 'Test minor upgrade' failed, but we continue"
                    }
                }
            }
        }
        stage ('Test haproxy') {
            when {
                expression { env.TO_REPO != 'release' }
            }
            steps {
                script {
                    try {
                        build job: 'haproxy', parameters: [
                        string(name: 'REPO', value: "${env.TO_REPO}"),
                        string(name: 'VERSION', value: "${env.VERSION}"),
                        string(name: 'TESTING_BRANCH', value: "${env.TESTING_BRANCH}"),
                        ]
                    }
                    catch (err) {
                        currentBuild.result = "FAILURE"
                        echo "Stage 'haproxy' failed, but we continue"
                    }
                }
            }
        }
  }
}
