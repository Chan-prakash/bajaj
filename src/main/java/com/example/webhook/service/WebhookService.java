package com.example.webhook.service;

import com.example.webhook.model.User;
import com.example.webhook.model.WebhookData;
import com.example.webhook.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    @Autowired
    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void processWebhook() {
        String url = "https://example.com/api"; // Replace with actual endpoint

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("registrationNumber", "AB123CD");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<WebhookResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, WebhookResponse.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                WebhookResponse response = responseEntity.getBody();
                System.out.println("Received response: " + response.getMessage());
            } else {
                System.out.println("Failed with status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Just simulating the use of the User class
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Chandrakanth", 100));
        System.out.println("User list size: " + users.size());
    }
}
