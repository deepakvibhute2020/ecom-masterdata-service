package com.enncompany.ecom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "supplier_products")
@Getter
@Setter
public class SupplierProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "supplier_id", nullable = false)
  private Supplier supplier;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private Integer availableQuantity;

  @Column(nullable = false)
  private Integer reservedQuantity;
}

