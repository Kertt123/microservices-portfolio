package com.serkowski.orderservice.repository.read;

import com.serkowski.orderservice.model.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderReadRepository extends JpaRepository<OrderSummary, Long> {

    Optional<OrderSummary> findByOrderNumberAndVersion(String orderNumber, Integer version);
    Optional<OrderSummary> findByOrderNumber(String orderNumber);
}
