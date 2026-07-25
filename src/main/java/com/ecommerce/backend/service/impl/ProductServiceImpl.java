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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCTS_CACHE = "products";
    private static final String PRODUCT_LISTS_CACHE = "productLists";

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryService categoryService,
                               ProductMapper productMapper,
                               CacheManager cacheManager) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = PRODUCT_LISTS_CACHE, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setCategory(categoryService.getCategoryEntity(request.getCategoryId()));
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Cacheable(cacheNames = PRODUCTS_CACHE, key = "#id")
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Override
    public Page<ProductResponse> getAll(Long categoryId, Pageable pageable) {
        String key = categoryId + "-" + pageable.getPageNumber() + "-" + pageable.getPageSize() + "-" + pageable.getSort();
        Cache cache = cacheManager.getCache(PRODUCT_LISTS_CACHE);
        CachedProductPage cached = cache.get(key, () -> loadPage(categoryId, pageable));
        return new PageImpl<>(cached.content(), pageable, cached.totalElements());
    }

    private CachedProductPage loadPage(Long categoryId, Pageable pageable) {
        Page<Product> products = categoryId != null
                ? productRepository.findByCategoryId(categoryId, pageable)
                : productRepository.findAll(pageable);
        List<ProductResponse> content = products.getContent().stream()
                .map(productMapper::toResponse)
                .toList();
        return new CachedProductPage(content, products.getTotalElements());
    }

    private record CachedProductPage(List<ProductResponse> content, long totalElements) {
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = PRODUCTS_CACHE, key = "#id"),
            @CacheEvict(cacheNames = PRODUCT_LISTS_CACHE, allEntries = true)
    })
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(categoryService.getCategoryEntity(request.getCategoryId()));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = PRODUCTS_CACHE, key = "#id"),
            @CacheEvict(cacheNames = PRODUCT_LISTS_CACHE, allEntries = true)
    })
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = PRODUCTS_CACHE, key = "#id"),
            @CacheEvict(cacheNames = PRODUCT_LISTS_CACHE, allEntries = true)
    })
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
