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
import com.enncompany.ecom.exception.InvalidSortException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock private CustomerRepository repository;
  private CustomerServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new CustomerServiceImpl(repository, new CustomerMapper());
  }

  private static Customer customer(Long id, String name, String country, String email, String contactNo) {
    Customer c = new Customer();
    c.setId(id);
    c.setName(name);
    c.setCountry(country);
    c.setEmail(email);
    c.setContactNo(contactNo);
    return c;
  }

  private static CustomerDto dto(String name, String country, String email, String contactNo) {
    CustomerDto d = new CustomerDto();
    d.setName(name);
    d.setCountry(country);
    d.setEmail(email);
    d.setContactNo(contactNo);
    return d;
  }

  @Test
  void test_customer_create_success() {
    when(repository.save(any(Customer.class)))
        .thenReturn(customer(1L, "Alice", "USA", "alice@example.com", "12345"));

    CustomerDto result = service.create(dto("Alice", "USA", "alice@example.com", "12345"));

    assertEquals(1L, result.getId());
    assertEquals("Alice", result.getName());
    assertEquals("USA", result.getCountry());
    assertEquals("alice@example.com", result.getEmail());
    assertEquals("12345", result.getContactNo());
    verify(repository).save(any(Customer.class));
  }

  @Test
  void test_customer_getById_success() {
    when(repository.findById(1L))
        .thenReturn(Optional.of(customer(1L, "Alice", "USA", "alice@example.com", "12345")));

    CustomerDto result = service.getById(1L);

    assertEquals(1L, result.getId());
    assertEquals("Alice", result.getName());
    assertEquals("USA", result.getCountry());
    assertEquals("alice@example.com", result.getEmail());
    assertEquals("12345", result.getContactNo());
    verify(repository).findById(1L);
  }

  @Test
  void test_customer_getById_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> service.getById(5L));

    assertEquals("Customer not found: 5", ex.getMessage());
  }

  @Test
  void test_customer_getAll_success() {
    when(repository.findAll(any(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(
                    customer(1L, "Alice", "USA", "alice@example.com", "12345"),
                    customer(2L, "Bob", "India", "bob@example.com", "99999")),
                PageRequest.of(0, 20),
                2));

    Page<CustomerDto> result = service.getAll(PageRequest.of(0, 20));

    assertEquals(2, result.getContent().size());
    assertEquals("Alice", result.getContent().get(0).getName());
    assertEquals("Bob", result.getContent().get(1).getName());
    verify(repository).findAll(PageRequest.of(0, 20));
  }

  @Test
  void test_customer_getAll_empty() {
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

    assertEquals(0, service.getAll(PageRequest.of(0, 20)).getContent().size());
  }

  @Test
  void test_customer_getByName_success() {
    when(repository.findByNameContainingIgnoreCase("ali", PageRequest.of(0, 20)))
        .thenReturn(
            new PageImpl<>(
                List.of(customer(1L, "Alice", "USA", "alice@example.com", "12345")),
                PageRequest.of(0, 20),
                1));

    Page<CustomerDto> result = service.getByName("ali", PageRequest.of(0, 20));

    assertEquals(1, result.getContent().size());
    assertEquals("Alice", result.getContent().get(0).getName());
    verify(repository).findByNameContainingIgnoreCase("ali", PageRequest.of(0, 20));
  }

  @Test
  void test_customer_getByName_noMatch_emptyList() {
    when(repository.findByNameContainingIgnoreCase("zzz", PageRequest.of(0, 20)))
        .thenReturn(new PageImpl<>(List.of()));

    assertEquals(0, service.getByName("zzz", PageRequest.of(0, 20)).getContent().size());
  }

  @Test
  void test_customer_getAll_invalidSort_throws() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("country"));

    assertThrows(InvalidSortException.class, () -> service.getAll(request));
    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  void test_customer_getByName_invalidSort_throws() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("country"));

    assertThrows(InvalidSortException.class, () -> service.getByName("ali", request));
    verify(repository, never()).findByNameContainingIgnoreCase(any(), any());
  }

  @Test
  void test_customer_getAll_validSort_ok() {
    PageRequest request = PageRequest.of(0, 20, Sort.by("name"));
    when(repository.findAll(request)).thenReturn(new PageImpl<>(List.of()));

    service.getAll(request);

    verify(repository).findAll(request);
  }

  @Test
  void test_customer_update_trimsFields() {
    Customer existing = customer(1L, "Old", "OldCountry", "old@example.com", "00000");
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    CustomerDto result = service.update(1L, dto("  New  ", "  UK  ", "  new@example.com  ", "  11111  "));

    assertEquals("New", result.getName());
    assertEquals("UK", result.getCountry());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("11111", result.getContactNo());
    verify(repository).save(existing);
  }

  @Test
  void test_customer_update_success() {
    Customer existing = customer(1L, "Old", "OldCountry", "old@example.com", "00000");
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    CustomerDto result = service.update(1L, dto("New", "India", "new@example.com", "99999"));

    assertEquals(1L, result.getId());
    assertEquals("New", result.getName());
    assertEquals("India", result.getCountry());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("99999", result.getContactNo());
    verify(repository).save(existing);
  }

  @Test
  void test_customer_update_notFound() {
    when(repository.findById(5L)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class, () -> service.update(5L, dto("New", "India", "new@example.com", "99999")));

    verify(repository, never()).save(any());
  }
}

