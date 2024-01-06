package com.example.rag;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RagApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RagApplication.class)
                .headless(false)
                .run(args);
    }

}
