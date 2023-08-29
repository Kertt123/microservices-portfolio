package com.serkowski.productservice.service.api;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.ProductItemDto;

public interface ProductItemService {



    /**
     * Add item to the product.
     *
     * @param productId          product Id
     * @param productItemRequest product item request
     * @return {@link ProductItemDto}
     */
    ProductItemDto addItem(String productId, ProductItemDto productItemRequest);
}
