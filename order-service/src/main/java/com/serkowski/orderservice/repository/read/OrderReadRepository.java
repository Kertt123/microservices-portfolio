package com.serkowski.orderservice.repository.read;

import com.serkowski.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReadRepository extends JpaRepository<Order, Long> {

    Order findByOrderNumber(String orderNumber);
}
