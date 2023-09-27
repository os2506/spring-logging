package com.dproduction.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testConcurrentRequests() throws Exception {
        int numThreads = 10;
        int numRequestsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < numRequestsPerThread; j++) {
                    restTemplate.getForEntity("http://localhost:" + port + "/users/", String.class);
                }
                latch.countDown();
            });
        }

        // Wait for all threads to finish
        latch.await();
        executorService.shutdown();
    }
}
