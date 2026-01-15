package com.example.backgestcom.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogSecurityIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String loginAndGetToken() throws Exception {
        String loginJson = """
      {"username":"admin","password":"Admin#2026!"}
    """;

        String response = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return om.readTree(response).get("accessToken").asText();
    }

    @Test
    void products_requiresAuth() throws Exception {
        mvc.perform(get("/api/catalog/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void products_allowsAdminToken() throws Exception {
        String token = loginAndGetToken();

        mvc.perform(get("/api/catalog/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
