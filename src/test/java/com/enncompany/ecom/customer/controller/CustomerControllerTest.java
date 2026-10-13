package com.enncompany.ecom.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enncompany.ecom.customer.service.CustomerService;
import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.exception.CustomerNotFoundException;
import com.enncompany.ecom.exception.GlobalExceptionHandler;
import com.enncompany.ecom.exception.InvalidSortException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private CustomerService service;

  private static CustomerDto dto(Long id, String name, String country, String email, String contactNo) {
    CustomerDto d = new CustomerDto();
    d.setId(id);
    d.setName(name);
    d.setCountry(country);
    d.setEmail(email);
    d.setContactNo(contactNo);
    return d;
  }

  private static String json(String name, String country, String email, String contactNo) {
    return "{"
        + "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ","
        + "\"country\":" + (country == null ? "null" : "\"" + country + "\"") + ","
        + "\"email\":" + (email == null ? "null" : "\"" + email + "\"") + ","
        + "\"contactNo\":" + (contactNo == null ? "null" : "\"" + contactNo + "\"")
        + "}";
  }

  @Test
  void test_customer_create_returnsCreated() throws Exception {
    when(service.create(any(CustomerDto.class))).thenReturn(dto(1L, "Alice", "USA", "alice@example.com", "12345"));

    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Alice", "USA", "alice@example.com", "12345")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.country").value("USA"))
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.contactNo").value("12345"));

    verify(service).create(any(CustomerDto.class));
  }

  @Test
  void test_customer_create_missingName_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(null, "USA", "alice@example.com", "12345")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("name is required"));

    verify(service, never()).create(any());
  }

  @Test
  void test_customer_create_missingEmail_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Alice", "USA", null, "12345")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("email is required"));

    verify(service, never()).create(any());
  }

  @Test
  void test_customer_create_blankName_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("   ", "USA", "alice@example.com", "12345")))
        .andExpect(status().isBadRequest());

    verify(service, never()).create(any());
  }

  @Test
  void test_customer_create_malformedJson_badRequest() throws Exception {
    mockMvc
        .perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{bad"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));

    verify(service, never()).create(any());
  }

  @Test
  void test_customer_getById_exists_ok() throws Exception {
    when(service.getById(101L)).thenReturn(dto(101L, "Alice", "USA", "alice@example.com", "12345"));

    mockMvc
        .perform(get("/api/customers/101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(101))
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.email").value("alice@example.com"));

    verify(service).getById(101L);
  }

  @Test
  void test_customer_getById_notFound() throws Exception {
    when(service.getById(9999L)).thenThrow(new CustomerNotFoundException(9999L));

    mockMvc
        .perform(get("/api/customers/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer not found: 9999"));
  }

  @Test
  void test_customer_getById_invalidId_badRequest() throws Exception {
    mockMvc
        .perform(get("/api/customers/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"));

    verify(service, never()).getById(any());
  }

  @Test
  void test_customer_getAll_ok() throws Exception {
    when(service.getAll(any())).thenReturn(
        new PageImpl<>(
            List.of(
                dto(1L, "Alice", "USA", "alice@example.com", "12345"),
                dto(2L, "Bob", "India", "bob@example.com", "99999")),
            PageRequest.of(0, 20),
            2));

    mockMvc
        .perform(get("/api/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[1].name").value("Bob"))
        .andExpect(jsonPath("$.page.size").value(20))
        .andExpect(jsonPath("$.page.totalElements").value(2));

    verify(service).getAll(PageRequest.of(0, 20));
  }

  @Test
  void test_customer_getAll_invalidSort_badRequest() throws Exception {
    when(service.getAll(any())).thenThrow(new InvalidSortException("country"));

    mockMvc
        .perform(get("/api/customers").param("sort", "country,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid sort property: country"));
  }

  @Test
  void test_customer_getByName_ok() throws Exception {
    when(service.getByName(any(), any()))
        .thenReturn(new PageImpl<>(List.of(dto(1L, "Alice", "USA", "alice@example.com", "12345"))));

    mockMvc
        .perform(get("/api/customers/name/ali"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Alice"));

    verify(service).getByName("ali", PageRequest.of(0, 20));
  }

  @Test
  void test_customer_getByName_noMatch_emptyList() throws Exception {
    when(service.getByName(any(), any())).thenReturn(new PageImpl<>(List.of()));

    mockMvc
        .perform(get("/api/customers/name/zzz"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0));
  }

  @Test
  void test_customer_update_ok() throws Exception {
    when(service.update(any(), any(CustomerDto.class))).thenReturn(dto(1L, "Alice Updated", "UK", "alice.updated@example.com", "77777"));

    mockMvc
        .perform(
            put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Alice Updated", "UK", "alice.updated@example.com", "77777")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Alice Updated"))
        .andExpect(jsonPath("$.country").value("UK"))
        .andExpect(jsonPath("$.email").value("alice.updated@example.com"));

    verify(service).update(any(), any(CustomerDto.class));
  }

  @Test
  void test_customer_update_notFound() throws Exception {
    when(service.update(any(), any(CustomerDto.class))).thenThrow(new CustomerNotFoundException(5L));

    mockMvc
        .perform(
            put("/api/customers/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("X", "Y", "x@y.com", "123")))
        .andExpect(status().isNotFound());
  }

  @Test
  void test_customer_update_blankName_badRequest() throws Exception {
    mockMvc
        .perform(
            put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("", "USA", "alice@example.com", "12345")))
        .andExpect(status().isBadRequest());

    verify(service, never()).update(any(), any());
  }

  @Test
  void test_customer_update_invalidId_badRequest() throws Exception {
    mockMvc
        .perform(
            put("/api/customers/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("X", "Y", "x@y.com", "123")))
        .andExpect(status().isBadRequest());
  }
}

