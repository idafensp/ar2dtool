package es.upm.oeg.webAR2DTool.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import es.upm.oeg.webAR2DTool.responses.WebConfig;
import es.upm.oeg.webAR2DTool.threads.WebAR2DToolThread;
import es.upm.oeg.webAR2DTool.utils.Constants;

public class AR2DToolManager {
	private static final Logger logger = Logger.getLogger(Constants.WEBAPP_NAME);
	
	private final String sessionID;
	private final File file;
	private final File workspaceFolder;
	private Timer timer;
	private WebAR2DToolThread thread;
	
	public AR2DToolManager(String sessionID,File file, File workspaceFolder){
		this.sessionID = sessionID;
		this.file = file;
		this.workspaceFolder = workspaceFolder;
		this.timer = new Timer();
		this.thread = null;
	}
	
	public AR2DToolManager(String sessionID,String ontUri, File workspaceFolder){
		this.sessionID = sessionID;
		String [] splitUri = ontUri.split("/");
		String uriFile = splitUri[splitUri.length-1];
		this.file = new File(workspaceFolder,uriFile+".rdf");
		if(file.exists()){
			file.delete();
		}
		try {
			if(file.createNewFile()){
				Writer writer = new FileWriter(file);
				writer.write("<?xml version=\"1.0\"?>\n");
				writer.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n");
				writer.write("\t<owl:Ontology rdf:about=\"\">\n");
				writer.write("\t\t<owl:imports rdf:resource=\""+ontUri+"\"/>\n");
				writer.write("\t</owl:Ontology>\n");
				writer.write("</rdf:RDF>");
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Can not save to file uri:"+ontUri,e);
		}
		this.workspaceFolder = workspaceFolder;
		this.timer = new Timer();
		this.thread = null;
	}
	
	public void cancelTimeout(){
		try{
			timer.cancel();
			timer = new Timer();
		}catch (Exception e){
			logger.log(Level.SEVERE,"Can not cancel timeout time for sessionID: "+sessionID,e);
		}
	}
	
	public boolean removeWorkspaceFolder(){
		try {
			FileUtils.deleteDirectory(workspaceFolder);
			return true;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Can not remove workspace: "+workspaceFolder.getAbsolutePath()+" sessionID:"+sessionID, e);
			return false;
		}
	}
	
	public File getWorkspaceFolder(){
		return workspaceFolder;
	}
	
	public void createNewThread(WebConfig config){
		thread = new WebAR2DToolThread(config, file.getAbsolutePath());
	}
	
	public File getImage(){
		if(thread==null){
			return null;
		}
		return thread.getGeneratedImage();
	}
	
	public File getGrapml(){
		if(thread==null){
			return null;
		}
		String graphmlPath = file.getAbsolutePath()+".graphml";
		File graphml = new File(graphmlPath);
		return graphml;
	}
	
	public File getDot(){
		if(thread==null){
			return null;
		}
		String dotPath = file.getAbsolutePath()+".dot";
		File dot = new File(dotPath);
		return dot;
	}
	
	public File getLog(){
		if(thread==null){
			return null;
		}
		String logPath = file.getAbsolutePath()+".log";
		File log = new File(logPath);
		return log;
	}
	
	public void destroy() {
		cancelTimeout();
		try{
			if(thread!=null && thread.isAlive()){
				thread.interrupt();
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,"Can not interrupt thread for sessionID: "+sessionID,e);
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
