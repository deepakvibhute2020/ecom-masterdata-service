package com.enncompany.ecom.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import com.enncompany.ecom.product.mapper.ProductMapper;
import com.enncompany.ecom.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock private ProductRepository repository;
  @Spy private ProductMapper mapper = new ProductMapper();
  @InjectMocks private ProductServiceImpl service;

  private static Product product(Long id, String name) {
    Product p = new Product();
    p.setId(id);
    p.setName(name);
    return p;
  }

  private static ProductDto dto(String name) {
    ProductDto d = new ProductDto();
    d.setName(name);
    return d;
  }

  @Test
  void test_product_create_success() {
    when(repository.save(any(Product.class))).thenReturn(product(1L, "Laptop"));

    ProductDto result = service.create(dto("Laptop"));

    assertEquals(1L, result.getId());
    assertEquals("Laptop", result.getName());
    verify(repository).save(any(Product.class));
  }

  @Test
  void test_product_create_nullName_badRequest() {
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.create(dto(null)));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    verify(repository, never()).save(any());
  }

  @Test
  void test_product_create_blankName_badRequest() {
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.create(dto("  ")));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    verify(repository, never()).save(any());
  }

  @Test
  void test_product_create_nameTooLong_badRequest() {
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.create(dto("a".repeat(201))));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    verify(repository, never()).save(any());
  }

  @Test
  void test_product_getById_success() {
    when(repository.findById(1L)).thenReturn(Optional.of(product(1L, "Laptop")));

    ProductDto result = service.getById(1L);

    assertEquals(1L, result.getId());
    assertEquals("Laptop", result.getName());
    verify(repository).findById(1L);
  }

  @Test
  void test_product_getById_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.getById(5L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void test_product_getByName_success() {
    when(repository.findByNameContainingIgnoreCase("lap"))
        .thenReturn(List.of(product(1L, "Laptop"), product(2L, "Gaming Laptop")));

    List<ProductDto> result = service.getByName("lap");

    assertEquals(2, result.size());
    assertEquals("Laptop", result.get(0).getName());
    assertEquals("Gaming Laptop", result.get(1).getName());
    verify(repository).findByNameContainingIgnoreCase("lap");
  }

  @Test
  void test_product_getByName_noMatch_emptyList() {
    when(repository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());

    assertEquals(0, service.getByName("xyz").size());
  }

  @Test
  void test_product_update_success() {
    Product existing = product(1L, "Old");
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    ProductDto result = service.update(1L, dto("New"));

    assertEquals("New", result.getName());
    assertEquals(1L, result.getId());
    verify(repository).save(existing);
  }

  @Test
  void test_product_update_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.update(5L, dto("New")));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(repository, never()).save(any());
  }

  @Test
  void test_product_update_blankName_badRequest() {
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> service.update(1L, dto("")));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    verify(repository, never()).findById(any());
  }
}
