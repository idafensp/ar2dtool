import es.upm.oeg.ar2dtool.RDF2Diagram;
import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;


public class Main {
	
	
	/*
	 * 
	 * Set these variables to specify your input file, the output file diagram to be generated
	 * the output type (png, pdf, etc.) and your config file.
	 * 
	 */
	private static String pathToInputFile = "";
	private static String pathToOuputFile = "";
	private static String outputFileType = "";
	private static String pathToConfFile = "";
	
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
