package com.dev.multipledbconnections.controller;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.dev.multipledbconnections.model.User;
import com.dev.multipledbconnections.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {

  private final UserRepository repository;

  private final DataSource dataSource;

  private final MetricRegistry registry;

  public UsersController(UserRepository repository,
                         @Qualifier("productDataSource") DataSource dataSource,
                         MetricRegistry registry) {
    this.repository = repository;
    this.dataSource = dataSource;
    this.registry = registry;
  }

  @DeleteMapping
  public void delete() {

    try {
      Connection conn1 = dataSource.getConnection();

      log.info("Schema name is " + conn1.getSchema());
    } catch (SQLException e1) {
      log.error("An error occurred");
      e1.printStackTrace();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) throws InterruptedException {
    var product = repository.findById(id);

    if (product.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    Thread.sleep(30 * 1000);


    return ResponseEntity.ok(product.get());
  }

  @GetMapping
  public ResponseEntity<List<User>> getAllUserById() {
    ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
    reporter.report();

    return ResponseEntity.ok(repository.findAll());
  }

  @PostMapping
  public ResponseEntity<User> registerUser(@RequestBody User product) {
    return ResponseEntity.ok(repository.save(product));
  }

}
