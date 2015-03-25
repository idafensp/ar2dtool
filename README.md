![Logo](https://dl.dropboxusercontent.com/u/4192819/logoard2tool.png) AR2DTool (alpha)
===============

Standalone tool
===============
test
This tool receives as an input an RDF file and produces as an output a file with a graphical representation of the source RDF file

Command line syntax:

java -jar ar2dtool.jar -i PathToInputRdfFile -o FileToOutputFile -t OutputFileType -c PathToConfFile -GENERATE_FLAGS [-d]

- PathToInputRdfFile: input file. Any RDF file, including local files and URIs.

- FileToOutputFile: output picture file.

- OutputFileType: output file type: png, pdf, etc. (check supported file types at http://www.graphviz.org/doc/info/output.html)

- PathToConfFile: config file location. For more information about config values check the section below.

- GENERATE_FLAGS: you can specify one or more of the flags below to define which files you want to generate:

⋅⋅* -gv/GV: using -gv a DOT source file will be generated as FileToOutputFile.gv. Using -GV will also compile the DOT source code into an image with the format specified in OutputFileType.

⋅⋅* -gml: using -gml a GraphML source file will be generated as FileToOutputFile.graphml.

- [-d] optional flag for debugging. 


AR2DTool Java API
===============

You can download the AR2DTool Java API from the lib/ folder on this repository. If you want to use it you have to add the ar2dtool-0.1.jar file to your CLASSPATH/BUILPATH. 

As AR2DTool relies on the Jena API you also need to include it as part of your project (http://jena.apache.org/download/)


Configuration file
===============

For the standalone version of the tool all the configuration parameters must be specified on a configuration file. On the API library you can either use a configuration file or specify the configuration values by means of the API methods of the ConfigValues class. 

The syntax of the simple parameters is:

parameterName=parameterValue;

Be aware that ';' are necessary at the end of each line. For comments you can use '#' at the beginning of each line.

These are the values that can be specified. 

- pathToDot: path in your filesystem to the DOT exectuable file (e.g. pathToDot=/usr/local/bin/dot;).

- pathToTempDir: path to the folder where temporary files are going to be created (e.g. pathToTempDir=/tmp;).

- imageSize: size of the DOT image generated (if any) (e.g. imageSize=1501;).

- rankdir: direction of the DOT graph image generated (if any) (e.g. rankdir=LR;).

- classShape, individualShape, and literalShape: shapes of each type of element of the graph. Allowed values are: recatangle (default), ellipse, triangle, and diamond (e.g. classShape=ellipse;).


- classColor, individualColor, and literalColor: color of each type of element of the graph. Allowed values so far are: black (default), red, blue, green, orange, yellow (e.g. literalColor=blue;).

- nodeNameMode: defines the way the resources are named. You can specify 'fulluri' for using the URIs of each resource, 'prefix' for shortened names using the specified prefixes (resources with no mathcing prefix will be displayed with their full URI), or 'localname' for using the localname of the resource. (e.g. nodeNameMode=localname;).

- ignoreLiterals: if you want to exclude literals from your graph you can set this flag to true (e.g. ignoreLiterals=true;).

- ignoreRdfType: in case you do not want the rdfs:type property to be displayed you can sent this flag to true (e.g. ignoreRdfType=false;).

- synthesizeObjectProperties: in general, AR2DTool translates RDF triples directly into graphs nodes and edges. When working with ontologies that means that object properties will be displayed indicating their range and domain, instead of connecting two classes. If you want your properties to connect classes directly you should set this flag to true (e.g. synthesizeObjectProperties=true;)

Apart from single configuration parameters you can specify lists for processing resources of your RDF dataset in different ways.

The syntax of a list is: listName=[<elementA1, elementA2,..., elementA3><elementB1, elementB2,..., elementb3>...<elementZ1, elementZ2,..., elementZ3>];

Basically a list is a set of N-tuples. In most cases you will only need 1 tuple for your whole list, but in some cases it will be necessary to include more than one (like in the case of specialElementsList or equivalentElementList parameters).

These are the list parameters that can be defined:

- includeOnlyElementList: the tool will only consider RDF triples that have a URI (as subject, object or predicate) defined on the list.

- ignoreElementsList: the tool will exclude all the RDF triples that contain a URI (as subject, object or predicate) defined on the list.

- equivalentElementList: each of the elements of a tuple will be replaced by the first element of it on the final diagram.

- specialElementList: a list composed by tuples of 3 elements (<URI,SHAPE,COLOR>) allowing to differenciate/highlight resources on the final diagram.>



Acknowledgements
===============

- In this work we use the GraphViz Java API developed by Laszlo Szathmary (https://github.com/jabbalaci) for generating the diagramas from DOT files https://github.com/jabbalaci/graphviz-java-api
