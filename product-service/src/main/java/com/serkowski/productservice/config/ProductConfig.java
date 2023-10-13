package com.serkowski.productservice.config;

import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductInnerService;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.impl.ProductItemServiceImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    public ProductItemService productItemService(ProductInnerService productInnerService,
                                                 ProductItemReadRepository productItemReadRepository,
                                                 ProductItemWriteRepository productItemWriteRepository) {
        return new ProductItemServiceImpl(productInnerService, productItemReadRepository, productItemWriteRepository);
    }
}
