package com.enncompany.ecom.product.controller;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.product.service.ProductService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping
  public ResponseEntity<PagedModel<ProductDto>> getAll(
      @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(new PagedModel<>(service.getAll(pageable)));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<PagedModel<ProductDto>> getByName(
      @PathVariable String name,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(new PagedModel<>(service.getByName(name, pageable)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> update(
      @PathVariable Long id, @Valid @RequestBody ProductDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }
}
