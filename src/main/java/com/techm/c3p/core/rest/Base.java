package com.techm.c3p.core.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Base {
	private static final Logger logger = LogManager.getLogger(Base.class);

	private static void display() {

		logger.info("Static or class method from Base");
	}

	public void print() {
		logger.info("Non-static or instance method from Base");
	}
}