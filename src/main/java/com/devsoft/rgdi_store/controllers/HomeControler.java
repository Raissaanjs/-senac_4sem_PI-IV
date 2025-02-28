package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControler {

	//defino o início da raiz do projeto (que é o contexto)
	@GetMapping("/auth")
	public String home() {
		return "/home";
	}
}
