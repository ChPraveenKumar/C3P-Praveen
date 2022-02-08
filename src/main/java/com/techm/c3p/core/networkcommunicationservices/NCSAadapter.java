package com.techm.c3p.core.networkcommunicationservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.UtilityMethods;

@Component
public class NCSAadapter {
	@Autowired
	private ChannelElements channelElements;


	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	
	private static final Logger logger = LogManager
			.getLogger(NCSAadapter.class);
	private static final String JSCH_CONFIG_INPUT_BUFFER = "max_input_buffer_size";

	public Session getSession(String username, String host, String pwd) {
		Session session = null;
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(username, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(pwd);
			session.connect();
			UtilityMethods.sleepThread(6000);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

	public ChannelElements channelElements(Session session, RequestInfoPojo requestinfo, String type,RequestInfoDetailsDao requestInfoDetailsDao)
	{
		try {
		Channel channel = session.openChannel("shell");
		OutputStream ops = channel
				.getOutputStream();

		PrintStream ps = new PrintStream(ops,
				true);
		InputStream input = channel
				.getInputStream();
		channel.connect();
		// conduct and analyse the tests
		ps = requestInfoDetailsDao
				.setCommandStream(ps,
						requestinfo, type,
						false);
		channelElements.setChannel(channel);
		channelElements.setInputStrm(input);
		channelElements.setPs(ps);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return channelElements;
	}
}
