package com.launchpad.orders.service;

import com.launchpad.orders.dto.OrderDTO;
import com.launchpad.orders.mapper.OrderMapper;
import com.launchpad.orders.model.Order;
import com.launchpad.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
// This annotation tells Spring to
// “Wrap this method (or class) in a database transaction. If anything fails, roll it back.”
// If anything fails, the whole operation is rolled back to keep data consistent.
// Each public method in a database transaction.
// It ensures consistency across multiple operations (e.g., save/update/delete).
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repo;
    private final OrderMapper mapper;

    public List<OrderDTO> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public OrderDTO findById(Long id) {
        return repo.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Order " + id + " not found"));
    }

    public OrderDTO create(OrderDTO dto) {
        return mapper.toDTO(repo.save(mapper.toEntity(dto)));
    }

    public OrderDTO update(Long id, OrderDTO dto) {
        Order existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order " + id + " not found"));

        existing.updateFromDTO(dto);
        return mapper.toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        repo.deleteById(id);
    }
}
