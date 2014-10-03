import es.upm.oeg.ar2dtool.RDF2Diagram;
import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;
import es.upm.oeg.ar2dtool.utils.graphml.GraphMLGenerator;


public class Main {
	
	
	/*
	 * 
	 * Set these variables to specify your input file, the output file diagram to be generated
	 * the output type (png, pdf, etc.) and your config file.
	 * 
	 */
	private static String pathToInputFile = "/Users/isantana/Dropbox/DOCTORADO/ar2dtool/samples/pizza/individuals/pizzainds.owl";
	private static String pathToOuputFile = "/Users/isantana/Desktop/pizzares.png";
	private static String outputFileType = "png";
	private static String pathToConfFile = "/Users/isantana/Dropbox/DOCTORADO/ar2dtool/samples/pizza/individuals/conf.txt";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RDF2Diagram r2d = new RDF2Diagram();
		
		try {

			//load config info
			r2d.loadConfigValues(pathToConfFile);

			//print config values 
			System.out.println(r2d.getConf().toString());
			
			//load model
			r2d.loadRdf(pathToInputFile);
			
			//apply the filters specified in config file
			r2d.applyFilters();
			
			//get the DOTGenerator with the resultant info
			DOTGenerator dg = r2d.getDOTGenerator();
			
			//apply transformations
			dg.applyTransformations();
			
			//get source DOT code
			String src = dg.generateDOTSource();
			
			//compile src code into a graph 
			dg.generateDOTDiagram(src,pathToOuputFile,outputFileType);
			
			//get the GraphMLGenerator with the resultant info
			GraphMLGenerator gg = r2d.getGraphMLGenerator();
			
			//apply transformations
			gg.applyTransformations();
			
			//save the GraphML source to file
			gg.saveSourceToFile(pathToOuputFile+".graphml");
			
			
			
			
			
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

}
