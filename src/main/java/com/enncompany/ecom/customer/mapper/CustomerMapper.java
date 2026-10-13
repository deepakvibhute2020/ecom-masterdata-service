package com.enncompany.ecom.customer.mapper;

import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public CustomerDto toDto(Customer entity) {
    CustomerDto dto = new CustomerDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setCountry(entity.getCountry());
    dto.setEmail(entity.getEmail());
    dto.setContactNo(entity.getContactNo());
    return dto;
  }

  public Customer toEntity(CustomerDto dto) {
    Customer entity = new Customer();
    apply(entity, dto);
    return entity;
  }

  public void apply(Customer entity, CustomerDto dto) {
    entity.setName(trim(dto.getName()));
    entity.setCountry(trim(dto.getCountry()));
    entity.setEmail(trim(dto.getEmail()));
    entity.setContactNo(trim(dto.getContactNo()));
  }

  private String trim(String value) {
    return value == null ? null : value.trim();
  }
}

