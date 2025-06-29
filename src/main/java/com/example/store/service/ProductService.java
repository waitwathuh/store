package com.example.store.service;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();

    ProductDTO createProduct(Product product);

    ProductDTO getProduct(Long id);
}
