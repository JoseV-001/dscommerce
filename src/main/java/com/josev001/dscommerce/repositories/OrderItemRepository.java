package com.josev001.dscommerce.repositories;

import com.josev001.dscommerce.entities.OrderItem;
import com.josev001.dscommerce.entities.OrderItemPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk> {

}
