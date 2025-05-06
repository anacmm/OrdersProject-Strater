package com.launchpad.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchpad.orders.dto.OrderDTO;
import com.launchpad.orders.model.Order;
import com.launchpad.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // assumes you're using a test DB config
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private Order savedOrder;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll(); // clean slate

        savedOrder = orderRepository.save(
                Order.builder()
                        .customerName("Alice")
                        .product("Widget")
                        .quantity(2)
                        .price(new BigDecimal("19.99"))
                        .build()
        );

    }

    @Test
    void getAll_shouldReturnListOfOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Alice"));
    }

    @Test
    void getOne_shouldReturnOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", savedOrder.getId()))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString())) // debug
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product").value("Widget"));
    }

    @Test
    void create_shouldPersistAndReturnOrder() throws Exception {
        OrderDTO newOrder = new OrderDTO(null, "Bob", "Gadget", 1, new BigDecimal("49.99"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Bob"));
    }

    @Test
    void update_shouldModifyOrder() throws Exception {
        OrderDTO updated = new OrderDTO(savedOrder.getId(), "Alice", "Updated Widget", 3, new BigDecimal("25.0"));

        mockMvc.perform(put("/api/orders/{id}", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product").value("Updated Widget"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", savedOrder.getId()))
                .andExpect(status().isNoContent());
    }

}
