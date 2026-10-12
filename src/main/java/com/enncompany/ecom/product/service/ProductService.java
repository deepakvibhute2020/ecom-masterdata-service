package com.enncompany.ecom.product.service;

import com.enncompany.ecom.dto.ProductDto;
import java.util.List;

public interface ProductService {

  ProductDto create(ProductDto dto);

  ProductDto getById(Long id);

  List<ProductDto> getByName(String name);

  ProductDto update(Long id, ProductDto dto);
}
