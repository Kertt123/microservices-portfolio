package com.serkowski.productservice.service.api;

import com.serkowski.productservice.model.Product;

import java.util.Optional;

public interface ProductInnerService {

    /**
     * Get product by ID
     *
     * @param productId product id
     * @return {@link Product}
     */
    Optional<Product> findById(String productId);


    /**
     * Save updated product.
     *
     * @param product updated product
     */
    void saveProduct(Product product);
}
