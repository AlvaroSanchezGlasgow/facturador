package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByUser(User user);
    Optional<Customer> findByIdAndUser(Long id, User user);
    long countByUser(User user);
}
