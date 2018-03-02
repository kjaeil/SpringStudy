package com.example.hw.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hw.entity.Hello;

public interface HelloDao extends JpaRepository<Hello, Integer> {

}
