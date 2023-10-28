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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    private final LocalDateTime dueDate;

    private final long MinutesElapsedSinceCreated;

    @Autowired
    public TaskControllerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;

        this.dueDate = LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.MINUTES);
        this.MinutesElapsedSinceCreated = 2;
        this.exampleTask = new Task(
                "Example Task",
                "Example Task is a test task",
                Task.PriorityLevel.LOW,
                dueDate);
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
        assertEquals(this.dueDate, LocalDateTime.parse(task1Json.get("dueDate").toString()).truncatedTo(ChronoUnit.MINUTES));
        assertEquals(this.dueDate, LocalDateTime.parse(task2Json.get("dueDate").toString()).truncatedTo(ChronoUnit.MINUTES));
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
        assertEquals(this.dueDate, LocalDateTime.parse(responseJson.get("dueDate").toString()).truncatedTo(ChronoUnit.MINUTES));
        long diff = Duration.between(LocalDateTime.now(), LocalDateTime.parse(responseJson.get("createdAt").toString())).toMinutes();
        assertTrue(diff <= this.MinutesElapsedSinceCreated);
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
                        .queryParam("dueDate", this.dueDate.plusDays(2).toString()))
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
        assertEquals(this.dueDate.plusDays(2), LocalDateTime.parse(responseJson.get("dueDate").toString()).truncatedTo(ChronoUnit.MINUTES));
        long diff = Duration.between(LocalDateTime.now(), LocalDateTime.parse(responseJson.get("updatedAt").toString())).toMinutes();
        assertTrue(diff <= 1);
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
                        .queryParam("dueDate", this.dueDate.minusMonths(2).toString()))
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
        long diff = Duration.between(LocalDateTime.now(), LocalDateTime.parse(responseJson.get("resolvedAt").toString())).toMinutes();
        assertTrue(diff <= 1);
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
                "dueDate": "2024-03-01T05:05" \n
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
