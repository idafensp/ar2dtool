package es.upm.oeg.webAR2DTool.utils;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebString {
	private String string;
	
	public WebString(String string){
		this.string = string;
	}
	
	public String getString(){
		return convertToUTF8(string);
	}

	private String convertToUTF8(String in){
		try {
            in = in.replace("\n", "\\n");
            byte[] bytes = in.getBytes();
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Constants.WEBAPP_NAME).log(Level.SEVERE, null, ex);
        }
		return in;
	}
}
