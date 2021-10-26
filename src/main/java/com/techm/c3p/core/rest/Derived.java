package com.techm.c3p.core.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Derived extends Base {
	private static final Logger logger = LogManager.getLogger(Derived.class);

	private static void display() {
		logger.info("Static or class method from Derived");
	}

	public void print() {
		logger.info("Non-static or instance method from Derived");
	}
}
