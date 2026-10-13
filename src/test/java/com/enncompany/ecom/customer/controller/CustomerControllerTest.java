package com.enncompany.ecom.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enncompany.ecom.customer.service.CustomerService;
import com.enncompany.ecom.dto.CustomerDto;
import com.enncompany.ecom.exception.CustomerNotFoundException;
import com.enncompany.ecom.exception.GlobalExceptionHandler;
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

  private static CustomerDto dto(Long id, String name, String email) {
    CustomerDto dto = new CustomerDto();
    dto.setId(id);
    dto.setName(name);
    dto.setEmail(email);
    return dto;
  }

  private static String customerJson(String name, String email) {
    return "{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}";
  }

  @Test
  void create_returnsCreatedCustomer() throws Exception {
    when(service.create(any(CustomerDto.class)))
        .thenReturn(dto(1L, "Taylor", "taylor@example.com"));

    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson("Taylor", "taylor@example.com")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Taylor"))
        .andExpect(jsonPath("$.email").value("taylor@example.com"));

    verify(service).create(any(CustomerDto.class));
  }

  @Test
  void create_invalidEmail_returnsBadRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson("Taylor", "not-an-email")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("email must be valid"));

    verify(service, never()).create(any());
  }

  @Test
  void getAll_returnsPagedCustomers() throws Exception {
    when(service.getAll(any()))
        .thenReturn(
            new PageImpl<>(
                List.of(dto(1L, "Taylor", "taylor@example.com")), PageRequest.of(0, 20), 1));

    mockMvc
        .perform(get("/api/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Taylor"))
        .andExpect(jsonPath("$.page.totalElements").value(1));

    verify(service).getAll(PageRequest.of(0, 20));
  }

  @Test
  void getById_notFoundReturnsNotFound() throws Exception {
    when(service.getById(9L)).thenThrow(new CustomerNotFoundException(9L));

    mockMvc
        .perform(get("/api/customers/9"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer not found: 9"));
  }

  @Test
  void update_returnsUpdatedCustomer() throws Exception {
    when(service.update(any(), any(CustomerDto.class)))
        .thenReturn(dto(1L, "Updated", "updated@example.com"));

    mockMvc
        .perform(
            put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson("Updated", "updated@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"));

    verify(service).update(any(), any(CustomerDto.class));
  }

  @Test
  void delete_returnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/customers/1")).andExpect(status().isNoContent());

    verify(service).delete(1L);
  }
}
