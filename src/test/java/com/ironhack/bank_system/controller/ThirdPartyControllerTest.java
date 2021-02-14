package com.ironhack.bank_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.StoredProcedureParameter;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ThirdPartyControllerTest {
  
  @Autowired
  private WebApplicationContext webApplicationContext;
  
  private MockMvc mockMvc;
  private ObjectMapper objectMapper = new ObjectMapper();
  
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
  
  @AfterEach
  void tearDown() {
  }
  
  @Test
  void findAll() {
  }
  
  @Test
  void findThirdPartyById() {
  }
  
  @Test
  void createAddress() {
  }
  
  @Test
  void updateThirdParty() {
  }
  
  @Test
  void deleteAddress() {
  }
}