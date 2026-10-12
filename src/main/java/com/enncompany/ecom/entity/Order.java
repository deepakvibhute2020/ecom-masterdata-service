package com.enncompany.ecom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  private java.time.LocalDateTime creationDate;

  private java.time.LocalDateTime deliveredDate;

  @Column(length = 50)
  private String status;

  @Column(length = 50)
  private String paymentMode;
}

