package com.techm.c3p.core.rest;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.repositories.MasterCommandsRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.TemplateFeatureRepo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.TextReport;

@Controller
@RequestMapping("/DownloadBasicConfigFile")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class DownloadBasicConfigFile {
	@Autowired
	private RequestInfoDetailsRepositories reository;
	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;
	@Autowired
	private MasterCommandsRepository masterCommandsRepository;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@Produces("application/x-download")
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET }, value = { "/getBasicConfigFile" })
	public void getBasicConfigFile(HttpServletResponse response, @RequestParam String requestId,
			@RequestParam String version) {
		try {
			String path = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId
					+ "V" + version + "_Configuration";
			
			String textData = "";
			
			RequestInfoEntity req = reository.findByAlphanumericReqIdAndRequestVersion(requestId, Double.valueOf(version));
			List<TemplateFeatureEntity> templateFeatureEntity = templateFeatureRepo
					.findByCommandType(req.getTemplateUsed());
			List<CommandPojo> commandValue = new ArrayList<>();
			templateFeatureEntity.forEach(templateFeature -> {
				if (templateFeature.getComandDisplayFeature().contains("Basic Configuration")) {
					commandValue.addAll(masterCommandsRepository.findByCommandId(templateFeature.getId()));
				}
			});
			for(CommandPojo commands : commandValue) {
				CommandPojo commandpojo = new CommandPojo();
				commandpojo.setCommand_value(commands.getCommand_value());
				commandpojo.setCommand_sequence_id(commands.getCommand_sequence_id());
				textData = textData+commands.getCommand_value();
			}
			TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), requestId
					+ "V" + version + "_basicConfiguration.txt",textData);	
			
			File file = new File(path);
			if (!file.exists()) {

				response.setHeader("Access-Control-Expose-Headers", "*");
				response.sendError(0, "file not found");
			} else {
				response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Content-Disposition",
						"attachment; filename=" + requestId + "V" + version + "_basicConfiguration.txt");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("txt/plain");
				FileInputStream fileIn = new FileInputStream(file);
				IOUtils.copy(fileIn, response.getOutputStream());
				fileIn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
