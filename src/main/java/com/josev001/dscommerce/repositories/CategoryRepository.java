package com.josev001.dscommerce.repositories;

import com.josev001.dscommerce.entities.Category;
import com.josev001.dscommerce.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {

}

