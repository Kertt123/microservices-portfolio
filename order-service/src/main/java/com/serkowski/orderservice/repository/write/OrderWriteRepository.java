package com.serkowski.orderservice.repository.write;

import com.serkowski.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderWriteRepository extends JpaRepository<Order, Long> {

    void deleteByOrderNumber(String orderNumber);
}
