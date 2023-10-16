package com.craftworks.TaskManagementSystem.Task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TaskControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final Task exampleTask;

    @Autowired
    public TaskControllerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.exampleTask = new Task(
                "Example Task",
                "Example Task is a test task",
                Task.PriorityLevel.LOW,
                LocalDate.of(2023, Month.DECEMBER, 2));
    }

    private String getNewTaskBody(Task task) {
        return "{ \"title\": \"" +
                task.getTitle() +
                "\", \"description\": \"" +
                task.getDescription() +
                "\", \"priority\": \"" +
                task.getPriority() +
                "\", \"dueDate\": \"" +
                task.getDueDate().toString() +
                "\" }";
    }

    @Test
    @Order(1)
    public void test_addNewTasks() throws Exception {
        String body = getNewTaskBody(this.exampleTask);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.exampleTask.setTitle("Second Example Task");
        body = getNewTaskBody(this.exampleTask);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.exampleTask.setTitle("Third Example Task");
        body = getNewTaskBody(this.exampleTask);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void test_getTasks() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        JSONArray jsonArray = new JSONArray(responseStr);
        assertEquals(3, jsonArray.length());
        JSONObject task1Json = jsonArray.getJSONObject(0);
        JSONObject task2Json = jsonArray.getJSONObject(1);

        assertEquals("Example Task", task1Json.get("title"));
        assertEquals("Second Example Task", task2Json.get("title"));
        assertEquals("Example Task is a test task", task1Json.get("description"));
        assertEquals("Example Task is a test task", task2Json.get("description"));
        assertEquals("LOW", task1Json.get("priority"));
        assertEquals("LOW", task2Json.get("priority"));
        assertEquals("2023-12-02", task1Json.get("dueDate"));
        assertEquals("2023-12-02", task2Json.get("dueDate"));

    }

    @Test
    @Order(3)
    public void test_getTask() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        JSONObject responseJson = new JSONObject(responseStr);

        assertEquals("Second Example Task", responseJson.get("title"));
        assertEquals("Example Task is a test task", responseJson.get("description"));
        assertEquals("LOW", responseJson.get("priority"));
        assertEquals("2023-12-02", responseJson.get("dueDate"));
        assertEquals(LocalDate.now().toString(), responseJson.get("createdAt"));
        assertEquals("NOT_STARTED", responseJson.get("status"));
    }

    @Test
    @Order(4)
    public void test_getTask_doesNotExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/2222")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void test_deleteTask() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void test_deleteTask_doesNotExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/333"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    public void test_updateTasks() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/2")
                        .queryParam("title", "New Title")
                        .queryParam("description", "New description")
                        .queryParam("priority", "MEDIUM")
                        .queryParam("dueDate", "2023-11-27"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        JSONObject responseJson = new JSONObject(responseStr);
        assertEquals("New Title", responseJson.get("title"));
        assertEquals("New description", responseJson.get("description"));
        assertEquals("MEDIUM", responseJson.get("priority"));
        assertEquals("2023-11-27", responseJson.get("dueDate"));
    }

    @Test
    @Order(8)
    public void test_updateTasks_wrongTitle() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/2")
                        .queryParam("title", ""))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException));
    }

    @Test
    @Order(9)
    public void test_updateTasks_existingTitle() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/3")
                        .queryParam("title", "New Title"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException));
    }

    @Test
    @Order(10)
    public void test_updateTasks_wrongDueDate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/2")
                        .queryParam("dueDate", LocalDate.of(2020, Month.DECEMBER, 1).toString()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException));
    }

    @Test
    @Order(11)
    public void test_addNewTask_badTitle() throws Exception {
        String body = """ 
                { \n
                "title": "", \n
                "description": "This is third test task", \n
                "priority": "HIGH", \n
                "dueDate": "2024-03-01" \n
                }""";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    public void test_addNewTask_badDueDate() throws Exception {
        String body = """ 
                { \n
                "title": "Bad Due Date Task", \n
                "description": "This is third test task", \n
                "priority": "HIGH", \n
                "dueDate": "2020-03-01" \n
                }""";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    public void test_addNewTask_existingTitle() throws Exception {
        String body = """ 
                { \n
                "title": "New Title", \n
                "description": "This is third test task", \n
                "priority": "HIGH", \n
                "dueDate": "2024-03-01" \n
                }""";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    public void test_updateTask_resolved() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/2")
                        .queryParam("status", "COMPLETED"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        JSONObject responseJson = new JSONObject(responseStr);
        assertEquals("COMPLETED", responseJson.get("status"));
        assertEquals(LocalDate.now().toString(), responseJson.get("resolvedAt"));
    }

    @Test
    @Order(15)
    public void test_addNewTask_null() throws Exception {
        String body = """ 
                { \n
                "title": "", \n
                "description": "This is third test task", \n
                "priority": "HIGH", \n
                "dueDate": "2024-03-01" \n
                }""";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(16)
    public void test_updateTasks_sameTaskSameTitle() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks/2")
                        .queryParam("title", "New Title"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(17)
    public void test_addNewTask_existingId() throws Exception {
        String body = """ 
                { \n
                "id": "1", \n
                "title": "Example Task renewed", \n
                "description": "This is third test task", \n
                "priority": "HIGH", \n
                "dueDate": "2024-03-01" \n
                }""";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        long newTaskId = Long.parseLong(result.getResponse().getContentAsString());
        assertNotEquals(1, newTaskId);

    }
}
