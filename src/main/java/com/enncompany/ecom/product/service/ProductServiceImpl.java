package com.enncompany.ecom.product.service;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.entity.Product;
import com.enncompany.ecom.exception.InvalidSortException;
import com.enncompany.ecom.exception.ProductNotFoundException;
import com.enncompany.ecom.product.mapper.ProductMapper;
import com.enncompany.ecom.product.repository.ProductRepository;
import java.util.Set;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  private static final Set<String> SORTABLE_PROPERTIES = Set.of("id", "name");

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
  public Page<ProductDto> getAll(@ParameterObject Pageable pageable) {
    validateSort(pageable);
    return repository.findAll(pageable).map(mapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDto> getByName(String name, @ParameterObject Pageable pageable) {
    validateSort(pageable);
    return repository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDto);
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

  private void validateSort(Pageable pageable) {
    pageable.getSort().forEach(order -> {
      if (!SORTABLE_PROPERTIES.contains(order.getProperty())) {
        throw new InvalidSortException(order.getProperty());
      }
    });
  }

}
