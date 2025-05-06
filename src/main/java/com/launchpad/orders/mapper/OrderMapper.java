package com.launchpad.orders.mapper;

import com.launchpad.orders.dto.OrderDTO;
import com.launchpad.orders.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    default Order toEntity(OrderDTO dto) {
        if (dto == null) return null;

        return Order.builder()
                .id(dto.id())
                .customerName(dto.customerName())
                .product(dto.product())
                .quantity(dto.quantity())
                .price(dto.price())
                .build();
    }
}

