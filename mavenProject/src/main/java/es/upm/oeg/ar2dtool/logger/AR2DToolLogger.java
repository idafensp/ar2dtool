package es.upm.oeg.ar2dtool.logger;

import java.util.HashMap;
import java.util.Map;

public class AR2DToolLogger {
	
	private String loggerName="AR2DTool";
	private LoggerWriterInterface logger = new DefaultLoggerWriter(loggerName);
	
	private AR2DToolLogger(){
	}
	
	private AR2DToolLogger(String loggerName){
		this.loggerName = loggerName;
		logger = new DefaultLoggerWriter(loggerName);
	}
	
	public LoggerWriterInterface getWriter(){
		return logger;
	}
	
	public void setWriter(LoggerWriterInterface logger){
		this.logger = logger;
	}
	
	//AR2DToolLoggerManager
	private static Map<String,AR2DToolLogger> loggers=new HashMap<String,AR2DToolLogger>();
	public static AR2DToolLogger getLogger(String name){
		if(loggers.containsKey(name)){
			return loggers.get(name);
		}else{
			AR2DToolLogger logger = new AR2DToolLogger(name);
			loggers.put(name, logger);
			return logger;
		}
	}
}
