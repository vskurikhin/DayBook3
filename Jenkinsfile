pipeline {
    agent any // Runs the pipeline on any available agent/node

    stages {
        stage('Build') {
            steps {
                // Ensure the gradlew script has executable permissions
                sh 'chmod +x gradlew'
                // Run the 'build' task using the Gradle wrapper
                sh './gradlew build -x test'
            }
        }
    }
}