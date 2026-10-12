package com.enncompany.ecom.product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock private ProductService service;
  @InjectMocks private ProductController controller;

  @Test
  void test_product_create_returnsCreated() {
    ProductDto in = new ProductDto();
    ProductDto out = new ProductDto();
    when(service.create(in)).thenReturn(out);

    ResponseEntity<ProductDto> response = controller.create(in);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(out, response.getBody());
    verify(service).create(in);
  }

  @Test
  void test_product_getById_returnsDto() {
    ProductDto out = new ProductDto();
    when(service.getById(1L)).thenReturn(out);

    assertSame(out, controller.getById(1L));
    verify(service).getById(1L);
  }

  @Test
  void test_product_getByName_returnsList() {
    List<ProductDto> out = List.of(new ProductDto());
    when(service.getByName("lap")).thenReturn(out);

    assertSame(out, controller.getByName("lap"));
    verify(service).getByName("lap");
  }

  @Test
  void test_product_update_returnsDto() {
    ProductDto in = new ProductDto();
    ProductDto out = new ProductDto();
    when(service.update(1L, in)).thenReturn(out);

    assertSame(out, controller.update(1L, in));
    verify(service).update(1L, in);
  }
}
