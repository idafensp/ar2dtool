package es.upm.oeg.webAR2DTool.threads;

import java.io.File;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import es.upm.oeg.ar2dtool.RDF2Diagram;
import es.upm.oeg.ar2dtool.logger.AR2DToolLogger;
import es.upm.oeg.ar2dtool.utils.ConfigValues;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;
import es.upm.oeg.ar2dtool.utils.graphml.GraphMLGenerator;
import es.upm.oeg.webAR2DTool.responses.WebConfig;
import es.upm.oeg.webAR2DTool.utils.FileLoggerWriter;

public class WebAR2DToolThread extends Thread {

	private File generatedImage = null;
	private ConfigValues config;
	private String filePath = "";
	private static Logger logger = Logger.getLogger("WebAR2DTool");
	private final AR2DToolLogger ar2dtoolLog = AR2DToolLogger.getLogger("AR2DTOOL");
	
	public WebAR2DToolThread(WebConfig config, String filePath) {
		this.config = config.toConfigValues();
		this.filePath = filePath;
	}

	@Override
	public void run() {
		String logFilePath = filePath;
		logFilePath += ".log";
		FileLoggerWriter loggerWriter = new FileLoggerWriter("AR2DTOOL_FILE",Level.FINE, logFilePath);
		ar2dtoolLog.setWriter(loggerWriter);
		RDF2Diagram r2d = new RDF2Diagram();
		try {
			// load config info
			r2d.setConf(config);

			// print config values
			log(r2d.getConf().toString());

			// load model
			r2d.loadRdf(filePath);

			// apply the filters specified in config file
			r2d.applyFilters();
			log("model:\n" + r2d.printModel());
			log("Generating GV file...");

			// get the DOTGenerator with the resultant info
			DOTGenerator dg = r2d.getDOTGenerator();

			// apply transformations
			dg.applyTransformations();

			// save the DOT source to file
			dg.saveSourceToFile(filePath + ".dot");

			log("Generated! Path="+filePath+".dot");

			// get source DOT code
			String src = dg.generateDOTSource();

			// log("Compiling GV, this may take a little while...");
			// compile src code into a graph
			dg.generateDOTDiagram(src, filePath+".png", "png");
			
			generatedImage = new File(filePath+".png");
			if(!generatedImage.exists() || !generatedImage.canRead() || !generatedImage.isFile()){
				generatedImage = null;
			}
			// log("Compiled! Path="+pathToOuputFile);
			//get the GraphMLGenerator with the resultant info
			GraphMLGenerator gg = r2d.getGraphMLGenerator();
			
			//apply transformations
			gg.applyTransformations();
			
			log("GraphML source " + gg.generateGraphMLSource());
			
			//save the GraphML source to file
			gg.saveSourceToFile(filePath+".graphml");
			loggerWriter.close();
		} catch (Exception e) {
			ar2dtoolLog.getWriter().log(e,Level.SEVERE);
			loggerWriter.close();
		}
	}

	private void log(String string) {
		ar2dtoolLog.getWriter().log(string);
		//System.out.println(string);
	}

	public File getGeneratedImage() {
		return generatedImage;
	}
}
