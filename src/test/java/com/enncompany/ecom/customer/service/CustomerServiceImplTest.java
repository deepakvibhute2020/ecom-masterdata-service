package com.enncompany.ecom.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enncompany.ecom.customer.mapper.CustomerMapper;
import com.enncompany.ecom.customer.repository.CustomerRepository;
import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.entity.Customer;
import com.enncompany.ecom.exception.CustomerNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock private CustomerRepository repository;
  private CustomerServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new CustomerServiceImpl(repository, new CustomerMapper());
  }

  private static Customer customer(Long id, String name, String email) {
    Customer customer = new Customer();
    customer.setId(id);
    customer.setName(name);
    customer.setEmail(email);
    return customer;
  }

  private static CustomerDto dto(String name, String email) {
    CustomerDto dto = new CustomerDto();
    dto.setName(name);
    dto.setEmail(email);
    return dto;
  }

  @Test
  void create_trimsValuesAndReturnsCustomer() {
    when(repository.save(any(Customer.class)))
        .thenAnswer(invocation -> {
          Customer saved = invocation.getArgument(0);
          saved.setId(1L);
          return saved;
        });
    CustomerDto request = dto("  Taylor  ", " taylor@example.com ");
    request.setCountry("  Canada ");
    request.setContactNo(" 123 ");

    CustomerDto result = service.create(request);

    assertEquals(1L, result.getId());
    assertEquals("Taylor", result.getName());
    assertEquals("taylor@example.com", result.getEmail());
    assertEquals("Canada", result.getCountry());
    assertEquals("123", result.getContactNo());
  }

  @Test
  void getById_notFound() {
    when(repository.findById(9L)).thenReturn(Optional.empty());

    CustomerNotFoundException exception =
        assertThrows(CustomerNotFoundException.class, () -> service.getById(9L));

    assertEquals("Customer not found: 9", exception.getMessage());
  }

  @Test
  void getAll_returnsMappedPage() {
    when(repository.findAll(PageRequest.of(0, 20)))
        .thenReturn(new PageImpl<>(List.of(customer(1L, "Taylor", "taylor@example.com"))));

    var result = service.getAll(PageRequest.of(0, 20));

    assertEquals("Taylor", result.getContent().get(0).getName());
    assertEquals("taylor@example.com", result.getContent().get(0).getEmail());
  }

  @Test
  void update_replacesCustomerFields() {
    Customer existing = customer(1L, "Old name", "old@example.com");
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);
    CustomerDto request = dto("New name", "new@example.com");
    request.setCountry("USA");
    request.setContactNo("555");

    CustomerDto result = service.update(1L, request);

    assertEquals("New name", result.getName());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("USA", result.getCountry());
    assertEquals("555", result.getContactNo());
    verify(repository).save(existing);
  }

  @Test
  void delete_notFoundDoesNotDelete() {
    when(repository.findById(9L)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class, () -> service.delete(9L));

    verify(repository, never()).delete(any(Customer.class));
  }
}
