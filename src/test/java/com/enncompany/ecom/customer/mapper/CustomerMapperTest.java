package com.enncompany.ecom.customer.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.entity.Customer;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private final CustomerMapper mapper = new CustomerMapper();

  @Test
  void test_customer_toDto_mapsAllFields() {
    Customer entity = new Customer();
    entity.setId(1L);
    entity.setName("Alice");
    entity.setCountry("USA");
    entity.setEmail("alice@example.com");
    entity.setContactNo("12345");

    CustomerDto dto = mapper.toDto(entity);

    assertEquals(1L, dto.getId());
    assertEquals("Alice", dto.getName());
    assertEquals("USA", dto.getCountry());
    assertEquals("alice@example.com", dto.getEmail());
    assertEquals("12345", dto.getContactNo());
  }

  @Test
  void test_customer_toEntity_mapsFieldsAndIgnoresId() {
    CustomerDto dto = new CustomerDto();
    dto.setId(99L);
    dto.setName("Bob");
    dto.setCountry("India");
    dto.setEmail("bob@example.com");
    dto.setContactNo("99999");

    Customer entity = mapper.toEntity(dto);

    assertNull(entity.getId());
    assertEquals("Bob", entity.getName());
    assertEquals("India", entity.getCountry());
    assertEquals("bob@example.com", entity.getEmail());
    assertEquals("99999", entity.getContactNo());
  }

  @Test
  void test_customer_toEntity_trimsStringFields() {
    CustomerDto dto = new CustomerDto();
    dto.setName("  Bob  ");
    dto.setCountry("  India  ");
    dto.setEmail("  bob@example.com  ");
    dto.setContactNo("  99999  ");

    Customer entity = mapper.toEntity(dto);

    assertEquals("Bob", entity.getName());
    assertEquals("India", entity.getCountry());
    assertEquals("bob@example.com", entity.getEmail());
    assertEquals("99999", entity.getContactNo());
  }
}

