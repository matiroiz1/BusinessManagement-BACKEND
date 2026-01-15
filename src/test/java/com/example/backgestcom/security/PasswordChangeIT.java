package com.example.backgestcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordChangeIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String login(String username, String password) throws Exception {
        String loginJson = """
      {"username":"%s","password":"%s"}
    """.formatted(username, password);

        String response = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn().getResponse().getContentAsString();

        return om.readTree(response).get("accessToken").asText();
    }

    @Test
    void changePassword_invalidatesOldPassword() throws Exception {
        String token = login("admin", "Admin#2026!");

        String body = """
    {"currentPassword":"Admin#2026!","newPassword":"Admin#2026!!"}
  """;

        mvc.perform(patch("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin#2026!\"}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin#2026!!\"}"))
                .andExpect(status().isOk());
    }

}
