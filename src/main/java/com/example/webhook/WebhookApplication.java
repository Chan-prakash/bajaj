package com.example.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.webhook.service.WebhookService;


@SpringBootApplication
public class WebhookApplication implements CommandLineRunner {

    @Autowired
    private WebhookService webhookService;

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        
        webhookService.processWebhook();
    }
}
