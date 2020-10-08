package es.upm.oeg.ar2dtool;

/*
 * Copyright 2012-2013 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance 
with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under 
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


import java.util.logging.Level;

import es.upm.oeg.ar2dtool.exceptions.ConfigKeyNotFound;
import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.NullTripleMember;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
import es.upm.oeg.ar2dtool.logger.AR2DToolLogger;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;
import es.upm.oeg.ar2dtool.utils.graphml.GraphMLGenerator;

public class Main {

	private static final int ARG_LENGTH = 8;
	public static String syntaxErrorMsg = "Syntax error. Please use the following syntax \"java -jar ar2dtool.jar -i PathToInputRdfFile -o FileToOutputFile -t OutputFileType -c PathToConfFile [-d] [-mnt MaxNumberOfTriples]\"";
	private static String pathToInputFile = "";
	private static String pathToOuputFile = "";
	private static String outputFileType = ""; 
	private static String pathToConfFile = "";
	private static int maxNumberOfTriples = -1;

	private static boolean DEBUG = false;

	// LOGGING
	private static Level logLevelToSee = Level.INFO;
	private static Level logLevel = Level.FINE;
	private static final AR2DToolLogger log = AR2DToolLogger.getLogger("AR2DTOOL");
	
	//GENERATION FLAGS
	private static boolean GENERATE_GV = false;
	private static boolean GENERATE_GML = false;
	private static boolean COMPILE_GV = false;
	private static String temp_dir = "";

	public static void main(String[] args) {
		parseArgs(args);
		log.getWriter().setLogLevelDefault(logLevel);
		if ((pathToInputFile.equals("")) || (outputFileType.equals(""))
				|| (pathToOuputFile.equals("")) || (pathToConfFile.equals(""))) {
			//WebServices.debugWrite();
			dbg(syntaxErrorMsg,Level.WARNING);
			return;
		}
		
		if(DEBUG)
		{
			logLevelToSee =Level.INFO;
			//logLevelToSee =Level.ALL;
			dbg("now in debug \n\n\n",Level.INFO);	
		}
		else
		{
			logLevelToSee =Level.FINE;
		}
		
		log.getWriter().setVisibleLogLevel(logLevelToSee);
		log("pathToInputFile:" + pathToInputFile);
		log("pathToOuputFile:" + pathToOuputFile);
		log("outputFileType:" + outputFileType);
		log("pathToConfFile:" + pathToConfFile);
		
				
		
		
		
		
		
		
		RDF2Diagram r2d = new RDF2Diagram();
		
		try {

			//load config info
			r2d.loadConfigValues(pathToConfFile);
			if(temp_dir!=""){
				log("Setting the temp dir to: "+temp_dir);
				try{
					r2d.getConf().setKeys("pathToTempDir",temp_dir);
				}
				catch(ConfigKeyNotFound e){
					log("Exception in setting the temp dir");
					e.printStackTrace();
				}
			}

			//print config values 
			log(r2d.getConf().toString());
			
			//load model
			r2d.loadRdf(pathToInputFile);
			
			//check if the model has more triples than allowed
			if(r2d.exceedsNumberOfTriples(maxNumberOfTriples))
			{
				log("The model exceeds the number of max. number of triples specified (" + maxNumberOfTriples + ")");
				return;
			}
				
			//apply the filters specified in config file
			r2d.applyFilters();
			log("model:\n" + r2d.printModel());
			
			if(GENERATE_GV)
			{
				log("Generating GV file...");
				
				//get the DOTGenerator with the resultant info
				DOTGenerator dg = r2d.getDOTGenerator();
				log("Got Dot Generator");	
				//apply transformations
				dg.applyTransformations();
				log("Applied Transformations");
				//save the DOT source to file
				dg.saveSourceToFile(pathToOuputFile+".dot");
				log("Saved the .dot file");

				log("Generated! Path="+pathToOuputFile+".dot");
				
				
				if(COMPILE_GV)
				{
					log("Getting the DOT Source");
					//get source DOT code
					String src = dg.generateDOTSource();
					

					log("Compiling GV, this may take a little while...");
					//compile src code into a graph 
					int gvo = dg.generateDOTDiagram(src,pathToOuputFile,outputFileType);
					
					if(gvo!=1) {
						log("Problem when compiling the DOT image");
						
					} else {
						log("Compiled! Path="+pathToOuputFile);
					}
					
				}	
			}
			
			if(GENERATE_GML)
			{
				//get the GraphMLGenerator with the resultant info
				GraphMLGenerator gg = r2d.getGraphMLGenerator();
				log("Getting GML");	
				//apply transformations
				gg.applyTransformations();
				
				log("GraphML source " + gg.generateGraphMLSource());
				
				//save the GraphML source to file
				gg.saveSourceToFile(pathToOuputFile+".graphml");
				log("the Graphml is generated");
			}
			
			
			
			
			
		} catch (ConfigFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFInputNotValid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullTripleMember e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private static void parseArgs(String[] args) {
		if (args.length < ARG_LENGTH) {
			//System.err.println(syntaxErrorMsg);
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
								if(args[i].equals("-gv"))
								{
									GENERATE_GV=true;
								}
								else
								{
									if(args[i].equals("-gml"))
									{
										GENERATE_GML=true;
									}
									else
									{
										if(args[i].equals("-GV"))
										{
											GENERATE_GV=true;
											COMPILE_GV=true;
										}
										else
										{
											if(args[i].equals("-mnt"))
											{
												i++;
												maxNumberOfTriples = Integer.valueOf(args[i]);
											}
											else{
												if(args[i].equals("-tmp")){
													i++;
													temp_dir = args[i];
												}
												else
												{
													dbg(syntaxErrorMsg,Level.WARNING);
													return;	
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static void log(String msg) {
		log.getWriter().log(msg);
		log.getWriter().log(msg,Level.INFO);
	}
	
	private static void dbg(String msg, Level logLevel){
		log.getWriter().log(msg,logLevel);
	}

}
