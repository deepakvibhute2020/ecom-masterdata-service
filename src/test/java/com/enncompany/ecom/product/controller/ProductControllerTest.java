package com.enncompany.ecom.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enncompany.ecom.dto.ProductDto;
import com.enncompany.ecom.exception.GlobalExceptionHandler;
import com.enncompany.ecom.exception.ProductNotFoundException;
import com.enncompany.ecom.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private ProductService service;

  private static ProductDto dto(Long id, String name) {
    ProductDto d = new ProductDto();
    d.setId(id);
    d.setName(name);
    return d;
  }

  private static String json(String name) {
    return "{\"name\":" + (name == null ? "null" : "\"" + name + "\"") + "}";
  }

  @Test
  void test_product_create_returnsCreated() throws Exception {
    when(service.create(any(ProductDto.class))).thenReturn(dto(1L, "Laptop"));

    mockMvc
        .perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json("Laptop")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Laptop"));

    verify(service).create(any(ProductDto.class));
  }

  @Test
  void test_product_create_missingName_badRequest() throws Exception {
    mockMvc
        .perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("name is required"));

    verify(service, never()).create(any());
  }

  @Test
  void test_product_create_blankName_badRequest() throws Exception {
    mockMvc
        .perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json("   ")))
        .andExpect(status().isBadRequest());

    verify(service, never()).create(any());
  }

  @Test
  void test_product_create_nameTooLong_badRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("a".repeat(201))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("name must be <= 200 characters"));

    verify(service, never()).create(any());
  }

  @Test
  void test_product_create_malformedJson_badRequest() throws Exception {
    mockMvc
        .perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{bad"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));

    verify(service, never()).create(any());
  }

  @Test
  void test_product_getById_exists_ok() throws Exception {
    when(service.getById(101L)).thenReturn(dto(101L, "Laptop"));

    mockMvc
        .perform(get("/api/products/101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(101))
        .andExpect(jsonPath("$.name").value("Laptop"));

    verify(service).getById(101L);
  }

  @Test
  void test_product_getById_notFound() throws Exception {
    when(service.getById(9999L)).thenThrow(new ProductNotFoundException(9999L));

    mockMvc
        .perform(get("/api/products/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Product not found: 9999"));
  }

  @Test
  void test_product_getById_invalidId_badRequest() throws Exception {
    mockMvc
        .perform(get("/api/products/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"));

    verify(service, never()).getById(any());
  }

  @Test
  void test_product_getAll_ok() throws Exception {
    when(service.getAll()).thenReturn(List.of(dto(1L, "Laptop"), dto(2L, "Phone")));

    mockMvc
        .perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[1].name").value("Phone"));

    verify(service).getAll();
  }

  @Test
  void test_product_getAll_empty_ok() throws Exception {
    when(service.getAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void test_product_getByName_ok() throws Exception {
    when(service.getByName("lap")).thenReturn(List.of(dto(1L, "Laptop")));

    mockMvc
        .perform(get("/api/products/name/lap"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Laptop"));

    verify(service).getByName("lap");
  }

  @Test
  void test_product_getByName_noMatch_emptyList() throws Exception {
    when(service.getByName("zzz")).thenReturn(List.of());

    mockMvc
        .perform(get("/api/products/name/zzz"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void test_product_update_ok() throws Exception {
    when(service.update(any(), any(ProductDto.class))).thenReturn(dto(1L, "Gaming Laptop"));

    mockMvc
        .perform(
            put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Gaming Laptop")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Gaming Laptop"));

    verify(service).update(any(), any(ProductDto.class));
  }

  @Test
  void test_product_update_notFound() throws Exception {
    when(service.update(any(), any(ProductDto.class))).thenThrow(new ProductNotFoundException(5L));

    mockMvc
        .perform(put("/api/products/5").contentType(MediaType.APPLICATION_JSON).content(json("X")))
        .andExpect(status().isNotFound());
  }

  @Test
  void test_product_update_blankName_badRequest() throws Exception {
    mockMvc
        .perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(json("")))
        .andExpect(status().isBadRequest());

    verify(service, never()).update(any(), any());
  }

  @Test
  void test_product_update_invalidId_badRequest() throws Exception {
    mockMvc
        .perform(put("/api/products/abc").contentType(MediaType.APPLICATION_JSON).content(json("X")))
        .andExpect(status().isBadRequest());
  }
}
