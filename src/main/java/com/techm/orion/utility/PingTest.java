package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PingTest {
	private static final Logger logger = LogManager.getLogger(PingTest.class);
	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	public boolean cmdPingCall(String managementIp, String routername, String region) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		boolean flag = true;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			String commandToPing = "ping " + managementIp + " -n 20";
			logger.info("Management ip " + managementIp);
			p_stdin.write(commandToPing);
			p_stdin.newLine();
			p_stdin.flush();
			try {
				Thread.sleep(21000);
			} catch (Exception ee) {
			}
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner s = new Scanner( p.getInputStream() );

		InputStream input = p.getInputStream();
		flag = printResult(input, routername, region);

		return flag;
	}

	private boolean printResult(InputStream input, String routername, String region) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		boolean flag = true;
		PingTest.loadProperties();
		String filepath = PingTest.PROPERTIES.getProperty("responseDownloadPath") + routername + "_" + region
				+ "_Reachability.txt";
		File file = new File(filepath);

		// if file doesnt exists, then create it

		if (!file.exists()) {
			file.createNewFile();

		}

		else {
			file.delete();
			file.createNewFile();
		}
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* logger.info(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);

			if (s.contains("Microsoft")) {
				int startIndex = s.indexOf("Microsoft ");
				int endIndex = s.indexOf(">");
				String toBeReplaced = s.substring(startIndex, endIndex + 1);
				logger.info("\n" + s.replace(toBeReplaced, ""));

				s = s.replace(toBeReplaced, "");
			}
			if (!(s.equals("")) && s.contains("Destination host unreachable")) {

				flag = false;

			} else if (!(s.equals("")) && s.contains("Request timed out.")) {
				flag = false;

			} else if (!(s.equals("")) && s.contains("Destination net unreachable")) {
				flag = false;

			}

			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.append(s);
			bw.close();

		}

		return flag;
	}

	public String readResult(String managementIp, String routername, String region) {
		String result = null;

		try {
			PingTest.loadProperties();

			String filepath = PingTest.PROPERTIES.getProperty("responseDownloadPath") + routername + "_" + region
					+ "_Reachability.txt";
			result = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String pingResults(String managementIp) {
		logger.info("Start getPingResults - managementIp- "+managementIp);
		long startTime = System.currentTimeMillis();
		StringBuilder commadBuilder = new StringBuilder();
		String responce = null;
		try {
			commadBuilder.append("ping ");
			commadBuilder.append(managementIp);
			//Pings timeout
			if("Linux".equals(TSALabels.APP_OS.getValue())) {
				commadBuilder.append(" -c ");
			}else {
				commadBuilder.append(" -n ");
			}
			//Number of pings
			commadBuilder.append("5");
			logger.info("commandToPing -"+commadBuilder);		
			Process process = Runtime.getRuntime().exec(commadBuilder.toString());
			responce = getPingResults(process);
		}catch(IOException exe) {
			logger.error("Exception in pingResults - "+exe.getMessage());
		}
		logger.info("Total time taken to getPingResults in millsecs- "+ (System.currentTimeMillis()-startTime));
		return responce;
	}
	
	public String getPingResults(Process process) {
		logger.info("Start getPingResults - "+process);
		long startTime = System.currentTimeMillis();
		StringBuilder resultBuilder = new StringBuilder();
		String responce = null;
		try(
				BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			){
			if(errorStream.readLine() != null) {
				resultBuilder.append("Error");
				resultBuilder.append("\n");
				while ((responce = errorStream.readLine()) != null) {
					resultBuilder.append(responce);
					resultBuilder.append("\n");
		        }
			}else {
				while ((responce = inputStream.readLine()) != null) {
					resultBuilder.append(responce);
					resultBuilder.append("\n");
		        }
			}
			logger.info("getPingResults - "+resultBuilder);
		}catch(IOException exe) {
			logger.error("getPingResults Error - "+exe.getMessage());
		}
		logger.info("Total time taken to getPingResults in millsecs- "+ (System.currentTimeMillis()-startTime));
		return resultBuilder.toString();
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
}
