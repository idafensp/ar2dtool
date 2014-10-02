![Logo](https://dl.dropboxusercontent.com/u/4192819/logoard2tool.png) AR2DTool (beta)
===============

Standalone tool
===============

This tool receives as an input an RDF file and produces as an output a file with a graphical representation of the source RDF file

Command line syntax:

java -jar ar2dtool.jar -i PathToInputRdfFile -o FileToOutputFile -t OutputFileType -c PathToConfFile [-d]

- PathToInputRdfFile: input file. Any RDF file, including local files and URIs.

- FileToOutputFile: output picture file.

- OutputFileType: output file type: png, pdf, etc. (check supported file types at http://www.graphviz.org/doc/info/output.html)

- PathToConfFile: config file location. 

- [-d] optional flag for debugging. 

AR2DTool Java API
===============

You can download the AR2DTool Java API from the lib/ folder on this repository. If you want to use it you have to add the ar2dtool-0.1.jar file to your CLASSPATH/BUILDATH. 

As AR2DTool relies on the Jena API you also need to include it as part of your project (http://jena.apache.org/download/)


Acknowledgements
===============

- In this work we use the GraphViz Java API developed by Laszlo Szathmary (https://github.com/jabbalaci) for generating the diagramas from DOT files https://github.com/jabbalaci/graphviz-java-api