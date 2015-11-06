package es.upm.oeg.ar2dtool.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultLoggerWriter implements LoggerWriterInterface {
	
	private String loggerName;
	private Logger log;
	private Level logLevelToSee = Level.INFO;
	private Level logLevel = Level.FINE;
	
	public DefaultLoggerWriter(String loggerName) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		log.setLevel(logLevelToSee);
	}
	
	public DefaultLoggerWriter(String loggerName, Level visibleLevel) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		logLevelToSee = visibleLevel;
		log.setLevel(logLevelToSee);
	}
	public DefaultLoggerWriter(String loggerName,Level visibleLevel, Level logLevelDefault) {
		this.loggerName = loggerName;
		log = Logger.getLogger(loggerName);
		logLevelToSee = visibleLevel;
		log.setLevel(logLevelToSee);
		logLevel = logLevelDefault;
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
}
