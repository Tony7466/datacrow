package org.datacrow.core;

import org.apache.logging.log4j.LogManager;

public class DcLogManager {
	
	public static org.apache.logging.log4j.Logger getLogger(String className) {
		return LogManager.getLogger(className);
	}
	
	public static org.apache.logging.log4j.Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}	
}
