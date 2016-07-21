package es.upm.oeg.webAR2DTool.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import es.upm.oeg.webAR2DTool.managers.AR2DToolManager;
import es.upm.oeg.webAR2DTool.responses.WebConfig;
import es.upm.oeg.webAR2DTool.utils.Constants;
import es.upm.oeg.webAR2DTool.utils.MailSender;
import es.upm.oeg.webAR2DTool.utils.ParameterNames;
import es.upm.oeg.webAR2DTool.utils.PosibleLangsJena;
import es.upm.oeg.webAR2DTool.utils.WebResponse;

@Path("methods")
@Singleton
public class AR2DToolMethods {

	private static final Logger logger = Logger.getLogger(Constants.WEBAPP_NAME);
	private final Map<String, AR2DToolManager> sessions = new HashMap<String, AR2DToolManager>();
	
	private static final String HTTP_ACCEPT_TYPES = "application/rdf+xml,application/owl+xml,application/n-triples,application/ld+json,text/trig,application/n-quads,application/trix+xml,application/rdf+thrift";
	
	private String uploadedFilesFolder = "";
	private int sessionTimeoutSeconds = 3600;
	private boolean removeDirSession = true;
	private int timeoutUploadSeconds = 5;//Seconds
	private double limitFileSizeUploadMB = 5; //Mega-bytes
	private int numberOfTriplesOnFile = 5000;
	private MailSender mailSender = null;
	
	public AR2DToolMethods(@Context ServletContext sContext) {
		try {
			WebConfig config = new WebConfig(sContext);
			config.toConfigValues();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Can not load config values", e);
		}
		try {
			Properties prop = new Properties();
			prop.load(sContext.getResourceAsStream(Constants.SERVER_PROPERTIES));
			File tempUploadedFilesFolder = new File(prop.getProperty(ParameterNames.PATH_TO_UPLOADED_FILES));
			if (!tempUploadedFilesFolder.exists()) {
				if (!tempUploadedFilesFolder.mkdirs()) {
					throw new IOException(
							"Can not create folder for uploaded files: " + tempUploadedFilesFolder.getAbsolutePath());
				}
			}
			if (!tempUploadedFilesFolder.isDirectory()) {
				throw new IOException(tempUploadedFilesFolder.getAbsolutePath() + " is not a Folder.");
			}
			if (!tempUploadedFilesFolder.canRead() || !tempUploadedFilesFolder.canWrite()
					|| !tempUploadedFilesFolder.canExecute()) {
				throw new IOException(
						tempUploadedFilesFolder.getAbsolutePath() + " can not be readed, writed or executed");
			}
			uploadedFilesFolder = prop.getProperty(ParameterNames.PATH_TO_UPLOADED_FILES);
			sessionTimeoutSeconds = Integer.parseInt(prop.getProperty(ParameterNames.SESSION_TIMEOUT_IN_SECONDS,"3600").trim().replaceAll(" ", ""));
			removeDirSession = Boolean.valueOf(prop.getProperty(ParameterNames.REMOVE_DIR_SESSION,"true").trim().replaceAll(" ", ""));
			timeoutUploadSeconds = Integer.parseInt(prop.getProperty(ParameterNames.UPLOAD_TIMEOUT_SECONDS,"5").trim().replaceAll(" ", ""));
			limitFileSizeUploadMB = Double.parseDouble(prop.getProperty(ParameterNames.UPLOAD_FILE_SIZE_MB,"5").trim().replaceAll(" ", ""));
			numberOfTriplesOnFile = Integer.parseInt(prop.getProperty(ParameterNames.GENERATE_TRIPLETS_LIMIT,"2000").trim().replaceAll(" ", ""));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Can not load " + Constants.SERVER_PROPERTIES + " file", e);
		}
		try {
			//GET MAIL PROPERTIES
			Properties propMail = new Properties();
			propMail.load(sContext.getResourceAsStream(Constants.MAIL_PROPERTIES));
			mailSender = new MailSender(propMail);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Can not load " + Constants.MAIL_PROPERTIES + " file", e);
		}
	}

	@GET
	@Path("hasUploadedFile")
	@Produces(MediaType.APPLICATION_JSON)
	public WebResponse hasUploadedFile(@Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		boolean hasSession = updateSession(jSessionID); 
		String response = "{\"hasUploadedFile\":";
		response += hasSession;
		if(hasSession){
			response += ","+"\"hasGeneratedImage\":";
			boolean hasImage = sessions.get(jSessionID).getImage() !=null 
					&& sessions.get(jSessionID).getImage().exists()
					&& sessions.get(jSessionID).getImage().length()>0;
			response += hasImage;
		}
		response += "}";
		return new WebResponse(response, "", "");
	}

	@POST
	@Path("uploadFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public WebResponse uploadFile(@Context HttpServletRequest request,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
			@FormDataParam("uri") String uri,
			@FormDataParam("fileSize") long fileSize){
		String toUploadString = uploadedFilesFolder;
		if (!toUploadString.endsWith(File.separator)) {
			toUploadString += File.separator;
		}
		String jSessionID = request.getSession(true).getId();
		if (jSessionID == null || jSessionID.isEmpty()) {
			return new WebResponse(null, "server.errorNoID", "Error session ID not found or can not create it.");
		}
		if (sessions.containsKey(jSessionID) && sessions.get(jSessionID).getWorkspaceFolder().exists()) {
			if (sessions.get(jSessionID).getWorkspaceFolder().isDirectory()) {
				try {
					FileUtils.cleanDirectory(sessions.get(jSessionID).getWorkspaceFolder());
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Can not clean workspace folder:"
							+ sessions.get(jSessionID).getWorkspaceFolder().getAbsolutePath(), e);
				}
			} else {
				sessions.get(jSessionID).getWorkspaceFolder().delete();
			}
		}
		toUploadString += jSessionID;
		File toUploadFolder = new File(toUploadString);
		if (!toUploadFolder.exists()) {
			if (!toUploadFolder.mkdirs()) {
				return new WebResponse(null, "server.canNotCreateFolder",
						"Can not create folder with your session ID.");
			}
		}
		if (!toUploadFolder.isDirectory() || !toUploadFolder.canRead() || !toUploadFolder.canWrite()
				|| !toUploadFolder.canExecute()) {
			return new WebResponse(null, "server.canNotUseFolder",
					"Server error can not use the folder with your session ID.");
		}
		if(fileInputStream == null || contentDispositionHeader == null 
				|| contentDispositionHeader.getFileName()==null || contentDispositionHeader.getFileName().isEmpty()){
			if(uri != null && !uri.isEmpty()){
				try{
					/*java.net.URL url = new java.net.URL(uri);
					URLConnection urlCon = url.openConnection();*/
					String realURL = uri;
					try{
						realURL = obtainRealURI(uri);
					}catch(Exception e){
						return new WebResponse(null, "server.canNotReadDataFromURI", "Can not read data from uri:"+uri);
					}
					String [] splitUri = realURL.split("/");
					String uriFile = splitUri[splitUri.length-1];
					toUploadString += File.separator + uriFile ;
					File toUpload = new File(toUploadString);
					toUpload.createNewFile();
					OutputStream out = new FileOutputStream(toUpload);
					HttpURLConnection urlCon = (HttpURLConnection) new URL(realURL).openConnection();
					urlCon.setRequestProperty("Accept", HTTP_ACCEPT_TYPES);
					urlCon.connect();
					WebResponse response = uploadFile(urlCon.getInputStream(), out,toUpload.getAbsolutePath());
					if(response!=null){
						return response;
					}
					response = null; //Can be removed. Before this line response == null all time.
					//I write this line for view that response is null.
					response = checkNumberOfTriples(toUpload.getAbsolutePath());
					if(response!=null){
						return response;
					}
					createNewSession(jSessionID, toUpload, toUploadFolder);
					return new WebResponse("{isFileUploaded: true}", null, null);
				}catch(Exception e){
					logger.log(Level.INFO,"Invalid URI:"+uri,e);
					return new WebResponse(null, "server.invalidURI",
							"Upload fails. Invalid URI.");
				}
			}else{
				return new WebResponse(null, "server.invalidFileAndURI",
						"Upload fails. Invalid file and invalid URI.");
			}
		}
		toUploadString += File.separator + contentDispositionHeader.getFileName();
		File toUpload = new File(toUploadString);
		if(fileSize<=0 || fileSize > (limitFileSizeUploadMB*1024*1024)){
			double fileSizeMB = ((double)fileSize) / 1024.0 / 1024.0; 
			return new WebResponse(null, "fileSizeUpload.exceeded", "Error file size is 0 or exceed the upload limit: "+limitFileSizeUploadMB+" MB, uploaded file has: "+fileSizeMB+" MB");
		}
		try {
			toUpload.createNewFile();
			OutputStream out = new FileOutputStream(toUpload);
			WebResponse response = uploadFile(fileInputStream, out,toUpload.getAbsolutePath()); 
			if(response!=null){
				return response;
			}
			createNewSession(jSessionID, toUpload, toUploadFolder);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Error when try to upload a file. Session ID=" + jSessionID + " . FilePath=" + toUploadString, e);
			return new WebResponse(null, "server.errorUploadFile",
					"An unexpected error when upload the file please contact with system admin.");
		}
		return new WebResponse("{isFileUploaded: true}", null, null);
	}
	
	@GET
	@Path("getDefaultConfigValues")
	@Produces(MediaType.APPLICATION_JSON)
	public WebResponse getDefaultConfigValues(@Context ServletContext sContext, @Context HttpServletRequest request) {
		updateSession(request.getSession(true).getId());
		WebConfig config = null;
		String idError = "";
		String error = "";
		try {
			config = new WebConfig(sContext);

		} catch (Exception e) {
			config = null;
			idError = "UnexpectedException";
			error = e.getMessage();
			logger.log(Level.SEVERE, "Error when load config values.", e);
		}
		return new WebResponse(config, idError, error);
	}

	@GET
	@Path("getAllUris")
	@Produces(MediaType.APPLICATION_JSON)
	public WebResponse getAllUris(@Context ServletContext sContext, @Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		if(!updateSession(jSessionID)){
			return new WebResponse(null, "notUploadedFile", "You do not have uploaded file. Can not obtain uris.");
		}
		Set<String> uris = getAllURIs(sessions.get(jSessionID).getFile().getAbsolutePath());
		if(uris==null){
			return new WebResponse(uris, "error.ObtainUris", "Error when obtain model or uris for your file.");
		}
		return new WebResponse(uris, null, null);
	}
	
	
	@POST
	@Path("generateImage")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public WebResponse generateImage(@Context HttpServletRequest request, @FormParam("config") String configJSON) {
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return new WebResponse(null, "server.noUploadedFile", "Not have uploaded file with this session ID.");
		}
		ObjectMapper mapper = new ObjectMapper();
		WebConfig webConfig = null;
		try {
			 webConfig = mapper.readValue(configJSON, WebConfig.class);
		}catch(Exception e){
			logger.log(Level.SEVERE, "Can not convert JSON to WebConfig", e);
			return new WebResponse(null,"server.canNotGetConfig","Can not parse JSON to Config");
		}
		sessions.get(jSessionID).createNewThread(webConfig);
		sessions.get(jSessionID).getThread().start();
		long startTime = System.currentTimeMillis();
		boolean wait = true;
		try {
			while(wait){
				if(!sessions.get(jSessionID).getThread().isAlive()){
					wait = false;
				}else{
					Thread.sleep(100);
					if((System.currentTimeMillis()-startTime)>= timeoutUploadSeconds*1000){
						wait = false;
					}
				}
			}
			if(sessions.get(jSessionID).getThread().isAlive()){
				sessions.get(jSessionID).getThread().interrupt();
				return new WebResponse(null,"server.generateTimeout","Exceeded the timeout limit for generate image");
			}
			WebResponse response = getErrorWebResponse(sessions.get(jSessionID));
			if(response!=null){
				return response;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Can not generate image",e);
			return new WebResponse(null, "server.UnexpectedException", "Unexpected server exception. Please contact with system admin.");
		}
		return new WebResponse("{generated:true}", null, null);
	}

	@GET
	@Path("getImage")
	//@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("image/svg+xml")
	public Response getImage(@Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return Response.status(404).build();
		}
		if (sessions.get(jSessionID).getImage() == null || !sessions.get(jSessionID).getImage().exists()) {
			return Response.status(404).build();
		}
		ResponseBuilder responseBuilder = Response.ok((Object) sessions.get(jSessionID).getImage());
		responseBuilder.header("Content-Disposition", "attachment; filename=\""+sessions.get(jSessionID).getImage().getName()+"\"");
		return responseBuilder.build();
	}
	
	@GET
	@Path("getGraphml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getGraphml(@Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return Response.status(404).build();
		}
		if (sessions.get(jSessionID).getGrapml() == null || !sessions.get(jSessionID).getGrapml().exists()) {
			return Response.status(404).build();
		}
		ResponseBuilder responseBuilder = Response.ok((Object) sessions.get(jSessionID).getGrapml());
		responseBuilder.header("Content-Disposition", "attachment; filename=\""+sessions.get(jSessionID).getGrapml().getName()+"\"");
		return responseBuilder.build();
	}
	
	@GET
	@Path("getDot")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDot(@Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return Response.status(404).build();
		}
		if (sessions.get(jSessionID).getDot() == null || !sessions.get(jSessionID).getDot().exists()) {
			return Response.status(404).build();
		}
		ResponseBuilder responseBuilder = Response.ok((Object) sessions.get(jSessionID).getDot());
		responseBuilder.header("Content-Disposition", "attachment; filename=\""+sessions.get(jSessionID).getDot().getName()+"\"");
		return responseBuilder.build();
	}
	
	@GET
	@Path("getAR2DToolLog")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAR2DToolLog(@Context HttpServletRequest request) {
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return Response.status(404).build();
		}
		if (sessions.get(jSessionID).getLog() == null || !sessions.get(jSessionID).getLog().exists()) {
			return Response.status(404).build();
		}
		ResponseBuilder responseBuilder = Response.ok((Object) sessions.get(jSessionID).getLog());
		responseBuilder.header("Content-Disposition", "attachment; filename=\""+sessions.get(jSessionID).getLog().getName()+"\"");
		return responseBuilder.build();
	}

	@POST
	@Path("sendReport")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response sendReport(@Context HttpServletRequest request,@FormParam("email") String email,@FormParam("message") String message){
		String jSessionID = request.getSession(true).getId();
		if (!updateSession(jSessionID)) {
			return Response.status(404).build();
		}
		AR2DToolManager files = sessions.get(jSessionID);
		if(email == null || email.isEmpty()){
			return Response.status(400).build();
		}
		if(message == null || message.isEmpty()){
			return Response.status(400).build();
		}
		String html = "";
		try{
			//SEND EMAIL
			if(mailSender==null){
				html = "<p style='color:red'>The report log configuration is wrong. Contact with syte administrator.</p>";
			}else{
				if(mailSender.sendEmail(jSessionID,message, email,files.getFile(), files.getLog(),files.getImage(), files.getDot(), files.getGrapml())){
					html = "<p>Report sent successful.</p>";
				}else{
					html = "<p style='color:red'>A error happened when send report. Please contact with syte administrator.</p>";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			html = "<p style='color:red'>A error happened. Please contact with syte administrator.</p>";
		}	
		return Response.ok(html).build();
	}
	
	// TO DEBUG REMOVE IT
	@POST
	@Path("printConfig")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public WebResponse printConfig(@javax.ws.rs.FormParam("config") String configJSON) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebConfig webConfig = mapper.readValue(configJSON, WebConfig.class);
			return new WebResponse(webConfig.toConfigValues().toString(), "", "");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unrecognized config param: " + configJSON, e);
			return new WebResponse(null, String.valueOf(400), "Bad resquest: Unrecognized config param");
		}
	}

	@GET
	@Path("removeSession")
	public WebResponse removeSession(@Context HttpServletRequest request) {
		String sessionID = request.getSession(true).getId();
		if (sessions.containsKey(sessionID)) {
			String path = sessions.get(sessionID).getWorkspaceFolder().getAbsolutePath();
			removeSession(sessionID);
			return new WebResponse(path, "", "");
		}
		return new WebResponse("", "server.invalidID", "Invalid ID. You need to upload a file to get a ID");
	}

	// END REMOVE
	
	private void removeSession(String sessionID) {
		if (sessions.containsKey(sessionID)) {
			if (removeDirSession) {
				if (!sessions.get(sessionID).removeWorkspaceFolder()) {
					logger.log(Level.SEVERE, "Can not remove the folder: "
							+ sessions.get(sessionID).getWorkspaceFolder().getAbsolutePath());
					;
				}
			}
			sessions.get(sessionID).destroy();
			sessions.remove(sessionID);

		}
	}

	private boolean updateSession(final String sessionID) {
		if (sessions.containsKey(sessionID)) {
			sessions.get(sessionID).cancelTimeout();
			sessions.get(sessionID).schedule(new TimerTask() {
				@Override
				public void run() {
					removeSession(sessionID);
				}
			}, sessionTimeoutSeconds);
			return true;
		}
		return false;
	}

	private void createNewSession(String sessionID, File uploadedFile, File workspaceFolder) {
		if (sessionID != null && !sessionID.isEmpty() && uploadedFile != null && uploadedFile.exists()
				&& uploadedFile.isFile() && uploadedFile.canRead() && uploadedFile.canWrite()) {
			sessions.put(sessionID, new AR2DToolManager(sessionID, uploadedFile, workspaceFolder));
			updateSession(sessionID);
		} else {
			logger.severe("Invalid create new session with sessionID: " + sessionID + " or invalid uploaded file.");
		}
	}
	
	private WebResponse getErrorWebResponse(AR2DToolManager ar2dToolManager) {
		if(!ar2dToolManager.getGrapml().exists() || ar2dToolManager.getGrapml().length()<=0){
			if(!ar2dToolManager.getDot().exists() || ar2dToolManager.getDot().length()<=0){
				return new WebResponse(null, "server.errorGraphMl_errorDot_errorImage", "Server can not generate GraphML, Dot and Image");
			}else{
				if(!ar2dToolManager.getImage().exists() || ar2dToolManager.getImage().length()<=0){
					return new WebResponse(null, "server.errorGraphMl_errorImage", "Server can not generate GraphML and Image");
				}else{
					return new WebResponse(null, "server.errorGraphMl", "Server can not generate GraphML");
				}
			}
		}else{
			if(!ar2dToolManager.getDot().exists() || ar2dToolManager.getDot().length()<=0){
				return new WebResponse(null, "server.errorDot_errorImage", "Server can not generate Dot and Image");
			}else{
				if(!ar2dToolManager.getImage().exists() || ar2dToolManager.getImage().length()<=0){
					return new WebResponse(null, "server.errorImage", "Server can not generate Image");
				}
			}
		}
		return null;
	}
	
	private WebResponse checkNumberOfTriples(String fullPathOrUri) {
		for(org.apache.jena.riot.Lang lang:PosibleLangsJena.posibleLangs){
			try{
				//Model model = RDFDataMgr.loadModel(fullPathOrUri,lang);
				//OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,  model);
				OntModel ontModel = ModelFactory.createOntologyModel();
				ontModel.read(fullPathOrUri,null,lang.getName());
				if(ontModel.size()>numberOfTriplesOnFile){
					return new WebResponse(null,"server.exceededNumberOfTuples","File or URI exceeded the maximun number of triples: "+numberOfTriplesOnFile);
				}else{
					return null;
				}
			}catch(Exception e){
				logger.log(Level.INFO, "Can not obtain ont model from file or uri:"+fullPathOrUri+" and "+lang);
			}
		}
		logger.log(Level.SEVERE, "Can not obtain ont model from your uri or file.");
		return new WebResponse(null,"server.canNotObtainModel","Can not obtain Ont Model from your uri or file.");
	}

	private String obtainRealURI(String URL) throws MalformedURLException, IOException{
	    HttpURLConnection con = (HttpURLConnection) new URL(URL).openConnection();
	    con.setRequestProperty("Accept", HTTP_ACCEPT_TYPES);
	    con.setInstanceFollowRedirects(false);
	    con.connect();
	    con.getInputStream();
	    if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
	    		|| con.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER) {
	        String redirectUrl = con.getHeaderField("Location");
	        return obtainRealURI(redirectUrl);
	    }
	    return URL;
		/*URLConnection con = new URL(URL).openConnection();
		con.connect();
		InputStream is = con.getInputStream();
		java.net.URL toReturn = con.getURL();
		is.close();
		return toReturn.toString();*/
	}
	
	private WebResponse uploadFile(InputStream in,OutputStream out,String absolutePath) throws IOException{
		int read = 0;
		byte[] bytes = new byte[1024];
		long totalBytesRead = 0;
		boolean exceedLimit = false;
		while ((read = in.read(bytes)) != -1 && !exceedLimit) {
			out.write(bytes, 0, read);
			totalBytesRead += read;
			if(totalBytesRead>limitFileSizeUploadMB*1024*1024){
				exceedLimit = true;
			}
		}
		out.flush();
		out.close();
		if(exceedLimit){
			return new WebResponse(null, "fileSizeUpload.exceeded", "Error file size is 0 or exceed the upload limit: "+limitFileSizeUploadMB+" MB");
		}
		WebResponse response = checkNumberOfTriples("file://"+absolutePath);
		if(response!=null){
			return response;
		}
		return null;
	}
	
	private Set<String> getAllURIs(String fullPathOrUri){
		OntModel ontModel=null;
		for(org.apache.jena.riot.Lang lang:PosibleLangsJena.posibleLangs){
			try{
				//Model model = RDFDataMgr.loadModel(fullPathOrUri,lang);
				//OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,  model);
				ontModel = ModelFactory.createOntologyModel();
				ontModel.read(fullPathOrUri,null,lang.getName());
				break;
			}catch(Exception e){
				logger.log(Level.INFO, "Can not obtain ont model from file or uri:"+fullPathOrUri+" and "+lang);
				ontModel = null;
			}
		}
		if(ontModel==null){
			logger.log(Level.SEVERE, "Can not obtain ont model from uri or file:"+fullPathOrUri);
			return null;
		}
		try{
			Set<String> uris = new HashSet<String>();
			StmtIterator statements = ontModel.listStatements();
			while(statements.hasNext()){
				Statement st = statements.next();
				Resource sub = st.getSubject();
				Property pre = st.getPredicate();
				RDFNode obj = st.getObject();
				if(sub.isURIResource()){
					uris.add(sub.getURI());
				}
				if(pre.isURIResource()){
					uris.add(pre.getURI());
				}
				if(obj.isURIResource()){
					uris.add(obj.asResource().getURI());
				}
			}
			return uris;
		}catch(Exception e){
			logger.log(Level.SEVERE, "Error when obtain URIs for: "+fullPathOrUri,e);
			return null;
		}
		
	}
	
}
