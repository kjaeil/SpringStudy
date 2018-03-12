package com.example.hw.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Post {

	@Id
	@GeneratedValue
	private int id;

	private String userId;
	private String name;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(nullable = false)
	private String title;

	@Size(max = 255)
	private String subtitle;

	@NotNull
	@Size(min = 1, max = 100000000)
	@Column(length = 100000000)
	private String content;

	private String _csrf;

	private LocalDateTime regDate;

	@Min(value = 1)
	private int categoryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId", insertable = false, updatable = false)
	private Category category;
}