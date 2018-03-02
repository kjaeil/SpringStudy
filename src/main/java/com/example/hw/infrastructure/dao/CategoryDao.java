package com.example.hw.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hw.entity.Category;
 
public interface CategoryDao extends JpaRepository<Category, Integer>{
 
}