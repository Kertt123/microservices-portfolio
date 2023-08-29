package com.serkowski.productservice.service.api;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;

public interface ProductItemService {


    /**
     * Add item to the product.
     *
     * @param productId          product Id
     * @param productItemRequest product item request
     * @return {@link ProductItemDto}
     */
    ProductItemDto addItem(String productId, ProductItemDto productItemRequest);

    /**
     * Get product item by ID
     *
     * @param productItemId product item id
     * @return {@link ProductItemDto}
     */
    ProductItemDto getItemById(String productItemId);

    /**
     * Reserve items.
     *
     * @param reserveItemsDto list of items to reserve
     */
    void reserveItems(ReserveItemsDto reserveItemsDto);
}
