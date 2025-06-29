package com.example.store.service;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private Product productA;
    private Product productB;

    private ProductDTO productDTOA;
    private ProductDTO productDTOB;

    @BeforeEach
    void setUp() {
        productA = new Product();
        productA.setId(1L);
        productA.setDescription("Product A");

        productB = new Product();
        productB.setId(1L);
        productB.setDescription("Product B");

        productDTOA = new ProductDTO();
        productDTOA.setId(1L);
        productDTOA.setDescription("Product A");

        productDTOB = new ProductDTO();
        productDTOB.setId(2L);
        productDTOA.setDescription("Product B");
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(productA, productB);
        List<ProductDTO> expectedProductDTOs = List.of(productDTOA, productDTOB);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productsToProductDTOs(products)).thenReturn(expectedProductDTOs);

        List<ProductDTO> actualProductDTOs = productServiceImpl.getAllProducts();

        assertEquals(expectedProductDTOs.size(), actualProductDTOs.size());
        assertEquals(expectedProductDTOs, actualProductDTOs);
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById(productA.getId())).thenReturn(Optional.ofNullable(productA));
        when(productMapper.productToProductDTO(productA)).thenReturn(productDTOA);

        ProductDTO actualProductDTO = productServiceImpl.getProductById(productA.getId());

        assertEquals(productDTOA, actualProductDTO);
        verify(productRepository).findById(productA.getId());
    }

    @Test
    void testGetProductByIdNotFound() {
        Long productId = productA.getId();
        String message = String.format("Product with productId %s not found", productId);

        when(productRepository.findById(productA.getId())).thenThrow(new NotFoundException(message));

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> productServiceImpl.getProductById(productId));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(productA)).thenReturn(productA);
        when(productMapper.productToProductDTO(productA)).thenReturn(productDTOA);

        ProductDTO actualProductDTO = productServiceImpl.createProduct(productA);

        assertEquals(productDTOA, actualProductDTO);
        verify(productRepository).save(productA);
    }
}
