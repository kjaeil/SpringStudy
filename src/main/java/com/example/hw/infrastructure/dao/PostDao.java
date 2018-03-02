package com.example.hw.infrastructure.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hw.entity.Post;

public interface PostDao extends JpaRepository<Post, Integer> {
	public Page<Post> findByCategoryId(int categoryId, Pageable pageable);

}
