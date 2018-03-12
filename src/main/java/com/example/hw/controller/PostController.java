package com.example.hw.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.hw.UserSession;
import com.example.hw.entity.Category;
import com.example.hw.entity.Post;
import com.example.hw.entity.Weather;
import com.example.hw.infrastructure.dao.CategoryDao;
import com.example.hw.infrastructure.dao.PostDao;
import com.example.hw.repository.CategoryRepository;
import com.example.hw.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/")
public class PostController {

	@Autowired
	private PostDao postDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ConnectionRepository connectionRepository;

	@RequestMapping(value = "/post/write", method = RequestMethod.GET)
	public String form(Post post, Model model) {

		List<Category> categoryList = categoryDao.findAll();

		log.debug("categoryList = {}", categoryList);

		model.addAttribute("categoryMap",
				categoryList.stream().collect(Collectors.toMap(Category::getId, Category::getName)));

		return "form";
	}

	@RequestMapping(value = "/post/write", method = RequestMethod.POST)
	public String write(@Valid Post post, BindingResult bindingResult, UserSession user) {

		if (bindingResult.hasErrors()) {
			return "form";
		}

		post.setRegDate(LocalDateTime.now());
		post.setUserId(user.getProviderUserId());
		post.setName(user.getDisplayName());
		return "redirect:/post/" + postDao.save(post).getId();
	}

	@RequestMapping("/post/{id}")
	public String view(Model model, @PathVariable int id) {
		Post post = postDao.findOne(id);
		model.addAttribute("post", post);

		return "post";
	}

	@RequestMapping("/post/{id}/delete")
	public String delete(@PathVariable int id, UserSession user) {

		Post post = postDao.findOne(id);
		if (post.getUserId().equals(user.getProviderUserId())) {
			postDao.delete(id);
		}

		return "redirect:/post/list";
	}

	@RequestMapping(value = "/post/{id}/edit", method = RequestMethod.GET)
	public String editor(Model model, @PathVariable int id, UserSession user) {

		Post post = postDao.findOne(id);
		if (post.getUserId().equals(user.getProviderUserId())) {
			model.addAttribute("post", post);
		}

		model.addAttribute("categoryMap", categoryRepository.getCategoryMap());

		return "form";
	}

	@RequestMapping(value = "/post/{id}/edit", method = RequestMethod.POST)
	public String edit(@Valid Post post, BindingResult bindingResult, UserSession user, Model model) {

		model.addAttribute("categoryMap", categoryRepository.getCategoryMap());

		if (bindingResult.hasErrors()) {
			return "form";
		}

		Post oldPost = postDao.findOne(post.getId());
		if (oldPost.getUserId().equals(user.getProviderUserId())) {
			oldPost.setTitle(post.getTitle());
			oldPost.setSubtitle(post.getSubtitle());
			oldPost.setContent(post.getContent());
			oldPost.setCategoryId(post.getCategoryId());
			return "redirect:/post/" + postDao.save(oldPost).getId();
		}

		return "form";
	}

	@RequestMapping(value = { "/", "/post/list", "/category/{categoryId}/post/list" })
	public String list(Model model, @PathVariable java.util.Optional<Integer> categoryId,
			@PathVariable java.util.Optional<String> tagName,
			@PageableDefault(sort = { "id" }, direction = Direction.DESC, size = 2) Pageable pageable) {

		Page<Post> postPage;
		Double degree = 0.0;

		if (categoryId.isPresent()) {
			postPage = postRepository.getPostList(pageable, categoryId.get());
			model.addAttribute("category", categoryRepository.getCategory(categoryId.get()).getName());
		} else {
			postPage = postRepository.getPostList(pageable);
		}

		model.addAttribute("Weather", Weather());
		model.addAttribute("Degree", Degree(degree));

		model.addAttribute("postPage", postPage);

		return "list";
	}

	public String Degree(Double Degree) {
		// http://api.openweathermap.org/data/2.5/weather?q=seoul&APPID=a0678fc18cd7e7537688102eef6793bb
		URL url;// URL 주소 객체
		URLConnection connection;// URL접속을 가지는 객체
		InputStream is;// URL접속에서 내용을 읽기 위한 Stream
		InputStreamReader isr;
		BufferedReader br;

		try {
			// URL객체를 생성하고 해당 URL로 접속한다..
			url = new URL(
					"http://api.openweathermap.org/data/2.5/weather?q=seoul&APPID=a0678fc18cd7e7537688102eef6793bb");
			connection = url.openConnection();
			// 내용을 읽어오기위한 InputStream객체를 생성한다.
			is = connection.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			String buf = null;
			String json = ""; // JSON 결과를 String 생성

			while (true) {
				buf = br.readLine();
				if (buf == null)
					break;
				json = buf; // JSON 결과 저장
			}

			System.out.println(json);

			JSONParser parser = new JSONParser();
			JSONObject temp1 = (JSONObject) parser.parse(json);
			JSONObject temp2 = (JSONObject) temp1.get("main");

			Degree = (double) temp2.get("temp") - 273.15;

		} catch (MalformedURLException mue) {
			// TODO Auto-generated catch block
			System.err.println("잘못된 URL입니다.");
			System.exit(1);
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			System.err.println("IOException " + ioe);
			ioe.printStackTrace();
			System.exit(1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return String.format("%.1f 'C", Degree);
	}

	public String Weather() {
		URL url;// URL 주소 객체
		URLConnection connection;// URL접속을 가지는 객체
		InputStream is;// URL접속에서 내용을 읽기 위한 Stream
		InputStreamReader isr;
		BufferedReader br;
		String weather = "null";
		try {
			// URL객체를 생성하고 해당 URL로 접속한다..
			url = new URL(
					"http://api.openweathermap.org/data/2.5/weather?q=seoul&APPID=a0678fc18cd7e7537688102eef6793bb");
			connection = url.openConnection();
			// 내용을 읽어오기위한 InputStream객체를 생성한다.
			is = connection.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			String buf = null;
			String json = ""; // JSON 결과를 String 생성

			while (true) {
				buf = br.readLine();
				if (buf == null)
					break;
				json = buf; // JSON 결과 저장
			}

			System.out.println(json);

			JSONParser parser = new JSONParser();
			JSONObject temp1 = (JSONObject) parser.parse(json);
			JSONArray temp2 = (JSONArray) temp1.get("weather");

			temp1 = (JSONObject) temp2.get(0);

			weather = (String) temp1.get("main");

		} catch (MalformedURLException mue) {
			// TODO Auto-generated catch block
			System.err.println("잘못된 URL입니다.");
			System.exit(1);
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			System.err.println("IOException " + ioe);
			ioe.printStackTrace();
			System.exit(1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return weather;
	}
}
