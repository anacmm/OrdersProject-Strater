package com.launchpad.orders.model;

import com.launchpad.orders.dto.OrderDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA needs this
@AllArgsConstructor                               // Used by builder and tests
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String product;
    private Integer quantity;
    private BigDecimal price;

    public void updateFromDTO(OrderDTO dto) {
        this.customerName = dto.customerName();
        this.product = dto.product();
        this.quantity = dto.quantity();
        this.price = dto.price();
    }
}

