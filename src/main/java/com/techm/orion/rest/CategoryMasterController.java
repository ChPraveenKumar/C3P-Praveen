package com.techm.orion.rest;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.CategoryMasterService;

@RestController
@RequestMapping
public class CategoryMasterController {

	@Autowired
	CategoryMasterService categoryService;

	
	@RequestMapping(value = "/category",method = RequestMethod.GET,produces = "application/json")
	public Response getAllCategory() {
		return Response.status(200).entity(categoryService.getAll()).build();
	}


}
