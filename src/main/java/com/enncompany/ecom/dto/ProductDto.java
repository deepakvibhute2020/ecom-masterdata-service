package com.enncompany.ecom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

  private Long id;

  @NotBlank(message = "name is required")
  @Size(max = 200, message = "name must be <= 200 characters")
  private String name;
}
