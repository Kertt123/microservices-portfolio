package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductInnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ProductInnerServiceImpl implements ProductInnerService {

    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;

    @Override
    public Optional<Product> findById(String productId) {
        return productReadRepository.findById(productId);
    }

    @Override
    public void saveProduct(Product product) {
        productWriteRepository.save(product);
    }
}
