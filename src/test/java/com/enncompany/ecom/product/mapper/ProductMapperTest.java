package com.enncompany.ecom.product.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

  private final ProductMapper mapper = new ProductMapper();

  @Test
  void test_product_toDto_mapsAllFields() {
    Product entity = new Product();
    entity.setId(1L);
    entity.setName("Laptop");

    ProductDto dto = mapper.toDto(entity);

    assertEquals(1L, dto.getId());
    assertEquals("Laptop", dto.getName());
  }

  @Test
  void test_product_toEntity_mapsNameAndIgnoresId() {
    ProductDto dto = new ProductDto();
    dto.setId(99L);
    dto.setName("Phone");

    Product entity = mapper.toEntity(dto);

    assertNull(entity.getId());
    assertEquals("Phone", entity.getName());
  }

  @Test
  void test_product_toEntity_trimsName() {
    ProductDto dto = new ProductDto();
    dto.setName("  Phone  ");

    assertEquals("Phone", mapper.toEntity(dto).getName());
  }
}
