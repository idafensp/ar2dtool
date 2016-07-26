
[![Code HTML5](https://img.shields.io/badge/code-HTML5-blue.svg)](https://www.w3.org/TR/html5/)
[![Code CSS3](https://img.shields.io/badge/code-CSS3-blue.svg)](http://www.w3schools.com/css/css3_intro.asp)
[![Code JS](https://img.shields.io/badge/code-JS-blue.svg)](http://www.w3schools.com/js/)
[![Desgin Responsive](https://img.shields.io/badge/design-Responsive-blue.svg)](http://www.w3schools.com/html/html_responsive.asp)
[![Desgin Google Design](https://img.shields.io/badge/design-Google%20Design-blue.svg)](https://design.google.com/)  
[![lib Google Maps](https://img.shields.io/badge/lib-Google%20Maps-green.svg)](https://developers.google.com/maps/documentation/javascript/)
[![lib jQuery](https://img.shields.io/badge/lib-jQuery-green.svg)](https://jquery.com/)
[![lib jQueryMobile](https://img.shields.io/badge/lib-jQuery%20Mobile-green.svg)](https://jquerymobile.com/)
[![lib Modernizr](https://img.shields.io/badge/lib-Modernizr-green.svg)](https://modernizr.com/)
[![lib Image Select](https://img.shields.io/badge/lib-Image%20Select-green.svg)](https://github.com/websemantics/Image-Select)
[![lib Chosen](https://img.shields.io/badge/lib-Chosen-green.svg)](https://harvesthq.github.io/chosen/)  
[![Java 1.7 or later](https://img.shields.io/badge/Java-1.7%20or%20later-40c4ff.svg)](https://www.java.com/es/download/help/index_installing.xml?j=7)
[![Jersey 2.22.1](https://img.shields.io/badge/Jersey-2.22.1-40c4ff.svg)](https://jersey.java.net/documentation/latest/user-guide.html)  
[![Build Maven](https://img.shields.io/badge/build-Maven-lightgrey.svg)](https://maven.apache.org/)
[![Version 1.0](https://img.shields.io/badge/Version-1.0%20Beta-lightgrey.svg)](#version)  

# WebAR2DTool

This repository contains a web application for use AR2DTool.

# Table of Contents
1. [Pre-requisites](#pre-requisites)
2. [Configuration](#configuration)  
2.1. [Server configuration](#serverConfiguration)  
2.2. [Change mail configuration](#changeMailConfiguration)  
2.3. [Default AR2DTool Config](#editAR2DToolConfig)  
3. [Compile](#compile)
4. [Deploy](#deploy)
5. [Development guide](#developmentGuide)
6. [Version](#version)  
  

## 1) Pre-requisites <a name="pre-requisites"></a>
* Maven 3.0 or later.
* Java 1.7 or later.
* Tomcat 1.7 or later.
* Internet connection if you use maven (for downloading dependencies).

## 2) Configuration <a name="configuration"></a>
***2.1) Server configuration*** <a name="serverConfiguration"></a>   
The first step is change the server configuration.
Enter the project folder and edit  the following file: "src/main/webapp/WEB-INF/server.properties".  

This file have the following properties, which to must edit to meet your server:
* pathToDot: The path where DOT is installed. By default, it is /usr/bin/dot (you can check it by excuting 'wich dot' in UNIX shells).
* pathToTempDir: The path to the webapp temp dir. Example: /home/USER/webAR2DTool/temp  
* server.pathToUploadedFiles: The path to the webapp upload dir (webAR2DTool will save the file to this folder, which will need write permissions). Example: /home/USER/webAR2DTool/uploaded  
* server.removeDirSession: "true" or "false". If it is "true" all files of user will be removed when session expired. If "false" the file will not be removed.  
* server.session.timeout.seconds: Seconds for session to expire since user last action.     
* server.generate.timeout.seconds: Maximum execution time (in seconds) for thread that calls AR2DTool (Generate image).
* server.upload.file.size.mb:  Maximum size of the uploaded input file.  
* server.generate.limit.triples: Maximum triples of the uploaded input file.  

Be aware that file paths must use escaped backslashes (e.g. "\\\\").

Example:
> pathToDot=/usr/bin/dot  
> pathToTempDir=/home/USER/webAR2DTool/temp    
> server.pathToUploadedFiles=/home/USER/webAR2DTool/uploaded  
> server.removeDirSession=true  
> server.session.timeout.seconds=3600  
> server.generate.timeout.seconds=16  
> server.upload.file.size.mb=5  
> server.generate.limit.triples=5000  

***2.2) Mail configuration*** <a name="changeMailConfiguration"></a>  
To change the mail properties edit the following file: "src/main/webapp/WEB-INF/mail.properties"  

* mail.server: The URL o IP address of the mail smtp server.
* mail.server.user: The login user for the mail server.
* mail.server.password: The login password for the mail server.
* mail.server.port: The port for the mail smtp server.
* mail.server.ttls.enable: : If smtp server has TTLs enabled then this parameter is "true".
* mail.from: The mail address from which email is sent from. Example: user@gmail.com  
* mail.to: The mail address of the destinatary.
* mail.cc: The mail address to be added as a copy on the email. 
* mail.bcc: The mail address to be added as hidden copy on the email.
* mail.subject: Subject of the email. Example: [WebAR2DTool] Report send. SessionID=%SESSION_ID%  
  *%SESSION_ID%: This paramerter is replaced by the app with the user ID.
* mail.message: Body of the email, containing the message to be sent. Example: "A user send report for you.\n%FILES_INFO%\nUser email: %CONTACT_EMAIL%\nUser message:\n%MESSAGE%\n"
  * %FILES_INFO%: this parameter is replaced by an array of files (and tell if exists or not).
  * %CONTACT_EMAIL%: this parameter is replaced by the user email.
  * %MESSAGE%: this parameter is replaced by the user message. 

Example:  
> mail.server=gmail.com  
> mail.server.user=myuser  
> mail.server.password=mypass    
> mail.server.port=576  
> mail.server.ttls.enable=true  
> mail.from=myuser@gmail.com  
> mail.to=idafensp@gmail.com  
> mail.cc=  
> mail.bcc=  
> mail.subject=[WebAR2DTool] Report send. SessionID=%SESSION_ID%  
> mail.message=A user send report for you.\n%FILES_INFO%\nUser email: %CONTACT_EMAIL%\nUser message:\n%MESSAGE%\n  


***2.3) Default AR2DTool config*** <a name="editAR2DToolConfig"></a>

Edit the next file:  "src/main/webapp/WEB-INF/defaultConfig.txt"  
For futher information visit:  
[Configuration file (AR2DTool)](https://github.com/idafensp/ar2dtool/blob/master/README.md#configuration-file)

## 3) Compile <a name="compile"></a>

To compile, type in a cmd or terminal (On project folder, you need see the pom.xml):
```sh
mvn clean install
```

Generated files are inside "target" folder. 

## 4) Deploy <a name="deploy"></a>

After changing the settings you can compile the tool, generating a WAR file. You will deploy this WAR file on a tomcat manager or copy the folder "webAR2DTool" (in "target" folder) into the webapps folder of tomcat.  
For futher information visit: https://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html

## 5) Development guide <a name="developmentGuide"></a>  

For development guide visit the follow link:  
[Development Guide](https://github.com/idafensp/ar2dtool/wiki/Development-guide)

## 6) Version <a name="version"></a>
1.0 Beta
