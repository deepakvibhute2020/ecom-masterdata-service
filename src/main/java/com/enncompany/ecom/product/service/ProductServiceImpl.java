package com.enncompany.ecom.product.service;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import com.enncompany.ecom.exception.ProductNotFoundException;
import com.enncompany.ecom.product.mapper.ProductMapper;
import com.enncompany.ecom.product.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDto getById(Long id) {
    return mapper.toDto(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductDto> getAll() {
    return repository.findAll().stream().map(mapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductDto> getByName(String name) {
    return repository.findByNameContainingIgnoreCase(name).stream().map(mapper::toDto).toList();
  }

  @Override
  public ProductDto update(Long id, ProductDto dto) {
    Product existing = find(id);
    existing.setName(dto.getName().trim());
    return mapper.toDto(repository.save(existing));
  }

  private Product find(Long id) {
    return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }
}
