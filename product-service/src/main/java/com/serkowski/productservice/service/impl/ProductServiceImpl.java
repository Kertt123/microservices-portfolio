package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.ProductReadRepository;
import com.serkowski.productservice.repository.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .ifPresentOrElse(productWriteRepository::delete, () -> {
                    throw new ProductNotFound("Product which id: " + productId + " not exist, so can't be deleted");
                });
    }

    @Override
    public ProductItemDto addItem(String productId, ProductItemDto productItemRequest) {
        return productReadRepository.findById(productId)
                .map(product -> {
                    ProductItem item = ProductItem.builder().id(UUID.randomUUID().toString()).availability(Availability.AVAILABLE).serialNumber(productItemRequest.getSerialNumber()).build();
                    if (product.getItems() == null){
                        product.setItems(List.of(item));
                    } else {
                        product.getItems().add(item);
                    }
                    productWriteRepository.save(product);
                    return ProductItemDto.builder()
                            .serialNumber(item.getSerialNumber())
                            .build();
                })
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
