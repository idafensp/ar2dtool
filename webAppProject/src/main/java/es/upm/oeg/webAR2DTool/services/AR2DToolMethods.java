package es.upm.oeg.webAR2DTool.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.oeg.webAR2DTool.managers.AR2DToolManager;
import es.upm.oeg.webAR2DTool.responses.WebConfig;
import es.upm.oeg.webAR2DTool.threads.WebAR2DToolThread;
import es.upm.oeg.webAR2DTool.utils.Constants;
import es.upm.oeg.webAR2DTool.utils.ParameterNames;
import es.upm.oeg.webAR2DTool.utils.WebResponse;


@Path("methods")
public class AR2DToolMethods {

	private static final Logger logger = Logger.getLogger(Constants.WEBAPP_NAME);
	private final Map<String,AR2DToolManager> sessions = new HashMap<String,AR2DToolManager>();

	private String uploadedFilesFolder = "";
	private int sessionTimeoutSeconds = 3600;
	
	public AR2DToolMethods(@Context ServletContext sContext) {
		try{
			WebConfig config =  new WebConfig(sContext);
			config.toConfigValues();
		}catch(Exception e){
			logger.log(Level.SEVERE, "Can not load config values",e);
		}
		try{
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(Constants.SERVER_PROPERTIES)));
			File tempUploadedFilesFolder = new File(prop.getProperty(ParameterNames.PATH_TO_UPLOADED_FILES));
			if(!tempUploadedFilesFolder.exists()){
				if(!tempUploadedFilesFolder.mkdirs()){
					throw new IOException("Can not create folder for uploaded files: "+tempUploadedFilesFolder.getAbsolutePath());
				}
			}
			if(!tempUploadedFilesFolder.isDirectory()){
				throw new IOException(tempUploadedFilesFolder.getAbsolutePath() + " is not a Folder.");
			}
			if(!tempUploadedFilesFolder.canRead() || !tempUploadedFilesFolder.canWrite() || !tempUploadedFilesFolder.canExecute()){
				throw new IOException(tempUploadedFilesFolder.getAbsolutePath() + " can not be readed, writed or executed");
			}
			uploadedFilesFolder = prop.getProperty(ParameterNames.PATH_TO_UPLOADED_FILES);
		}catch(Exception e){
			logger.log(Level.SEVERE, "Can not load "+Constants.SERVER_PROPERTIES + " file",e);
		}
	}
	
	
    @GET
    @Path("oneGet")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefaultConfig(@Context HttpServletRequest request) {
    	String jSessionID = request.getSession(true).getId();
    	return "Default Config.\nYour JSESSIONID="+jSessionID+"\nTo implements";
    }
    
    
    @GET
    @Path("otherGet")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMethod(@Context HttpServletRequest request) {
    	String jSessionID = request.getSession(true).getId();
    	return "Other get.\nYour JSESSIONID="+jSessionID+"\nTo implements";
    }
    
    @GET
    @Path("hasUploadedFile")
    @Produces(MediaType.APPLICATION_JSON)
    public WebResponse hasUploadedFile(@Context HttpServletRequest request){
    	String jSessionID = request.getSession(true).getId();
    	String response = "{\"hasUploadedFile\":\"";
    	response += sessions.containsKey(jSessionID);
    	response += "\"}";
    	return new WebResponse(response,"","");
    }
    
    @GET
    @Path("uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public WebResponse uploadFile(@Context HttpServletRequest request,@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader){
    	//TODO To implement 
    	return null;
    }
    
    @GET
    @Path("getDefaultConfigValues")
    @Produces(MediaType.APPLICATION_JSON)
    public WebResponse getDefaultConfigValues(@Context ServletContext sContext){
    	WebConfig config=null;
    	String idError = "";
    	String error = "";
    	try{
    		config = new WebConfig(sContext);
    		
    	}catch(Exception e){
    		config = null;
    		idError = "UnexpectedException";
    		error = e.getMessage();
    		logger.log(Level.SEVERE, "Error when load config values.", e);
    	}
    	return new WebResponse(config,idError,error);
    }
    
    @POST
    @Path("printConfig")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public WebResponse printConfig(@FormParam("config") String configJSON/*, @FormParam("otro")String otro*/){
    	ObjectMapper mapper = new ObjectMapper();
    	try{
    		WebConfig webConfig = mapper.readValue(configJSON, WebConfig.class);
    		return new WebResponse(webConfig.toConfigValues().toString(), "", "");
    	}catch (Exception e){
    		logger.log(Level.WARNING, "Unrecognized config param: "+configJSON, e);
    		return new WebResponse(null, String.valueOf(400), "Bad resquest: Unrecognized config param");
    	}  	
    }
    
    private void removeSession(String sessionID){
    	if(sessions.containsKey(sessionID)){
    		sessions.get(sessionID).destroy();
    		sessions.remove(sessionID);
    	}
    }
    
    private boolean updateSession(final String sessionID){
    	if(sessions.containsKey(sessionID)){
    		sessions.get(sessionID).cancelTimeout();
    		sessions.get(sessionID).schedule(new TimerTask() {
				@Override
				public void run() {
					removeSession(sessionID);					
				}
			},sessionTimeoutSeconds);
    		return true;
    	}
    	return false;
    }
    
	private void createNewSession(String sessionID, File uploadedFile){
		if(sessionID!= null && sessionID.isEmpty() && uploadedFile !=null){
    	sessions.put(sessionID, new AR2DToolManager(sessionID, uploadedFile));
		}else{
			logger.severe("Invalid create new session with sessionID: "+sessionID+" or null uploaded file.");
		}
    }
}
