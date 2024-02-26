package com.example.rag;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record Chemical(String productName,
                       String chemicalName,
                       String producer,
                       List<String> industries,
                       List<String> functions) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
