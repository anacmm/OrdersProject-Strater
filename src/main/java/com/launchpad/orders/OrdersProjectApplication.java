package com.launchpad.orders;

import com.launchpad.orders.model.Order;
import com.launchpad.orders.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class OrdersProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(OrderRepository repo) {
        return args -> repo.saveAll(List.of(
                new Order(null, "Alice", "Laptop", 1, new BigDecimal("999.99")),
                new Order(null, "Bob", "Monitor", 2, new BigDecimal("199.99"))
        ));
    }

}
