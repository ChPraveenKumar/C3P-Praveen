package com.techm.c3p.core.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MVCController {
	@RequestMapping("/home")
	public String helloToIndex(Model model) {
		// model.addAttribute("name", name);

		return "index";
	}
}
