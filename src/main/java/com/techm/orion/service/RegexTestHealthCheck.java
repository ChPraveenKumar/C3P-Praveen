package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.utility.C3PCoreAppLabels;

public class RegexTestHealthCheck {
	private static final Logger logger = LogManager.getLogger(RegexTestHealthCheck.class);

	public String PreValidationForHealthCheckThroughput(String requestId, String version) throws Exception {
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		data = C3PCoreAppLabels.REGEX_FILTER_PRE_THROUGHPUT.getValue().split("\\|");
		String text = readFile(requestId, version);
		Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);
		while (m.find()) {
			ar = m.group().split(data[2]);
			break;
		}
		while (m.find()) {
			ar = m.group().split(data[2]);
			break;
		}

		data1 = ar[1].trim().split(" ");
		int throughput = ((int) (Double.parseDouble(data1[0]) * 1000));
		logger.info("PreValidationForHealthCheckThroughput - throughput->"+throughput);
		String throughputValue = String.valueOf(throughput);
		return throughputValue;
	}

	public Map<String, String> PreValidationForHealthCheckPing(String requestId, String version) throws Exception {
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String latencyValue = "";
		String[] dataArray = null;
		Map<String, String> hmap = new HashMap<String, String>();

		String frameLoss = "";

		data = C3PCoreAppLabels.REGEX_FILTER_PRE_FRAMELOSS.getValue().split("\\|");

		String text = readFile(requestId, version);
		Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);

		while (m.find()) {
			ar = m.group().split(data[2]);
			data1 = ar[2].trim().split("=");
			frameLoss = String.valueOf(data1[1].charAt(1));
		}

		hmap.put("frameloss", frameLoss);

		Matcher matcher = Pattern.compile("(?m)^(.*?" + "Average =" + ").*ms").matcher(text);

		while (matcher.find()) {

			dataArray = matcher.group().replace("ms", "").trim().split(" ");

			latencyValue = dataArray[dataArray.length - 1];

		}
		hmap.put("latency", latencyValue);

		return hmap;
	}

	private static String readFile(String requestIdForConfig, String version) throws IOException {

		BufferedReader br = new BufferedReader(
				new FileReader(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version + "_HealthCheck.txt"));
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

}
