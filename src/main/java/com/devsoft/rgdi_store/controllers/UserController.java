package com.devsoft.rgdi_store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.services.UserService;

@RestController
@RequestMapping(value = "/usuarios")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public List<UserDto> findAll(){
		return userService.findAll();
	}
	
	@GetMapping(value = "/{id}")
	public UserDto findById(@PathVariable Long id) {
		UserDto dto = userService.findById(id);
		return dto;
	}

}
