package com.enncompany.ecom.product.service;

import com.enncompany.ecom.product.mapper.ProductMapper;
import com.enncompany.ecom.product.repository.ProductRepository;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;
  private final ProductMapper mapper;

  public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public ProductDto create(ProductDto dto) {
    validate(dto);
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDto getById(Long id) {
    return mapper.toDto(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductDto> getByName(String name) {
    return repository.findByNameContainingIgnoreCase(name).stream().map(mapper::toDto).toList();
  }

  @Override
  public ProductDto update(Long id, ProductDto dto) {
    validate(dto);
    Product existing = find(id);
    existing.setName(dto.getName());
    return mapper.toDto(repository.save(existing));
  }

  private Product find(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
  }

  private void validate(ProductDto dto) {
    if (dto.getName() == null || dto.getName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
    }
    if (dto.getName().length() > 200) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must be <= 200 characters");
    }
  }
}
