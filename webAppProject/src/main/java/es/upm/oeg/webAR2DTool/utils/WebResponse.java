package es.upm.oeg.webAR2DTool.utils;

import java.io.Serializable;

public class WebResponse implements Serializable{
	private static final long serialVersionUID = 4789743659969588210L;
	private Object response;
	private String idErrorMessage;
	private String errorMessage;
	
	public WebResponse(Object response,String idErrorMessage,String errorMessage){
		this.response = response;
		if(response instanceof String){
			String responseString = String.valueOf(response);
			if(responseString.isEmpty()){
				this.response = null;
			}
		}
		this.errorMessage = errorMessage;
		this.idErrorMessage = idErrorMessage;
	}
	
	public boolean isErrorResponse(){
		return response==null;
	}

	public Object getResponse() {
		return response;
	}

	public String getIdErrorMessage() {
		return idErrorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	
}