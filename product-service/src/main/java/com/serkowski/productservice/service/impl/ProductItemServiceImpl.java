package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductItemServiceImpl implements ProductItemService {

    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;
    private final ProductItemWriteRepository productItemWriteRepository;

    @Override
    public ProductItemDto addItem(String productId, ProductItemDto productItemRequest) {
        return productReadRepository.findById(productId)
                .map(product -> {
                    ProductItem item = ProductItem.builder()
                            .id(UUID.randomUUID().toString())
                            .availability(Availability.AVAILABLE)
                            .serialNumber(productItemRequest.getSerialNumber())
                            .updateDate(LocalDateTime.now())
                            .build();
                    if (product.getItems() == null) {
                        product.setItems(List.of(item));
                    } else {
                        product.getItems().add(item);
                    }
                    ProductItem productItem = productItemWriteRepository.save(item);
                    productWriteRepository.save(product);
                    return productItem;
                })
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product which id: " + productId + " not exist"));
    }

    private ProductItemDto mapToDto(ProductItem productItem) {
        return ProductItemDto.builder()
                .id(UUID.fromString(productItem.getId()))
                .serialNumber(productItem.getSerialNumber())
                .availability(productItem.getAvailability().toString())
                .updateDate(productItem.getUpdateDate())
                .reservationTimeDate(productItem.getReservationTimeDate())
                .build();
    }
}
