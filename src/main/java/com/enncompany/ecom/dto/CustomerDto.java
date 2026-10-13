package com.enncompany.ecom.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {

  private Long id;

  @NotBlank(message = "name is required")
  @Size(max = 200, message = "name must be <= 200 characters")
  private String name;

  @Size(max = 200, message = "country must be <= 200 characters")
  private String country;

  @NotBlank(message = "email is required")
  @Email(message = "email must be valid")
  @Size(max = 100, message = "email must be <= 100 characters")
  private String email;

  @Size(max = 20, message = "contactNo must be <= 20 characters")
  private String contactNo;
}
