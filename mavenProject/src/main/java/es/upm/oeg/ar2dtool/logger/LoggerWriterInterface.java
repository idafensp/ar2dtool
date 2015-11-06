package es.upm.oeg.ar2dtool.logger;

import java.util.logging.Level;

public interface LoggerWriterInterface {
	public void log(String msg);
	
	public void log(String msg, Level logLevel);

	public void log(Exception ex, Level logLevel);
	
	public void setLogLevelDefault(Level logLevelDefault);
	
	public void setVisibleLogLevel(Level visibleLevel);
	
	public Level getVisibleLogLevel();

}
