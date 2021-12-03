package com.techm.c3p.core.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.springframework.util.FileCopyUtils;

import java.io.BufferedInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.c3p.core.utility.C3PCoreAppLabels;

@RestController
@RequestMapping("/download")
public class C3PDownloadController {
	private static final Logger logger = LogManager
			.getLogger(C3PDownloadController.class);

	@RequestMapping("/file/{fileName:.+}")
	public void downloadPDFResource(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("fileName") String fileName) throws IOException {

		File file = new File(C3PCoreAppLabels.DOWNLOAD_PATH.getValue()
				+ fileName);
		if (file.exists()) {

			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file
					.getName());
			if (mimeType == null) {
				// unknown mimetype so set the mimetype to
				// application/octet-stream
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);

			/**
			 * In a regular HTTP response, the Content-Disposition response
			 * header is a header indicating if the content is expected to be
			 * displayed inline in the browser, that is, as a Web page or as
			 * part of a Web page, or as an attachment, that is downloaded and
			 * saved locally.
			 * 
			 */

			/**
			 * Here we have mentioned it to show inline
			 */
			response.setHeader("Content-Disposition", String
					.format("inline; filename=\"" + file.getName() + "\""));

			// Here we have mentioned it to show as attachment
			// response.setHeader("Content-Disposition",
			// String.format("attachment; filename=\"" + file.getName() +
			// "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(
					new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}
}
