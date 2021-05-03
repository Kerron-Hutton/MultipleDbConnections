package com.dev.multipledbconnections.repository;

import com.dev.multipledbconnections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Override
  @Transactional
  Optional<User> findById(Long aLong);

}
