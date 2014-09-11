AR2DTool (beta)
===============

This tool receives as an input an RDF file and produces as an output a file with a graphical representation of the source RDF file

Command line syntax:

java -jar ar2dtool.jar -i PathToInputRdfFile -o FileToOutputFile -t OutputFileType -c PathToConfFile [-d]

- PathToInputRdfFile: input file. Any RDF file, including local files and URIs.

- FileToOutputFile: output picture file. If the "generateGvFile" flag is activated it also generates a GraphViz file in the same locations (adding ".gv" to the file (e.g. "result.png.gv")

- OutputFileType: output file type: png, pdf, etc. (check supported file types at [http://www.graphviz.org/doc/info/output.html])

- PathToConfFile: config file location. 

- [-d] optional flag for debugging. 