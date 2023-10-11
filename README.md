# Task-Mgmt
This simple Task Management application supports creation, retrival, delition and update of tasks.

# Task
## Task contains following fields:
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
* ResolvedAt is autofilled when status is set to COMPLETED.
* Field updatedAt is automatically updated with each successful task update (PUT).

Upon creation request (see Create a new Task in REST API) filled title, description, dueDate and priority are requiered. Remaining fields are autofield or kept empty. Request will return generated id of the task. 

# Tests
Source code contains unit tests for service layer and integration tests for controller layer. Both can be run either directly in IntelliJ or using `mvn test`. For integration tests embedded data base is used for the simplicty, since there are no custom queries in TaskRepository.

# Run

Repository contains .jar file in docker directory, as well as docker-compose.yml. It is up to user to decide how to setup running environment. However, the simplest and recommended way is to run `docker-compose up` in docker directory which will run the app and the postgres database.

# REST API

The REST API of Task Management app is described below.

## Get list of Tasks

### Request

`GET api/tasks`

    curl --location 'localhost:8080/api/tasks'

### Response

    []

## Get single Task

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
