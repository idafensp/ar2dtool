package es.upm.oeg.webAR2DTool.managers;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import es.upm.oeg.webAR2DTool.threads.WebAR2DToolThread;
import es.upm.oeg.webAR2DTool.utils.Constants;

public class AR2DToolManager {
	private static final Logger logger = Logger.getLogger(Constants.WEBAPP_NAME);
	
	private final String sessionID;
	private final File file;
	private Timer timer;
	private WebAR2DToolThread thread;
	
	public AR2DToolManager(String sessionID,File file){
		this.sessionID = sessionID;
		this.file = file;
		timer = new Timer();
		thread = null;
	}
	
	public void cancelTimeout(){
		try{
			timer.cancel();
		}catch (Exception e){
			logger.severe("Can not cancel timeout time for sessionID: "+sessionID);
		}
	}
	

	public void destroy() {
		cancelTimeout();
		try{
			if(thread.isAlive()){
				thread.interrupt();
			}
		}catch(Exception e){
			logger.severe("Can not interrupt thread for sessionID: "+sessionID);
		}
	}
	
	public void schedule(TimerTask timerTask,int sessionTimeoutSeconds) {
		timer.schedule(timerTask, getNewTimeoutDate(sessionTimeoutSeconds));		
	}

	public String getSessionID() {
		return sessionID;
	}

	public File getFile() {
		return file;
	}

	public Timer getTimer() {
		return timer;
	}

	public WebAR2DToolThread getThread() {
		return thread;
	}

	private Date getNewTimeoutDate(int sessionTimeoutSeconds) {
    	Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, sessionTimeoutSeconds);
        return calendar.getTime();
	}
	
	
}
