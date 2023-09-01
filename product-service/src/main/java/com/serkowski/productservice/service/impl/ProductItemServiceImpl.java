package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItem;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
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
        reserveItemsDto.getItems()
                .forEach(reserveItem -> productReadRepository.findById(reserveItem.getItemRef())
                        .ifPresent(product -> reservedItems.addAll(getProductItems(reserveItem, product))));
        if (CollectionUtils.isEmpty(reservedItems)) {
            throw new ReservationItemsException("Reservation list is empty because of product not found or empty items list");
        }

        productItemWriteRepository.saveAll(reservedItems.stream().map(item -> markItemAsReserved(item, reserveItemsDto.getOrderNumber())).toList());
    }

    private ProductItem markItemAsReserved(ProductItem productItem, String orderNumber) {
        productItem.setAvailability(Availability.RESERVED);
        productItem.setReservationTimeDate(LocalDateTime.now());
        productItem.setReservationOrderNumber(orderNumber);
        return productItem;
    }

    private List<ProductItem> getProductItems(ReserveItem reserveItem, Product product) {
        List<ProductItem> list = product.getItems().stream()
                .filter(productItem -> Availability.AVAILABLE == productItem.getAvailability())
                .limit(reserveItem.getCount())
                .toList();
        if (list.size() < reserveItem.getCount()) {
            throw new ReservationItemsException("The amount of the available products is not enough to make a full reservation");
        }
        return list;
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
