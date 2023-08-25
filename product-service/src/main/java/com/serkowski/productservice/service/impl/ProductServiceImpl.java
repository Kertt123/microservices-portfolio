package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.ProductReadRepository;
import com.serkowski.productservice.repository.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;


    @Override
    public ProductDto placeProduct(ProductDto productRequest) {
        Product productSave = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .categories(productRequest.getCategories())
                .tags(productRequest.getTags())
                .price(productRequest.getPrice())
                .specification(productRequest.getSpecification())
                .build());
        return mapToDto(productSave);
    }

    @Override
    public ProductDto getProductById(String productId) {
        return productReadRepository.findById(productId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product which id: " + productId + " not exist"));
    }

    private ProductDto mapToDto(Product productSave) {
        return ProductDto.builder()
                .id(UUID.fromString(productSave.getId()))
                .name(productSave.getName())
                .description(productSave.getDescription())
                .categories(productSave.getCategories())
                .tags(productSave.getTags())
                .price(productSave.getPrice())
                .specification(productSave.getSpecification())
                .build();
    }
}
