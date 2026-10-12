package com.enncompany.ecom.product.controller;

import com.enncompany.ecom.product.service.ProductService;

import com.enncompany.ecom.dto.ProductDto;
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
  public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
  }

  @GetMapping("/id/{id:\\d+}")
  public ProductDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<ProductDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/name/{name}")
  public List<ProductDto> getByName(@PathVariable String name) {
    return service.getByName(name);
  }

  @PutMapping("/id/{id}")
  public ProductDto update(@PathVariable Long id, @RequestBody ProductDto dto) {
    return service.update(id, dto);
  }
}
