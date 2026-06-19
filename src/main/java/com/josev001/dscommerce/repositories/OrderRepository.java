package com.josev001.dscommerce.repositories;

import com.josev001.dscommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
