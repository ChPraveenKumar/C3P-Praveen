package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.UtilityMethods;

public class CSVWriteAndConnectPython {
	private static final Logger logger = LogManager.getLogger(CSVWriteAndConnectPython.class);

	public String ReadWriteAndConnectAnalyser(CreateConfigRequest configRequest) throws IOException {
		PrintWriter pw = new PrintWriter(new File(TSALabels.ANALYSER_PATH.getValue() + "SOThrData.csv"));

		StringBuilder sb = new StringBuilder();

		sb.append(configRequest.getC3p_interface().getBandwidth());
		sb.append(',');
		sb.append(configRequest.getThroughput());
		sb.append(',');
		sb.append("Pass");
		sb.append(',');
		sb.append("T");

		pw.write(sb.toString());
		pw.flush();
		pw.close();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;

		String line = "";
		String cvsSplitBy = ",";
		String result = "";
		BufferedReader br = null;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			p_stdin.write("cd " + TSALabels.ANALYSER_PATH.getValue());
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python testing.py");
			p_stdin.newLine();
			p_stdin.flush();

			UtilityMethods.sleepThread(17000);

			br = new BufferedReader(
					new FileReader(TSALabels.ANALYSER_PATH.getValue() + "SOTrainingData_C3P_Throughput_data.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] throuhput = line.split(cvsSplitBy);
				if (throuhput[5] != null) {
					result = throuhput[5];
					logger.info(result);
				} else {
					result = "Test not conducted";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(br !=null) {
					br.close();
				}
			}catch(IOException ioExe) {
				
			}
		}

		return result;

	}

	/* method overloding for UIRevamp */
	public String ReadWriteAndConnectAnalyser(RequestInfoPojo requestinfo) throws IOException {
		PrintWriter pw = new PrintWriter(new File(TSALabels.ANALYSER_PATH.getValue() + "SOThrData.csv"));

		StringBuilder sb = new StringBuilder();

		// sb.append(configRequest.getC3p_interface().getBandwidth());
		// sb.append(',');
		sb.append(requestinfo.getThroughput());
		sb.append(',');
		sb.append("Pass");
		sb.append(',');
		sb.append("T");

		pw.write(sb.toString());
		pw.flush();
		pw.close();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;

		String line = "";
		String cvsSplitBy = ",";
		String result = "";
		BufferedReader br = null;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			p_stdin.write("cd " + TSALabels.ANALYSER_PATH.getValue());
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python testing.py");
			p_stdin.newLine();
			p_stdin.flush();

			try {
				Thread.sleep(17000);
			} catch (Exception ee) {
			}

			br = new BufferedReader(
					new FileReader(TSALabels.ANALYSER_PATH.getValue() + "SOTrainingData_C3P_Throughput_data.csv"));
			while ((line = br.readLine()) != null) {				
				// use comma as separator
				String[] throuhput = line.split(cvsSplitBy);
				if (throuhput!=null && throuhput.length>3 && throuhput[4] != null) {
					result = throuhput[4];
					logger.info(result);
				} else {
					result = "Test not conducted";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(br !=null) {
					br.close();
				}
			}catch(IOException ioExe) {
				
			}
		}

		return result;

	}
}
