package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;

import com.techm.orion.pojo.HealthCheckComponent;

public class HealthCheckReport {
	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	private static String readFile(String path) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public String getHealthCheckReport(String hostname, String region) {
		String result = null;
		try {
			HealthCheckReport.loadProperties();
			String filepath = null;
			filepath = HealthCheckReport.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder") + "//"
					+ hostname + "_" + region + "_HealthCheckReport.html";
			StringBuilder contentBuilder = new StringBuilder();
			BufferedReader in = new BufferedReader(new FileReader(filepath));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
			result = contentBuilder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public boolean createFailureReport(List<HealthCheckComponent> resultArray, String routername, String region,
			String state) {
		boolean status = false;
		try {
			HealthCheckReport.loadProperties();

			String filepath = null;
			filepath = HealthCheckReport.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder") + "//" + state
					+ "_" + routername + "_" + region + "_HealthCheckReport.html";
			File file = new File(filepath);

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			Formatter fmt = new Formatter();

			try {
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pw = new PrintWriter(fw, true);

				if (resultArray != null) {
					pw.println("<html>");
					pw.println("<head>");
					pw.println("</head>");
					pw.println("<body>");
					pw.println("JSCH Connection Exception, Auth Fail/ Router refused the connection");
					pw.println("</body>");
					pw.println("</html>");
					pw.close();
				} else {
					pw.println("<html>");
					pw.println("<head>");
					pw.println("</head>");
					pw.println("<body>");
					pw.println("Could not conduct Health Check");
					pw.println("</body>");
					pw.println("</html>");
					pw.close();
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	public boolean createReport(List<HealthCheckComponent> resultArray, String routername, String region, String state)
			throws IOException {
		HealthCheckReport.loadProperties();
		boolean status = false;
		String filepath = null;
		filepath = HealthCheckReport.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder") + "//" + state
				+ "_" + routername + "_" + region + "_HealthCheckReport.html";
		File file = new File(filepath);

		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		Formatter fmt = new Formatter();

		try {
			FileWriter fw = new FileWriter(file, true);
			PrintWriter pw = new PrintWriter(fw, true);
			/*
			 * for(int i=0;i<resultArray.size();i++) {
			 * 
			 * if(resultArray.get(i).getTestresult().equalsIgnoreCase("pass")) {
			 * pw.println(fmt.format("%20s %20s %20s%n", resultArray.get(i).getTestname(),
			 * resultArray.get(i).getTestresult(), " ")); } else {
			 * pw.println(fmt.format("%20s %20s %20s%n", resultArray.get(i).getTestname(),
			 * "Fail", resultArray.get(i).getTestresult()));
			 * 
			 * }
			 * 
			 * }
			 */
			if (resultArray != null) {
				pw.println("<html>");
				pw.println("<head>");
				pw.println("</head>");
				pw.println("<body>");
				pw.println("<table border=\"1\">");
				pw.println("<tr>");
				pw.println("<th>" + "Test Name" + "</th>");

				pw.println("<th>" + "Result" + "</th>");

				pw.println("<th>" + "Reason" + "</th>");

				pw.println("</tr>");

				for (int i = 0; i < resultArray.size(); i++) {
					pw.println("<tr>");
					pw.println("<td>" + resultArray.get(i).getTestname() + "</td>");
					if (resultArray.get(i).getTestresult().equalsIgnoreCase("pass")) {
						pw.println("<td>" + "Pass" + "</td>");
						pw.println("<td>" + " " + "</td>");
					} else {
						pw.println("<td>" + "Fail" + "</td>");
						pw.println("<td>" + resultArray.get(i).getTestresult() + "</td>");
					}

					pw.println("</tr>");

				}

				pw.println("</table>");
				pw.println("</body>");
				pw.println("</html>");
				pw.close();
			} else {
				pw.println("<html>");
				pw.println("<head>");
				pw.println("</head>");
				pw.println("<body>");
				pw.println("Could not conduct Health Check");
				pw.println("</body>");
				pw.println("</html>");
				pw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
}
