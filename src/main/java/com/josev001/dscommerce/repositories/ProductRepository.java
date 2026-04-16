package com.josev001.dscommerce.repositories;

import com.josev001.dscommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;



public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("Select obj FROM Product obj " +
            "WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%',:name, '%'))")
    Page<Product> searchByName(String name, Pageable pageable);

}
