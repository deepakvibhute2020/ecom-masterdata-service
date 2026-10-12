package com.enncompany.ecom.product.mapper;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public ProductDto toDto(Product entity) {
    ProductDto dto = new ProductDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    return dto;
  }

  public Product toEntity(ProductDto dto) {
    Product entity = new Product();
    entity.setName(dto.getName().trim());
    return entity;
  }
}
