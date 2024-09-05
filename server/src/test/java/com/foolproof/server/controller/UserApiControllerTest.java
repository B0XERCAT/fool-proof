package com.foolproof.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foolproof.global.domain.User;
import com.foolproof.global.dto.UserDTO;
import com.foolproof.global.repository.UserRepository;

import static org.springframework.test.web.servlet.result.MockMvcBuilders.status;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcBuilders.post;
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("addUser: Succeed adding user.")
    @Test
    public void addUser() throws Exception {
        // Given
        final String url = "/test/user/add";
        final String username = "testUser";
        final String password = "testPass";
        final UserDTO userRequest = new UserDTO(username, password);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // When
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // Then
        result.addExpect(status().isCreated());

        List<User> users = userRepository.findAll();

        assertThat(users.size()).isEqualTo(1);
        User firstUser = users.get(0);
        assertThat(firstUser.getUsername()).isEqualTo(username);
        assertThat(firstUser.getPassword()).isEqualTo(password);
    }
}
