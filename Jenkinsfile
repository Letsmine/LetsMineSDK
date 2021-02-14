pipeline {
	environment {
		JAVA_TOOL_OPTIONS = '-Duser.home=/var/maven'
	}
	agent {
		docker {
			image 'maven:3.6.3-openjdk-15'
			args '-v /var/docker/jenkins/maven:/var/maven' 
		}
	}
	stages {
		stage('Build') { 
			steps {
				sh 'mvn -DskipTests clean install package' 
			}
		}
	}
	post {
		always {
			archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
		}
	}
}