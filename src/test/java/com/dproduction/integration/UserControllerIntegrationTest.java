package com.dproduction.integration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.dproduction.entity.User;
import com.dproduction.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.dproduction.dtos.UserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import java.util.concurrent.locks.ReentrantLock;


////(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    
    @Test
    public void testRegisterUser() {
        // Créez un objet UserDTO pour votre test
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("john");
        userDTO.setPwd("password");
        userDTO.setEmail("john@example.com");
        
        //http://localhost:8080/users/register
        // Appelez la méthode register via une requête HTTP POST
        ResponseEntity<User> responseEntity = restTemplate.postForEntity(
            "http://localhost:" + port + "/users/register",
            userDTO,
            User.class
        );

        // Vérifiez que la réponse est un code de statut HTTP 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Vérifiez que la réponse contient les informations de l'utilisateur enregistré
        User responseUser = responseEntity.getBody();
        
        assertNotNull(responseUser);
    }
    
  
    
    @Test
    public void testGetAllUsers() {
    	
    	 // Send an HTTP GET request to the "/users" endpoint
        ResponseEntity<List> responseEntity = restTemplate.getForEntity(
            "http://localhost:" + port + "/users/",
            List.class
        );
        
        // Check the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Check the response body (list of users)
        List<User> users = responseEntity.getBody();
        // Perform assertions on the list of users as needed

        // For example, you can check if the list is not empty
        assertEquals(false, users.isEmpty());
          
    }
    
    
    @Test
    @Transactional
    public void testDeleteUser_ExistingUser() throws Exception {
        // Create a test user in the database
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPwd("password");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);

        // Send an HTTP DELETE request to delete the user
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));


        // Verify that the user is deleted
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteUser_NonExistingUser() throws Exception {
        // Send an HTTP DELETE request to delete a non-existing user
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 12345)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteUser_InvalidUserIdFormat() throws Exception {
        // Send an HTTP DELETE request with an invalid user ID format
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", "invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Additional test scenarios can be added here for security, concurrency, performance, and error handling.
    
    //concurrency
    @Test
    @Transactional
    public void testConcurrentDeleteRequests() throws Exception {
        // Create a test user in the database
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPwd("password");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);

        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    // Send concurrent HTTP DELETE requests to delete the user
                    mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8.name()))
                            .andExpect(MockMvcResultMatchers.status().isNoContent());

                    // Retrieve the user by ID immediately after deletion
                    mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8.name()))
                            .andExpect(MockMvcResultMatchers.status().isNotFound());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to finish
        latch.await();

        executorService.shutdown();
    }

}
