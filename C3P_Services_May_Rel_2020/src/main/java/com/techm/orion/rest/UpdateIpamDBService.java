package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.EIPAMEntity;
import com.techm.orion.entitybeans.GlobalLstInterfaceRqst;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.Vendor_devicetypes;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.pojo.UserValidationResultDetailPojo;
import com.techm.orion.repositories.EIPAMEntityRepository;
import com.techm.orion.service.DcmConfigService;

@Controller
@RequestMapping("/UpdateIpamDBService")
public class UpdateIpamDBService implements Observer {

	@Autowired
	EIPAMEntityRepository eipamEntityRepository;
	
	RequestInfoDao requestInfoDao = new RequestInfoDao();

	private String siteid;

	private String customerName;

	private String zipAdd;

	private String maskAdd;

	private String serviceAdd;

	private String regionAdd;

	@POST
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response update(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		
		EIPAMEntity entity = new EIPAMEntity();

		List<EIPAMEntity>detailList=new ArrayList<EIPAMEntity>();
		detailList = eipamEntityRepository.findAll();
		
		boolean result = false;
		try {
			Gson gson = new Gson();
			
			EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);
			int id=dto.getId();
			String customerName = dto.getCustomer();
			String siteid = dto.getSite();
			String ipAdd = dto.getIp();
			String maskAdd = dto.getMask();
			
			boolean flag=false;
			if(flag==false)
			{
				String ipEdit = dto.getIp();
				String maskEdit = dto.getMask();
				
			
				for(int i=0; i<detailList.size(); i++)
				{
					if(detailList.get(i).getCustomer().equalsIgnoreCase(dto.getCustomer()) && detailList.get(i).getSite().equalsIgnoreCase(dto.getSite()))
					{
					detailList.get(i).setIp(ipEdit);
					detailList.get(i).setMask(maskEdit);
					eipamEntityRepository.save(detailList);
				}
				
			}
			}
			
			if (flag==false) {
				
				obj.put(new String("output"), "Updated Successfully");
				
			} 
			else {
				obj.put(new String("output"), "Error Saving Data");
			}
		

		} catch (Exception e) {
			System.out.println(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();
	}

	@POST
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response add(@RequestBody String searchParameters) {
        
	
		JSONObject obj = new JSONObject();
        EIPAMEntity entity = new EIPAMEntity();
        List<EIPAMEntity>exsistingRecs=new ArrayList<EIPAMEntity>();
        exsistingRecs = eipamEntityRepository.findAll();
		
        
        Gson gson = new Gson();
        
        EIPAMEntity dto = gson.fromJson(searchParameters, EIPAMEntity.class);
       String ipAdd = dto.getIp();
		
		
        boolean flag=false;
		
		
		
		for(int i=0; i<exsistingRecs.size();i++)
		{
			if(exsistingRecs.get(i).getIp().equalsIgnoreCase(ipAdd))
			{
				flag=true;
				obj.put(new String("output"), "IP already exists");
				break;

			}
			
		}
        
		try {
			
			
			if(flag==false)
			{
		   eipamEntityRepository.save(dto);
			}
			
			if(flag==false){
				
		    obj.put(new String("output"), "Added Successfully");
		    entity.setStatus(0);
		    eipamEntityRepository.save(dto);
			} 
			else {
				obj.put(new String("output"), "IP already exists");
			}
			
		}
		
		catch (Exception e) {
			System.out.println(e);
			return Response
					.status(200)
					.entity("Data already added and mapped please change Data")
					.build();
			
		}
		
		
		return Response.status(200).entity(obj)
				.build();
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
