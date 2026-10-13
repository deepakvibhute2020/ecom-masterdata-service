package com.enncompany.ecom.customer.service;

import com.enncompany.ecom.customer.mapper.CustomerMapper;
import com.enncompany.ecom.customer.repository.CustomerRepository;
import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.entity.Customer;
import com.enncompany.ecom.exception.CustomerNotFoundException;
import com.enncompany.ecom.exception.InvalidSortException;
import java.util.Set;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

  private static final Set<String> SORTABLE_PROPERTIES = Set.of("id", "name");

  private final CustomerRepository repository;
  private final CustomerMapper mapper;

  public CustomerServiceImpl(CustomerRepository repository, CustomerMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public CustomerDto create(CustomerDto dto) {
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }

  @Override
  @Transactional(readOnly = true)
  public CustomerDto getById(Long id) {
    return mapper.toDto(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CustomerDto> getAll(@ParameterObject Pageable pageable) {
    validateSort(pageable);
    return repository.findAll(pageable).map(mapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CustomerDto> getByName(String name, @ParameterObject Pageable pageable) {
    validateSort(pageable);
    return repository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDto);
  }

  @Override
  public CustomerDto update(Long id, CustomerDto dto) {
    Customer existing = find(id);
    mapper.apply(existing, dto);
    return mapper.toDto(repository.save(existing));
  }

  private Customer find(Long id) {
    return repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
  }

  private void validateSort(Pageable pageable) {
    pageable.getSort().forEach(order -> {
      if (!SORTABLE_PROPERTIES.contains(order.getProperty())) {
        throw new InvalidSortException(order.getProperty());
      }
    });
  }
}

