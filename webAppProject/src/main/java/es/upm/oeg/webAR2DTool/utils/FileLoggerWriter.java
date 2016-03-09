package es.upm.oeg.webAR2DTool.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import es.upm.oeg.ar2dtool.logger.LoggerWriterInterface;

public class FileLoggerWriter implements LoggerWriterInterface {

	private String loggerName;
	private Logger log;
	private Level logLevelToSee = Level.INFO;
	private Level logLevel = Level.FINE;
	private FileHandler fh;
	
	public FileLoggerWriter(String loggerName,String filePath) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		log.setLevel(logLevelToSee);
		setLoggerFile(filePath);
	}
	
	public FileLoggerWriter(String loggerName, Level visibleLevel,String filePath) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		logLevelToSee = visibleLevel;
		log.setLevel(logLevelToSee);
		setLoggerFile(filePath);
	}
	public FileLoggerWriter(String loggerName,Level visibleLevel, Level logLevelDefault,String filePath) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		logLevelToSee = visibleLevel;
		log.setLevel(logLevelToSee);
		logLevel = logLevelDefault;
		setLoggerFile(filePath);
	}
	
	@Override
	public void log(Exception ex, Level logLevel) {
		log.log(logLevel, "Exception: ", ex);
	}
	
	@Override
	public void log(String msg, Level logLevel) {
		log.log(logLevel,msg);
	}
	
	@Override
	public void log(String msg) {
		log.log(logLevel,msg);
	}

	@Override
	public void setLogLevelDefault(Level logLevelDefault) {
		logLevel = logLevelDefault;
	}

	@Override
	public void setVisibleLogLevel(Level visibleLevel) {
		logLevelToSee = visibleLevel;
		log.setLevel(logLevelToSee);
	}

	@Override
	public Level getVisibleLogLevel() {
		return logLevelToSee;
	}

	public void close(){
		fh.flush();
		fh.close();
	}
	
	private void setLoggerFile(String filePath){
	    try {  
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(filePath);
	        log.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}
	
}
