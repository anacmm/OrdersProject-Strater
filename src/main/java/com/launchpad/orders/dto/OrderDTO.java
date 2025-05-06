package com.launchpad.orders.dto;

import java.math.BigDecimal;

public record OrderDTO(
        Long id,
        String customerName,
        String product,
        Integer quantity,
        BigDecimal price
) {
}