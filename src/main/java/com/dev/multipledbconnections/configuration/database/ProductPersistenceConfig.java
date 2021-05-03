package com.dev.multipledbconnections.configuration.database;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@PropertySource({"classpath:database-connection.properties"})
@EnableJpaRepositories(
  basePackages = "com.dev.multipledbconnections.repository",
  transactionManagerRef = "productTransactionManager",
  entityManagerFactoryRef = "productEntityManager"
)
public class ProductPersistenceConfig {

  private final Environment env;

  @Bean
  public LocalContainerEntityManagerFactoryBean productEntityManager() {
    var em = new LocalContainerEntityManagerFactoryBean();

    em.setPackagesToScan("com.dev.multipledbconnections.model");
    em.setDataSource(productDataSource());

    var vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    var properties = new HashMap<String, Object>();

    properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));

    em.setJpaPropertyMap(properties);

    return em;
  }

  @Bean
  public DataSource productDataSource() {
    var dataSource = new HikariDataSource();

    dataSource.setDriverClassName(
      Objects.requireNonNull(env.getProperty("hibernate.driver"))
    );

    dataSource.setUsername(env.getProperty("product.datasource.username"));
    dataSource.setPassword(env.getProperty("product.datasource.password"));
    dataSource.setJdbcUrl(env.getProperty("product.datasource.url"));
    dataSource.setPoolName("PRODUCT_DATASOURCE_CONNECTION_POOL");

    dataSource.setLeakDetectionThreshold(2100);

    dataSource.setMinimumIdle(5);

    dataSource.setMetricRegistry(getMetrics());

    try {
      dataSource.setLogWriter(new PrintWriter(System.out));
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return dataSource;
  }

  @Bean
  public MetricRegistry getMetrics() {
    return new MetricRegistry();
  }

  @Bean
  public PlatformTransactionManager productTransactionManager() {
    var transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(
      productEntityManager().getObject()
    );

    return transactionManager;
  }

}
