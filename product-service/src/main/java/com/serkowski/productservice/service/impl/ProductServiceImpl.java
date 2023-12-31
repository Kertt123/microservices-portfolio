package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
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
    private final ProductItemWriteRepository productItemWriteRepository;


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
    public ProductDto updateProduct(ProductDto productRequest) {
        if (productRequest.getId() == null) {
            throw new ProductNotFound("Product can't be update because id field is empty");
        }
        return productReadRepository.findById(productRequest.getId().toString())
                .map(product -> {
                    product.setName(productRequest.getName());
                    product.setDescription(productRequest.getDescription());
                    product.setCategories(productRequest.getCategories());
                    product.setTags(productRequest.getTags());
                    product.setPrice(productRequest.getPrice());
                    product.setSpecification(productRequest.getSpecification());
                    return productWriteRepository.save(product);
                })
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product which id: " + productRequest.getId().toString() + " not exist, so can't be updated"));
    }

    @Override
    public ProductDto getProductById(String productId) {
        return productReadRepository.findById(productId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ProductNotFound("Product which id: " + productId + " not exist"));
    }

    @Override
    public void deleteProductById(String productId) {
        productReadRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    productItemWriteRepository.deleteAll(product.getItems());
                    productWriteRepository.delete(product);
                }, () -> {
                    throw new ProductNotFound("Product which id: " + productId + " not exist, so can't be deleted");
                });
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
