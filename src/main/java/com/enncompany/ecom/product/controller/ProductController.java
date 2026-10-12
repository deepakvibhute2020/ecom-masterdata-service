package com.enncompany.ecom.product.controller;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
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
  public ResponseEntity<List<ProductDto>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<ProductDto>> getByName(@PathVariable String name) {
    return ResponseEntity.ok(service.getByName(name));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> update(
      @PathVariable Long id, @Valid @RequestBody ProductDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }
}
