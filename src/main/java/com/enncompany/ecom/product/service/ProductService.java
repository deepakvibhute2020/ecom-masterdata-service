package com.enncompany.ecom.product.service;

import com.enncompany.ecom.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductDto create(ProductDto dto);

  ProductDto getById(Long id);

  Page<ProductDto> getAll(Pageable pageable);

  Page<ProductDto> getByName(String name, Pageable pageable);

  ProductDto update(Long id, ProductDto dto);
}
