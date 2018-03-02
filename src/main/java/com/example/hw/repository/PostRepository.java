package com.example.hw.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.hw.UserSession;
import com.example.hw.entity.Post;
import com.example.hw.infrastructure.dao.PostDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PostRepository {

	@Autowired
	private PostDao postDao;

	public Page<Post> getPostList(Pageable pageable) {
		Page<Post> postPage = postDao.findAll(pageable);
		log.debug("postPage = {}", postPage);

		return postPage;
	}

	public Page<Post> getPostList(Pageable pageable, int categoryId) {
		return postDao.findByCategoryId(categoryId, pageable);
	}

	public Post getPostById(int id) throws IllegalArgumentException {
		Post post = postDao.getOne(id);

		if (post == null) {
			throw new IllegalArgumentException("Post Not Found.");
		}

		return post;
	}

	public boolean isThisUserPostWriter(UserSession user, int id) throws IllegalArgumentException {
		Post post = getPostById(id);

		return post.getUserId().equals(user.getProviderUserId());
	}

}
