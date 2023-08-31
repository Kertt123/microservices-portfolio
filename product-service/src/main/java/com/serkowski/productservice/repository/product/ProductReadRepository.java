package com.serkowski.productservice.repository.product;

import com.serkowski.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductReadRepository extends MongoRepository<Product, String> {

}
