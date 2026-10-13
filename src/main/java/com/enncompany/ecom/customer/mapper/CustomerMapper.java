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
    entity.setName(dto.getName().trim());
    entity.setCountry(trimToNull(dto.getCountry()));
    entity.setEmail(dto.getEmail().trim());
    entity.setContactNo(trimToNull(dto.getContactNo()));
    return entity;
  }

  public void updateEntity(Customer entity, CustomerDto dto) {
    entity.setName(dto.getName().trim());
    entity.setCountry(trimToNull(dto.getCountry()));
    entity.setEmail(dto.getEmail().trim());
    entity.setContactNo(trimToNull(dto.getContactNo()));
  }

  private String trimToNull(String value) {
    return value == null ? null : value.trim();
  }
}
