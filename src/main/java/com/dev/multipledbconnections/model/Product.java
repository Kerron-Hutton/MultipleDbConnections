package com.dev.multipledbconnections.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table
@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  private double price;

}
