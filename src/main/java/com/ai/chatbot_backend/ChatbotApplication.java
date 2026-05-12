package com.ai.chatbot_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotApplication {
	public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        // Set environment variables as system properties only if present
        setIfPresent(dotenv, "PORT");
        setIfPresent(dotenv, "DB_URL");
        setIfPresent(dotenv, "DB_USERNAME");
        setIfPresent(dotenv, "DB_PASSWORD");
        setIfPresent(dotenv, "GROQ_API_KEY");
        setIfPresent(dotenv, "GROQ_MODEL");


        SpringApplication.run(ChatbotApplication.class, args);
	}
    private static void setIfPresent(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null && !value.trim().isEmpty()) {
            System.setProperty(key, value);
        }
    }
}