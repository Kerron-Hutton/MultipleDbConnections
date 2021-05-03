package com.dev.multipledbconnections.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  private String email;

}
