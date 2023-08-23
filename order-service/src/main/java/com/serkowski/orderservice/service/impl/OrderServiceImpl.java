package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.repository.read.OrderReadRepository;
import com.serkowski.orderservice.repository.write.OrderWriteRepository;
import com.serkowski.orderservice.service.api.OrderMapper;
import com.serkowski.orderservice.service.api.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderWriteRepository orderWriteRepository;
    private final OrderReadRepository orderReadRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse placeOrderDraft(OrderRequest orderRequest) {
        OrderSummary orderSummary = orderWriteRepository.save(orderMapper.map(orderRequest, State.DRAFT));

        return orderMapper.map(orderSummary);
    }

    @Override
    public OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber) {
        return orderReadRepository.findByOrderNumber(orderNumber)
                .map(orderSummary -> {
                    orderSummary.setOrderLineItemsList(orderMapper.mapItems(orderRequest.getOrderItems()));
                    orderSummary.setAddress(orderMapper.mapAddress(orderRequest.getAddressDto()));
                    return orderMapper.map(orderWriteRepository.save(orderSummary));
                })
                .orElseThrow(() -> new OrderNotFound("Can't update order which is not exist for number: " + orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        return orderReadRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::map)
                .orElseThrow(() -> new OrderNotFound("Order which number: " + orderNumber + " not exist"));
    }

    @Override
    public void deleteOrderByOrderNumber(String orderNumber) {
        orderReadRepository.findByOrderNumber(orderNumber)
                .ifPresentOrElse(order -> orderWriteRepository.delete(order), () -> {
                    throw new OrderNotFound("Order which number: " + orderNumber + " not exist, so can't be deleted");
                });
    }
}