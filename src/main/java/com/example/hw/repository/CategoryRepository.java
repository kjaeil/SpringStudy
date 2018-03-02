package com.example.hw.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.hw.entity.Category;
import com.example.hw.infrastructure.dao.CategoryDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CategoryRepository {

	@Autowired
	private CategoryDao categoryDao;

	public Category getCategory(int categoryId) {

		Category category = categoryDao.getOne(categoryId);
		log.debug("category = {}", category);

		return category;
	}

	public List<Category> getCategoryList() {

		List<Category> categoryList = categoryDao.findAll();
		log.debug("categoryList = {}", categoryList);

		return categoryList;
	}

	public Map<Integer, String> getCategoryMap() {
		return getCategoryList().stream().collect(Collectors.toMap(Category::getId, Category::getName));
	}
}
