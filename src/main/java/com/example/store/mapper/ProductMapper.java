package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(
            target = "orderIds",
            expression =
                    "java(product.getOrders() == null ? null : product.getOrders().stream().map(o -> o.getId()).toList())")
    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productsToProductDTOs(List<Product> products);
}
