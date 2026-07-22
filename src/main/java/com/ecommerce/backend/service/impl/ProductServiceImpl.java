package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.ProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.exception.base.BaseException;
import com.ecommerce.backend.exception.message.MessageType;
import com.ecommerce.backend.mapper.ProductMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.CategoryService;
import com.ecommerce.backend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryService categoryService,
                               ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setCategory(categoryService.getCategoryEntity(request.getCategoryId()));
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Override
    public Page<ProductResponse> getAll(Long categoryId, Pageable pageable) {
        Page<Product> products = categoryId != null
                ? productRepository.findByCategoryId(categoryId, pageable)
                : productRepository.findAll(pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(categoryService.getCategoryEntity(request.getCategoryId()));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse adjustStock(Long id, int quantityChange) {
        Product product = findProductOrThrow(id);
        int newStock = product.getStockQuantity() + quantityChange;
        if (newStock < 0) {
            throw new BaseException(MessageType.INSUFFICIENT_STOCK);
        }
        product.setStockQuantity(newStock);
        return productMapper.toResponse(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BaseException(MessageType.PRODUCT_NOT_FOUND));
    }
}
