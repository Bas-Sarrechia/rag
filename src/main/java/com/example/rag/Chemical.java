package com.example.rag;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record Chemical(String productName, String chemicalName, List<String> functions, String bananaColor) {
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
