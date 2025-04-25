package com.example.webhook.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@EnableRetry
public class WebhookService {

    private final String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
    private final String testWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook";

    public void processWebhook() {
        try {
            String requestBody = "{\"name\": \"John Doe\", \"regNo\": \"REG12347\", \"email\": \"john@example.com\"}";
            String responseBody = callGenerateWebhook(requestBody);
            
            JSONObject jsonResponse = new JSONObject(responseBody);
            String webhookUrl = jsonResponse.getString("webhook");
            String accessToken = jsonResponse.getString("accessToken");
            JSONArray users = jsonResponse.getJSONObject("data").getJSONArray("users");

            int regNoLastDigit = Integer.parseInt("12347".substring(7)) % 2;
            if (regNoLastDigit == 0) {  // Question 2 (Nth-Level Followers)
                JSONArray result = findNthLevelFollowers(users, 2, 1);  // Example for nth level followers
                sendResultToWebhook(webhookUrl, accessToken, result);
            } else {  // Question 1 (Mutual Followers)
                JSONArray result = findMutualFollowers(users);
                sendResultToWebhook(webhookUrl, accessToken, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String callGenerateWebhook(String requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(generateWebhookUrl, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private void sendResultToWebhook(String webhookUrl, String accessToken, JSONArray result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        JSONObject resultJson = new JSONObject();
        resultJson.put("outcome", result);

        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(4); 
        retryTemplate.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(5000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        HttpEntity<String> entity = new HttpEntity<>(resultJson.toString(), headers);

        try {
            retryTemplate.execute(context -> {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
                return null;
            });
        } catch (Exception e) {
            System.err.println("Failed to send result to webhook after retries: " + e.getMessage());
        }
    }

    private JSONArray findMutualFollowers(JSONArray users) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            int userId = user.getInt("id");
            JSONArray follows = user.getJSONArray("follows");

            for (int j = i + 1; j < users.length(); j++) {
                JSONObject otherUser = users.getJSONObject(j);
                int otherUserId = otherUser.getInt("id");

                if (follows.toList().contains(otherUserId) && otherUser.getJSONArray("follows").toList().contains(userId)) {
                    result.put(new JSONArray().put(Math.min(userId, otherUserId)).put(Math.max(userId, otherUserId)));
                }
            }
        }
        return result;
    }

    private JSONArray findNthLevelFollowers(JSONArray users, int n, int findId) {
        JSONArray result = new JSONArray();        
        return result;
    }
}
