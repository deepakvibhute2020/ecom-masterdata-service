package com.enncompany.ecom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Getter
@Setter
public class Supplier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 200)
  private String businessType;

  @Column(length = 200)
  private String country;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(length = 20)
  private String contactNo;
}

