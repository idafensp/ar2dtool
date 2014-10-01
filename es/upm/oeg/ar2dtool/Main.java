package es.upm.oeg.ar2dtool;

import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;

public class Main {

	private static final int ARG_LENGTH = 8;
	public static String syntaxErrorMsg = "Syntax error. Please use the following syntax \"java -jar ar2dtool.jar -i PathToInputRdfFile -o FileToOutputFile -t OutputFileType -c PathToConfFile [-d]\"";
	private static String pathToInputFile = "";
	private static String pathToOuputFile = "";
	private static String outputFileType = "";
	private static String pathToConfFile = "";

	private static boolean DEBUG = false;

	// LOGGING
	private static Level logLevel = Level.ALL;;
	private static final Logger log = Logger.getLogger(RDF2Diagram.class.getName());

	public static void main(String[] args) {

		parseArgs(args);

		if ((pathToInputFile.equals("")) || (outputFileType.equals(""))
				|| (pathToOuputFile.equals("")) || (pathToConfFile.equals(""))) {
			System.err.println(syntaxErrorMsg);
			return;
		}

		
		log("pathToInputFile:" + pathToInputFile);
		log("pathToOuputFile:" + pathToOuputFile);
		log("outputFileType:" + outputFileType);
		log("pathToConfFile:" + pathToConfFile);
		
		
		RDF2Diagram r2d = new RDF2Diagram();
		
		try {

			//load config info
			r2d.loadConfigValues(pathToConfFile);

			//print config values 
			log(r2d.getConf().toString());
			
			//load model
			r2d.loadRdf(pathToInputFile);
			
			//apply the filters specified in config file
			r2d.applyFilters();
			log("model:\n" + r2d.printModel());
			
			//get the DOTGenerator with the resultant info
			DOTGenerator dg = r2d.getDOTGenerator();
			
			//apply transformations
			dg.applyTransformations();
			
			//get source DOT code
			String src = dg.generateDOTSource();
			
			//compile src code into a graph 
			dg.generateDOTDiagram(src,pathToOuputFile,outputFileType);
			
			
			
			
			
		} catch (ConfigFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFInputNotValid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private static void parseArgs(String[] args) {
		if (args.length < ARG_LENGTH) {
			System.err.println(syntaxErrorMsg);
			return;
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				i++;
				pathToInputFile = args[i];
			} else {

				if (args[i].equals("-o")) {
					i++;
					pathToOuputFile = args[i];
				} else {

					if (args[i].equals("-t")) {
						i++;
						outputFileType = args[i];
					} else {

						if (args[i].equals("-c")) {
							i++;
							pathToConfFile = args[i];
						} else {
							if (args[i].equals("-d")) {
								DEBUG = true;
							} else {
								System.err.println(syntaxErrorMsg);
								return;
							}
						}
					}
				}
			}
		}
	}

	private static void log(String msg) {
		//log.log(logLevel, msg);
		//TODO use log instead of sys.out
		System.out.println(msg);
	}
}
