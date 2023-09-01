package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductItemServiceImpl implements ProductItemService {

    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;
    private final ProductItemReadRepository productItemReadRepository;
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
                    Optional.ofNullable(product.getItems())
                            .ifPresentOrElse(productItems -> productItems.add(item), () -> product.setItems(List.of(item)));
                    try {
                        ProductItem productItem = productItemWriteRepository.save(item);
                        productWriteRepository.save(product);
                        return productItem;
                    } catch (DuplicateKeyException e) {
                        throw new AddItemIndexException("Product with serial number: " + productItemRequest.getSerialNumber() + " already exist");
                    }
                })
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product which id: " + productId + " not exist"));
    }

    @Override
    public ProductItemDto getItemById(String productItemId) {
        return productItemReadRepository.findById(productItemId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product item which id: " + productItemId + " not exist"));
    }

    @Override
    public void reserveItems(ReserveItemsDto reserveItemsDto) {
        List<ProductItem> reservedItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(reserveItemsDto.getIds())) {
            reservedItems.addAll(productItemReadRepository.findByIds(reserveItemsDto.getIds())
                    .stream()
                    .map(this::markItemAsReserved)
                    .toList());
        }
        if (!CollectionUtils.isEmpty(reserveItemsDto.getSerialNumbers())) {
            reservedItems.addAll(productItemReadRepository.findBySerialNumbers(reserveItemsDto.getSerialNumbers())
                    .stream()
                    .map(this::markItemAsReserved)
                    .toList());
        }
        if (CollectionUtils.isEmpty(reservedItems)) {
            throw new ReservationItemsException("To reserve the products the ids or serial numbers need to be provided");
        }
        productItemWriteRepository.saveAll(reservedItems);
    }

    private ProductItem markItemAsReserved(ProductItem productItem) {
        if (Availability.RESERVED == productItem.getAvailability()) {
            throw new ReservationItemsException("The product item with serial number: " + productItem.getSerialNumber() + " is already reserved");
        }
        productItem.setAvailability(Availability.RESERVED);
        productItem.setReservationTimeDate(LocalDateTime.now());
        return productItem;
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
