# Code Coverage Report on SonarQube

## Run SonarQube locally on Docker

Make sure you have Docker installed and running on your machine.

1. **Navigate to the SonarQube docker-compose.yml file:**

    ```bash
    cd online-bookstore-api/dist/sonarqube
    ```

2. **Run docker-compose.yml:**

    ```bash
    docker compose up -d
    ```

   This command will start SonarQube in detached mode. You can check the logs to see if everything is running correctly:

    ```bash
    docker compose logs -f
    ```

3. **Access SonarQube:**

   Open your web browser and go to [http://localhost:9000](http://localhost:9000). The default credentials are:

  - **Username:** `admin`
  - **Password:** `admin`

4. **Create a local project:**

  - Go to the "Projects" tab and click on "Create Project > Local".
  - Enter a display name (e.g., `Online Bookstore`) and a project key (e.g., `online-bookstore`).
  - Click on "Next" to configure the project and generate a token.

5. **Generate and upload coverage data:**

  - Run the following command to generate the coverage report:

    ```bash
    ./mvnw clean verify sonar:sonar \
      -Dsonar.projectKey=Online-Bookstore \
      -Dsonar.projectName='Online Bookstore' \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=<your_token>
    ```

   Replace `<your_token>` with the token generated in the previous step.

6. **View the coverage report:**

  - Go back to the SonarQube dashboard and click on your project.
  - You should see the coverage report with various metrics, including code coverage, code smells, bugs, and
    vulnerabilities.
