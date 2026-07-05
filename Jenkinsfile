pipeline {
    agent any // Runs the pipeline on any available agent/node

    stages {
        stage('Build') {
            steps {
                script {
                    println "Build number: ${env.BUILD_NUMBER}"
                }
                sh 'cd auth ; touch .env ; make'

                // Ensure the gradlew script has executable permissions
                sh 'chmod +x gradlew'

                // Run the 'build' task using the Gradle wrapper
                sh '''
                    ./gradlew build -xtest -Dquarkus.package.jar.enabled=false -Dquarkus.native.enabled=true
                '''
            }
        }

        stage('Test') {
            steps {
                // Run the 'test' task specifically, if desired, though 'build' often includes it
                sh './gradlew test'
            }
            // Optional: Archive test results (e.g., JUnit format)
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        // Add more stages like 'Deploy', 'Staging', etc., as needed
        stage('Staging artifacts') {
            steps {
                script {
                    VERSION = sh(
                        script: './gradlew properties -q | grep "^version:" | awk \'{print $2}\'',
                        returnStdout: true
                    ).trim()
                }
                sh """
                    mkdir -p distributive-${VERSION}/api/build/resources/main/db/changelog/Change-Sets
                    mkdir -p distributive-${VERSION}/auth/cmd/server/cmd/migrations
                    mkdir -p distributive-${VERSION}/core/build/resources/main/db/changelog/Change-Sets
                    mkdir -p distributive-${VERSION}/core/build/libs
                    mkdir -p distributive-${VERSION}/app/html/assets
                    cp api/src/main/resources/Dockerfile distributive-${VERSION}/api/
                    cp api/build/api-*-runner distributive-${VERSION}/api/build/
                    cp api/src/main/resources/application.properties distributive-${VERSION}/api/build/resources/main/
                    cp api/src/main/resources/db/changelog/Change-Log.xml distributive-${VERSION}/api/build/resources/main/db/changelog/
                    cp api/src/main/resources/db/changelog/Change-Sets/* distributive-${VERSION}/api/build/resources/main/db/changelog/Change-Sets/
                    cp auth/Dockerfile distributive-${VERSION}/auth/
                    cp auth/cmd/server/auth-server distributive-${VERSION}/auth/cmd/server/
                    cp auth/cmd/server/cfg/.auth-server.yaml distributive-${VERSION}/auth/
                    cp auth/cmd/server/cmd/migrations/* distributive-${VERSION}/auth/cmd/server/cmd/migrations/
                    cp core/src/main/resources/Dockerfile distributive-${VERSION}/core/
                    cp core/build/libs/core-*.jar distributive-${VERSION}/core/build/libs/
                    rm -f distributive-${VERSION}/core/build/libs/core-*-plain.jar
                    cp core/src/main/resources/application.yaml distributive-${VERSION}/core/build/resources/main/
                    cp core/src/main/resources/db/changelog/Change-Log.xml distributive-${VERSION}/core/build/resources/main/db/changelog/
                    cp core/src/main/resources/db/changelog/Change-Sets/* distributive-${VERSION}/core/build/resources/main/db/changelog/Change-Sets/
                    cp app/Dockerfile distributive-${VERSION}/app/
                    cp app/nginx.conf distributive-${VERSION}/app/
                    cp app/html/index.html distributive-${VERSION}/app/html/
                    cp app/html/vite.svg distributive-${VERSION}/app/html/
                    cp app/html/assets/* distributive-${VERSION}/app/html/assets/
                """
                sh "tar czvf distributive-${VERSION}-${env.BUILD_NUMBER}.tar.gz distributive-${VERSION}"
                archiveArtifacts artifacts: "distributive-${VERSION}-${env.BUILD_NUMBER}.tar.gz", fingerprint: true

                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    passwordVariable: 'DOCKER_PASS',
                    usernameVariable: 'DOCKER_USER')]) {
                    sh """
                    cd distributive-${VERSION}/api/
                    docker build -f Dockerfile -t $DOCKER_USER/api:latest .
                    cd ../auth
                    docker build -f Dockerfile -t $DOCKER_USER/auth:latest .
                    cd ../core
                    docker build -f Dockerfile -t $DOCKER_USER/core:latest .
                    cd ../app
                    docker build -f Dockerfile -t $DOCKER_USER/app:latest .
                """
                }
            }
        }

        stage('Staging publish artifacts') {
            steps {
                script {
                    VERSION = sh(
                        script: './gradlew properties -q | grep "^version:" | awk \'{print $2}\'',
                        returnStdout: true
                    ).trim()
                }
                // Binds Jenkins secrets to environment variables securely
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    passwordVariable: 'DOCKER_PASS',
                    usernameVariable: 'DOCKER_USER')]) {

                    // Secure CLI login line
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh """
                        docker push $DOCKER_USER/api:latest
                        docker push $DOCKER_USER/app:latest
                        docker push $DOCKER_USER/auth:latest
                        docker push $DOCKER_USER/core:latest
                    """
                }
            }
        }
    }
}