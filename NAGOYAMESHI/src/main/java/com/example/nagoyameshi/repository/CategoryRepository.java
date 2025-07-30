package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByNameContainingIgnoreCaseOrderByIdAsc(String name);

    boolean existsByNameIgnoreCase(String name);
    
    List<Category> findAllByOrderByNameAsc();
    List<Category> findAllByOrderByIdAsc();
}
