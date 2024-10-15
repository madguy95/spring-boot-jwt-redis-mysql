package com.springjwt.controllers;

import com.github.javafaker.Faker;
import com.springjwt.exception.ApiResponseFactory;
import com.springjwt.exception.PagedApiResponse;
import com.springjwt.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	@GetMapping("/getList")
	public ResponseEntity<PagedApiResponse<User>> getListData() {
		Faker faker = new Faker(new Locale("en-US"));
		// Danh sách để lưu trữ dữ liệu
		List<User> userList = new ArrayList<>();

		// Tạo 10 đối tượng User giả
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setId(faker.number().randomNumber());
			user.setUsername(faker.name().username());
			user.setPassword(faker.internet().password());
			user.setEmail(faker.internet().emailAddress());
			userList.add(user);
		}
		return ApiResponseFactory.pagedResponse(userList, 1, 10, userList.size());
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
