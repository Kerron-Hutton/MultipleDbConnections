package com.dev.multipledbconnections.controller;

import com.dev.multipledbconnections.model.Product;
import com.dev.multipledbconnections.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {

  final ProductRepository repository;

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    var product = repository.findById(id);

    if (product.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(product.get());
  }

  @GetMapping
  public ResponseEntity<List<Product>> getAllProductById() {
    return ResponseEntity.ok(repository.findAll());
  }

  @PostMapping
  public ResponseEntity<Product> addNewProduct(@RequestBody Product product) {
    return ResponseEntity.ok(repository.save(product));
  }

}
