package com.launchpad.orders.service;

import com.launchpad.orders.dto.OrderDTO;
import com.launchpad.orders.mapper.OrderMapper;
import com.launchpad.orders.model.Order;
import com.launchpad.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repo;

    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderService service;

    private Order order;
    private OrderDTO dto;

    @BeforeEach
    void setUp() {
        order = new Order(1L, "John", "Widget", 2, new BigDecimal("99.99"));
        dto = new OrderDTO(1L, "John", "Widget", 2, new BigDecimal("99.99"));
    }

    @Test
    void findAll_shouldReturnListOfDTOs() {
        when(repo.findAll()).thenReturn(Arrays.asList(order));
        when(mapper.toDTO(order)).thenReturn(dto);

        List<OrderDTO> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void findById_shouldReturnDTO_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(order));
        when(mapper.toDTO(order)).thenReturn(dto);

        OrderDTO result = service.findById(1L);

        assertEquals(dto, result);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_shouldReturnSavedDTO() {
        when(mapper.toEntity(dto)).thenReturn(order);
        when(repo.save(order)).thenReturn(order);
        when(mapper.toDTO(order)).thenReturn(dto);

        OrderDTO result = service.create(dto);

        assertEquals(dto, result);
    }

    @Test
    void update_shouldReturnUpdatedDTO_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(order));
        when(repo.save(order)).thenReturn(order);
        when(mapper.toDTO(order)).thenReturn(dto);

        OrderDTO result = service.update(1L, dto);

        assertEquals(dto, result);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(1L, dto));
    }

    @Test
    void delete_shouldCallDelete_whenExists() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(repo.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.delete(1L));
    }
}