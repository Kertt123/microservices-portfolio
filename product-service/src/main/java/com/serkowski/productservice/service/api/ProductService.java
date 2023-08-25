package com.serkowski.productservice.service.api;

import com.serkowski.productservice.dto.ProductDto;

public interface ProductService {


    /**
     * Place product.
     *
     * @param productRequest request
     * @return {@link ProductDto}
     */
    ProductDto placeProduct(ProductDto productRequest);

    /**
     * Update already existing product
     *
     * @param productRequest request
     * @return {@link ProductDto}
     */
    ProductDto updateProduct(ProductDto productRequest);

    /**
     * Get product by id
     *
     * @param productId product id
     * @return {@link ProductDto}
     */
    ProductDto getProductById(String productId);

    /**
     * Delete product by id
     *
     * @param productId product id
     */
    void deleteProductById(String productId);

}
