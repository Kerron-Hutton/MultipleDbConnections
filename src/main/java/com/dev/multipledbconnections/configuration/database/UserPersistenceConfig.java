package com.dev.multipledbconnections.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@PropertySource({"classpath:database-connection.properties"})
@EnableJpaRepositories(
  basePackages = "com.dev.multipledbconnections.repository",
  transactionManagerRef = "userTransactionManager",
  entityManagerFactoryRef = "userEntityManager"
)
public class UserPersistenceConfig {

  private final Environment env;

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean userEntityManager() {
    var em = new LocalContainerEntityManagerFactoryBean();

    em.setPackagesToScan("com.dev.multipledbconnections.model");
    em.setDataSource(userDataSource());

    var vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    var properties = new HashMap<String, Object>();

    properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));

    em.setJpaPropertyMap(properties);

    return em;
  }

  @Bean
  @Primary
  public DataSource userDataSource() {
    var dataSource = new HikariDataSource();

    dataSource.setDriverClassName(
      Objects.requireNonNull(env.getProperty("hibernate.driver"))
    );

    dataSource.setUsername(env.getProperty("user.datasource.username"));
    dataSource.setPassword(env.getProperty("user.datasource.password"));
    dataSource.setJdbcUrl(env.getProperty("user.datasource.url"));
    dataSource.setPoolName("USER_DATASOURCE_CONNECTION_POOL");

    return dataSource;
  }

  @Bean
  @Primary
  public PlatformTransactionManager userTransactionManager() {
    var transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(
      userEntityManager().getObject()
    );

    return transactionManager;
  }

}
