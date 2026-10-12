package com.enncompany.ecom.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import com.enncompany.ecom.exception.InvalidSortException;
import com.enncompany.ecom.exception.ProductNotFoundException;
import com.enncompany.ecom.product.mapper.ProductMapper;
import com.enncompany.ecom.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock private ProductRepository repository;
  private ProductServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new ProductServiceImpl(repository, new ProductMapper());
  }

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

    ProductNotFoundException ex =
        assertThrows(ProductNotFoundException.class, () -> service.getById(5L));

    assertEquals("Product not found: 5", ex.getMessage());
  }

  @Test
  void test_product_getAll_success() {
    when(repository.findAll(any(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(product(1L, "Laptop"), product(2L, "Phone")), PageRequest.of(0, 20), 2));

    Page<ProductDto> result = service.getAll(PageRequest.of(0, 20));

    assertEquals(2, result.getContent().size());
    assertEquals("Laptop", result.getContent().get(0).getName());
    assertEquals("Phone", result.getContent().get(1).getName());
    verify(repository).findAll(PageRequest.of(0, 20));
  }

  @Test
  void test_product_getAll_empty() {
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

    assertEquals(0, service.getAll(PageRequest.of(0, 20)).getContent().size());
  }

  @Test
  void test_product_getByName_success() {
    when(repository.findByNameContainingIgnoreCase("lap", PageRequest.of(0, 20)))
        .thenReturn(
            new PageImpl<>(
                List.of(product(1L, "Laptop"), product(2L, "Gaming Laptop")),
                PageRequest.of(0, 20),
                2));

    Page<ProductDto> result = service.getByName("lap", PageRequest.of(0, 20));

    assertEquals(2, result.getContent().size());
    assertEquals("Laptop", result.getContent().get(0).getName());
    assertEquals("Gaming Laptop", result.getContent().get(1).getName());
    verify(repository).findByNameContainingIgnoreCase("lap", PageRequest.of(0, 20));
  }

  @Test
  void test_product_getByName_noMatch_emptyList() {
    when(repository.findByNameContainingIgnoreCase("xyz", PageRequest.of(0, 20)))
        .thenReturn(new PageImpl<>(List.of()));

    assertEquals(0, service.getByName("xyz", PageRequest.of(0, 20)).getContent().size());
  }

  @Test
  void test_product_getAll_usesRequestedPageSize() {
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

    service.getAll(PageRequest.of(0, 50));

    verify(repository).findAll(PageRequest.of(0, 50));
  }

  @Test
  void test_product_getByName_usesRequestedPageSize() {
    when(repository.findByNameContainingIgnoreCase("lap", PageRequest.of(1, 50)))
        .thenReturn(new PageImpl<>(List.of()));

    service.getByName("lap", PageRequest.of(1, 50));

    verify(repository).findByNameContainingIgnoreCase("lap", PageRequest.of(1, 50));
  }

  @Test
  void test_product_getAll_invalidSort_throws() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("string"));

    assertThrows(InvalidSortException.class, () -> service.getAll(request));
    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  void test_product_getByName_invalidSort_throws() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("string"));

    assertThrows(InvalidSortException.class, () -> service.getByName("lap", request));
    verify(repository, never()).findByNameContainingIgnoreCase(any(), any());
  }

  @Test
  void test_product_getAll_validSort_ok() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("name"));
    when(repository.findAll(request)).thenReturn(new PageImpl<>(List.of()));

    service.getAll(request);

    verify(repository).findAll(request);
  }

  @Test
  void test_product_update_trimsName() {
    Product existing = product(1L, "Old");
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    ProductDto result = service.update(1L, dto("  New  "));

    assertEquals("New", result.getName());
    verify(repository).save(existing);
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

    ProductDto request = dto("New");

    assertThrows(ProductNotFoundException.class, () -> service.update(5L, request));

    verify(repository, never()).save(any());
  }
}
