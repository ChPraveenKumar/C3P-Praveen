package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;

@Controller
@RequestMapping("/faq")
public class GetFAQ implements Observer {

    RequestInfoDao requestInfoDao = new RequestInfoDao();

    @GET
    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getIP(@RequestParam(value = "page") String searchParameters) {
    	String result=null;
	JSONObject obj = new JSONObject();
	try {
	    Gson gson = new Gson();
	    String jsonArray=null;
		try {
		    // quick fix for json not getting serialized
		    
			result = requestInfoDao.getFAQforPage(searchParameters);
		    
		    if(result==null)
		    {
			    obj.put(new String("output"), "No data found.");

		    }
		    else
		    {
		    	jsonArray = new Gson().toJson(obj);
			    obj.put(new String("output"), result);
		    }

		    
		} catch (Exception e) {
		    System.out.println(e);
		}
	    
	} catch (Exception e) {
	    System.out.println(e);
	}

	return obj;
    }

    @Override
    public void update(Observable o, Object arg) {
	// TODO Auto-generated method stub

    }

}
