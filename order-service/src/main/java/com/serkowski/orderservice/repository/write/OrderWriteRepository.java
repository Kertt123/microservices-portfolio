package com.serkowski.orderservice.repository.write;

import com.serkowski.orderservice.model.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderWriteRepository extends JpaRepository<OrderSummary, Long> {

    void deleteByOrderNumber(String orderNumber);
}
