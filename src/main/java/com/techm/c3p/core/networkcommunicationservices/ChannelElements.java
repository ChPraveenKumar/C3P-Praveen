package com.techm.c3p.core.networkcommunicationservices;

import java.io.InputStream;
import java.io.PrintStream;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
@Component
public class ChannelElements {
	private PrintStream ps = null;
	private Channel channel = null;
	private InputStream input = null;
	public PrintStream getPs() {
		return ps;
	}
	public void setPs(PrintStream ps) {
		this.ps = ps;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public InputStream getInputStrm() {
		return input;
	}
	public void setInputStrm(InputStream input) {
		this.input = input;
	}
}
