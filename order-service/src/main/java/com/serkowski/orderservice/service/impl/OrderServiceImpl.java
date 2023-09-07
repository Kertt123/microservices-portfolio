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
import com.serkowski.orderservice.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderWriteRepository orderWriteRepository;
    private final OrderReadRepository orderReadRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;

    @Override
    public Mono<OrderResponse> placeOrderDraft(OrderRequest orderRequest) {
        OrderSummary orderSummary = orderWriteRepository.save(orderMapper.map(orderRequest, State.DRAFT));

        return productService.reserveItems(orderSummary.getOrderNumber(), orderRequest.getOrderItems())
                .doOnError(exception -> {
                    log.error("Reserve items fail", exception);
                    orderWriteRepository.delete(orderSummary);
                })
                .map(result -> {
                    log.info("Response of reserve items: " + result);
                    return orderMapper.map(orderSummary);
                });
    }

    @Override
    public OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber, Integer versionNumber) {
        return orderReadRepository.findByOrderNumberAndVersion(orderNumber, versionNumber)
                .map(orderSummary -> {
                    orderSummary.setOrderLineItemsList(orderMapper.mapItems(orderRequest.getOrderItems()));
                    orderSummary.setAddress(orderMapper.mapAddress(orderRequest.getAddress()));
                    return orderMapper.map(orderWriteRepository.save(orderSummary));
                })
                .orElseThrow(() -> new OrderNotFound(String.format("Can't update order which is not exist for number: %s and version %d", orderNumber, versionNumber)));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber, Integer versionNumber) {
        return orderReadRepository.findByOrderNumberAndVersion(orderNumber, versionNumber)
                .map(orderMapper::map)
                .orElseThrow(() -> new OrderNotFound(String.format("Order which number: %s and version %d not exist", orderNumber, versionNumber)));
    }

    @Override
    public void deleteOrderByOrderNumber(String orderNumber) {
        orderReadRepository.findByOrderNumber(orderNumber)
                .ifPresentOrElse(orderWriteRepository::delete, () -> {
                    throw new OrderNotFound("Order which number: " + orderNumber + " not exist, so can't be deleted");
                });
    }
}
