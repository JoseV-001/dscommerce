package com.josev001.dscommerce.services;

import com.josev001.dscommerce.dto.ProductDTO;
import com.josev001.dscommerce.entities.Product;
import com.josev001.dscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
      Optional<Product> result = repository.findById(id);
      Product product = repository.findById(id).get();
      ProductDTO dto = new ProductDTO(product);
      return dto;

    }

}
