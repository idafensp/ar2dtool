package es.upm.oeg.webAR2DTool.utils;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

	public static class MailParameterNames {
		public static String SERVER = "mail.server";
		public static String SERVER_USER = "mail.server.user";
		public static String SERVER_PASS = "mail.server.password";
		public static String SERVER_PORT = "mail.server.port";
		public static String SERVER_TTLS = "mail.server.ttls.enable";
		public static String FROM_EMAIL = "mail.from";
		public static String TO_EMAIL = "mail.to";
		public static String CC = "mail.cc";
		public static String BCC = "mail.bcc";
		public static String SUBJECT = "mail.subject";
		public static String MESSAGE = "mail.message";

	}

	public class MailConfException extends Exception {
		public MailConfException(String message) {
			super(message);
		}

		private static final long serialVersionUID = -1533258755097009760L;
	}

	private String server = "";
	private String user = "";
	private String pass = "";
	private String port = "";
	private String from = "";
	private String to = "";
	private String subject = "";
	private String originalMessage = "";
	private String ttlsEnable = "";
	private String[] bcc = new String[0];
	private String[] cc = new String[0];

	private static String[] importantKeys = new String[] { MailParameterNames.SERVER, MailParameterNames.SERVER_USER,
			MailParameterNames.SERVER_PASS, MailParameterNames.SERVER_PORT, MailParameterNames.SERVER_TTLS,
			MailParameterNames.FROM_EMAIL, MailParameterNames.TO_EMAIL, MailParameterNames.SUBJECT,
			MailParameterNames.MESSAGE };

	public MailSender(Properties prop) throws MailConfException {
		StringBuffer errors = new StringBuffer("");
		String[] values = new String[importantKeys.length];
		for (int i = 0; i < importantKeys.length; i++) {
			values[i] = prop.getProperty(importantKeys[i]);
		}
		if (!checkParams(errors, values, importantKeys)) {
			throw new MailConfException(errors.toString());
		}
		server = prop.getProperty(MailParameterNames.SERVER);
		user = prop.getProperty(MailParameterNames.SERVER_USER);
		pass = prop.getProperty(MailParameterNames.SERVER_PASS);
		port = prop.getProperty(MailParameterNames.SERVER_PORT);
		from = prop.getProperty(MailParameterNames.FROM_EMAIL);
		to = prop.getProperty(MailParameterNames.TO_EMAIL);
		ttlsEnable = prop.getProperty(MailParameterNames.SERVER_TTLS);
		subject = prop.getProperty(MailParameterNames.SUBJECT);
		originalMessage = prop.getProperty(MailParameterNames.MESSAGE);
		String bccString = prop.getProperty(MailParameterNames.BCC);
		String ccString = prop.getProperty(MailParameterNames.CC);
		if (bccString != null && !bccString.isEmpty() && bccString.split("#").length > 0) {
			bcc = bccString.split("#");
		}
		if (ccString != null && !ccString.isEmpty() && ccString.split("#").length > 0) {
			cc = ccString.split("#");
		}
	}

	public boolean sendEmail(String sessionID, String message, String emailPerson,File ontFile, File log, File image, File dot,
			File graphml) {
		try {
			Properties propiedades = new Properties();
			propiedades.put("mail.smtp.auth", "true");
			propiedades.put("mail.smtp.starttls.enable", ttlsEnable);
			propiedades.put("mail.smtp.host", server);
			propiedades.put("mail.smtp.port", port);
			// Obtenemos la sesion
			Session sessionMail = Session.getInstance(propiedades, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});
			// Creamos un objeto mensaje tipo MimeMessage por defecto.
			MimeMessage mensaje = new MimeMessage(sessionMail);

			// Asignamos el emisor del mensaje al header del correo.
			mensaje.setFrom(new InternetAddress(from));

			// Asignamos el receptor del mensaje al header del correo.
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			for (String bcci : bcc) {
				mensaje.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcci));
			}
			for (String cci : cc) {
				mensaje.addRecipient(Message.RecipientType.CC, new InternetAddress(cci));
			}

			// Asignamos el asunto
			mensaje.setSubject(subject.replace("%SESSION_ID%", sessionID));

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(getMessage(originalMessage, message, emailPerson,ontFile, log, image, dot, graphml));

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			
			//attach files
			attachFiles(multipart, ontFile ,log, image, dot, graphml);
			
			// Send the complete message parts
			mensaje.setContent(multipart);

			// Enviamos el correo
			Transport.send(mensaje);
		} catch (Exception e) {
			Logger.getLogger(Constants.WEBAPP_NAME).log(Level.SEVERE, "Can not sent message", e);
			return false;
		}
		return true;
	}

	private static void attachFiles(Multipart multipart,File ontFile, File log, File image, File dot, File graphml)
			throws MessagingException {
		if(checkFile(ontFile)){
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(ontFile);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(ontFile.getName());
			multipart.addBodyPart(messageBodyPart);
		}
		if(checkFile(log)){
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(log);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(log.getName());
			multipart.addBodyPart(messageBodyPart);
		}
		if(checkFile(dot)){
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(dot);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(dot.getName());
			multipart.addBodyPart(messageBodyPart);
		}
		if(checkFile(graphml)){
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(graphml);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(graphml.getName());
			multipart.addBodyPart(messageBodyPart);
		}
		if(checkFile(image)){
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(image);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(image.getName());
			multipart.addBodyPart(messageBodyPart);
		}
	}

	private static String getMessage(String originalMessage, String message, String emailPerson,File ontFile, File log, File image,
			File dot, File graphml) {
		StringBuffer filesInfo = new StringBuffer("");
		filesInfo.append("\nOntology File available: ");
		filesInfo.append(checkFile(ontFile));
		filesInfo.append("\nLog File available: ");
		filesInfo.append(checkFile(log));
		filesInfo.append("\nDot File available: ");
		filesInfo.append(checkFile(dot));
		filesInfo.append("\nGraphML file available: ");
		filesInfo.append(checkFile(graphml));
		filesInfo.append("\nImage File available: ");
		filesInfo.append(checkFile(image));
		filesInfo.append("\n");
		return originalMessage.replace("%FILES_INFO%", filesInfo.toString()).replace("%MESSAGE%", message).replace("%CONTACT_EMAIL%", emailPerson);
	}

	private static boolean checkFile(File file) {
		return file != null && file.exists() && file.isFile() && file.canRead();
	}

	private static boolean checkParams(StringBuffer buffer, String[] args, String[] ids) {
		boolean okParams = true;
		StringBuffer toAppend = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null || args[i].isEmpty()) {
				okParams = false;
				toAppend.append("KEY="+ids[i]+" parameter is null or empty.\n");
			}
		}
		if(!okParams){
			buffer.append("Error bad config file.\n");
			buffer.append(toAppend);
		}
		return okParams;
	}
}
