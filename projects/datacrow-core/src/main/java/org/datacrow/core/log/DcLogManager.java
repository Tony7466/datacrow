package org.datacrow.core.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.datacrow.core.DcConfig;

public class DcLogManager {
	
	public static org.apache.logging.log4j.Logger getLogger(String className) {
		return LogManager.getLogger(className);
	}
	
	public static org.apache.logging.log4j.Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}
	
	public static void configureLog4j(boolean debug) {
	    ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.WARN);
        builder.setConfigurationName("DataCrow_Log4j2");
            
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout");
        layoutBuilder.addAttribute("pattern", "%d [%t] %-5level: %msg%n");

        ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies");
        triggeringPolicy.addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"));
        triggeringPolicy.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1MB")); 

        AppenderComponentBuilder appenderBuilder = builder.newAppender("RollingFile", "RollingFile");
        appenderBuilder.addAttribute("fileName", DcConfig.getInstance().getDataDir() + "data_crow.log");
        appenderBuilder.addAttribute("filePattern", DcConfig.getInstance().getDataDir() + "data_crow-%d{MM-dd-yy}.log.gz");
        appenderBuilder.addAttribute("ignoreExceptions", "false");
        
        appenderBuilder.add(layoutBuilder);
        appenderBuilder.addComponent(triggeringPolicy);
        
        builder.add(appenderBuilder);

        LoggerComponentBuilder loggerComponentBuilder = builder.newLogger("DcLogger", Level.TRACE);
        loggerComponentBuilder.add(builder.newAppenderRef("RollingFile"));
        loggerComponentBuilder.addAttribute("additivity", false);
        
        
        // create a console appender
        appenderBuilder = builder.newAppender("Console", "CONSOLE");
        appenderBuilder.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        
        layoutBuilder = builder.newLayout("PatternLayout");
        layoutBuilder.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
        
        appenderBuilder.add(layoutBuilder);
        builder.add(appenderBuilder);
        
        // create a log  appender
        appenderBuilder = builder.newAppender("DcLogAppender", "DcLogAppender");
        layoutBuilder = builder.newLayout("PatternLayout");
        layoutBuilder.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"); 
        
        appenderBuilder.add(layoutBuilder);
        builder.add(appenderBuilder);
        
        // create the new logger
        builder.add(loggerComponentBuilder);
        
        RootLoggerComponentBuilder rootLoggerComponentBuilder = builder.newRootLogger(Level.TRACE);
        rootLoggerComponentBuilder.add(builder.newAppenderRef("Console"));
        rootLoggerComponentBuilder.add(builder.newAppenderRef("RollingFile"));
        rootLoggerComponentBuilder.add(builder.newAppenderRef("DcLogAppender"));

        builder.add(rootLoggerComponentBuilder);

        BuiltConfiguration config = builder.build();
        Configurator.reconfigure(config);
	}
}
