package com.enncompany.ecom.customer.service;

import com.enncompany.ecom.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

  CustomerDto create(CustomerDto dto);

  CustomerDto getById(Long id);

  Page<CustomerDto> getAll(Pageable pageable);

  CustomerDto update(Long id, CustomerDto dto);

  void delete(Long id);
}
