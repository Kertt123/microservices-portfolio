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

//    /**
//     * Update already existing order draft
//     *
//     * @param orderRequest request
//     * @param orderNumber  order number
//     * @return {@link OrderResponse}
//     */
//    OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber);
//
    /**
     * Get product by id
     *
     * @param productId product id
     * @return {@link ProductDto}
     */
    ProductDto getProductById(String productId);
//
//    /**
//     * Delete order by order number.
//     *
//     * @param orderNumber order number
//     */
//    void deleteOrderByOrderNumber(String orderNumber);

}
