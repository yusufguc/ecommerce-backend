package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.ProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(Long id);

    Page<ProductResponse> getAll(Long categoryId, Pageable pageable);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    ProductResponse adjustStock(Long id, int quantityChange);

    Product getProductEntity(Long id);
}
