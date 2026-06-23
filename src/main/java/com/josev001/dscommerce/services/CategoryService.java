package com.josev001.dscommerce.services;

import com.josev001.dscommerce.dto.CategoryDTO;
import com.josev001.dscommerce.dto.ProductMinDTO;
import com.josev001.dscommerce.entities.Category;
import com.josev001.dscommerce.entities.Product;
import com.josev001.dscommerce.repositories.CategoryRepository;
import com.josev001.dscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll();
        return result.stream().map(x -> new CategoryDTO(x)).toList() ;

    }

}
