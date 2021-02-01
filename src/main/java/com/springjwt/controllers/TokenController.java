package com.springjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springjwt.security.jwt.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/token")
public class TokenController {

	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/revoke/{access_token}")
	public ResponseEntity<Object> userAccess(@PathVariable("access_token") String token, Authentication auth) {
		return jwtUtils.revokeToken(auth, token) ? ResponseEntity.ok("success") : ResponseEntity.ok("Fail");
	}
}
