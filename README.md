# Task-Mgmt
This simple Task Management application supports the creation, retrieval, deletion, and update of tasks.

# Task
## Task contains the following fields:
* id
* createdAt
* updatedAt
* dueDate
* resolvedAt
* title
* description
* priority (LOW, MEDIUM, HIGH)
* status (NOT_STARTED, IN_PROGRESS, BLOCKED, COMPLETED)

## Constraints:
* Tasks cannot share the same title.
* DueDate field cannot come before createdAt.
* ResolvedAt is autofilled when the status is set to COMPLETED.
* Field updatedAt is automatically updated with each successful task update (PUT).

Upon creation request, (see Create a new Task in REST API) filled title, description, dueDate, and priority are required. The remaining fields are autofielded or kept empty. The request will return the generated id of the task. 

# Task Scheduler

Task generation scheduler generates template task every ${DELAY} milliseconds. The default time interval is 10 seconds but it can be changed through the env variable in docker-compose or by setting the DELAY env variable if run locally.

# Tests
The source code contains unit tests for the service layer and integration tests for the controller layer. Both can be run either directly in IntelliJ or using `mvn test`. For integration tests embedded database is used for simplicity, since there are no custom queries in TaskRepository.

# Run

The repository contains a .jar file in the docker directory, as well as docker-compose.yml. It is up to the user to decide how to set up a running environment. However, the simplest and recommended way is to run `docker-compose up` in the docker directory which will run the app and the Postgres database.

If running .jar locally, environment variables DB_USER, DB_PW, and DELAY have to be set properly.

# REST API

The REST API of the Task Management app is described below.

## Get the list of Tasks

### Request

`GET api/tasks`

    curl --location 'localhost:8080/api/tasks'

### Response

    []

## Get a single Task

### Request

`GET api/tasks/{id}`

    curl --location 'localhost:8080/api/tasks/1'

### Response

    {"id":2,"createdAt":"2023-10-11","updatedAt":null,"dueDate":"2023-12-01","resolvedAt":null,"title":"Task 2","description":"Task 2 is a test task","priority":"MEDIUM","status":"NOT_STARTED"}

## Create a new Task

### Request

`POST api/tasks`

    curl --location 'localhost:8080/api/tasks' --header 'Content-Type: application/json' --data '{ \
        "title": "Third task",
        "description": "This is third test task",
        "priority": "HIGH",
        "dueDate": "2024-03-01"
        }'

### Response

    {id}

## Delete a Task

### Request

`DELETE api/tasks/{id}`

    curl --location --request DELETE 'localhost:8080/api/tasks/{id}'

## Update a Task

### Request

`PUT api/tasks/{id}?{attr}={val}`

    curl --location --request PUT 'localhost:8080/api/tasks/56?priority=MEDIUM&status=COMPLETED'
