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
5. [Development](#development)
6. [Version](#version)  
  

## 1) Pre-requisites <a name="pre-requisites"></a>
* Maven 3.0 or later.
* Java 1.7 or later.
* Tomcat 1.7 or later.
* Internet connection if you use maven (for downloading dependencies).

## 2) Configuration <a name="configuration"></a>
***2.1) Server configuration*** <a name="serverConfiguration"></a>   
The first step is change the server configuration.
Enter on project folder and edit "src/main/webapp/WEB-INF/server.properties".  

This file have the next properties:
* pathToDot: The path where is installed DOT. Default: /usr/bin/dot
* pathToTempDir: The path to the webapp temp dir. Example: /home/USER/webAR2DTool/temp  
* server.pathToUploadedFiles: The path to the webapp upload dir (webAR2DTool will save the file to this folder, need write permissions). Example: /home/USER/webAR2DTool/uploaded  
* server.removeDirSession: "true" or "false". If it is "true" all files of user will be removed when session expired, on "false" the file will not be removed.  
* server.session.timeout.seconds: Seconds for session expire since user last action.     
* server.generate.timeout.seconds: Maximum time in seconds for thread that call AR2DTool (Generate image).
* server.upload.file.size.mb:  Maximum size of the uploaded file.  
* server.generate.limit.triples: Maximum triples of the uploaded file.  

Remember for file paths, the backslash need to be escape (Put "\\\\" instead of).

Example:
> pathToDot=/usr/bin/dot  
> pathToTempDir=/home/USER/webAR2DTool/temp    
> server.pathToUploadedFiles=/home/USER/webAR2DTool/uploaded  
> server.removeDirSession=true  
> server.session.timeout.seconds=3600  
> server.generate.timeout.seconds=16  
> server.upload.file.size.mb=5  
> server.generate.limit.triples=5000  

***2.2) Change mail configuration*** <a name="changeMailConfiguration"></a>  
For change the mail properties edit the next file:  
"src/main/webapp/WEB-INF/mail.properties"  
The file have the next parameters:  
* mail.server: The URL o IP of mail smtp server.
* mail.server.user: The login user of mail server.
* mail.server.password: The login password user of mail server.
* mail.server.port: The port of mail smtp server.
* mail.server.ttls.enable: : If smtp server have TTLs then this parameter is "true". Values "false", "true".
* mail.from: The mail URL that send the email. Example: user@gmail.com  
* mail.to: The mail URL of destinatary.
* mail.cc: The mail URL to add a copy.
* mail.bcc: The mail URL to add a hidden copy.
* mail.subject: Subject of mail. Example: [WebAR2DTool] Report send. SessionID=%SESSION_ID%  
  *%SESSION_ID%: This paramerter is replaced by the app with the user ID.
* mail.message: Message to send. Example: "A user send report for you.\n%FILES_INFO%\nUser email: %CONTACT_EMAIL%\nUser message:\n%MESSAGE%\n"
  * %FILES_INFO%: this parameter is replaced by the app with a array of files (and tell if exists or not).
  * %CONTACT_EMAIL%: this parameter is replaced by the app with the user email.
  * %MESSAGE%: this parameter is replaced by the app with the user message. 

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

Edit the next file:  
"src/main/webapp/WEB-INF/defaultConfig.txt"  
For futher information visit:  
[Configuration file (AR2DTool)](https://github.com/idafensp/ar2dtool/blob/master/README.md#configuration-file)

## 3) Compile <a name="compile"></a>

To compile, type in a cmd or terminal (On project folder, you need see the pom.xml):
```sh
mvn clean install
```

Generated files are inside "target" folder. We used this files in "deploy" section.

## 4) Deploy <a name="deploy"></a>

After changing the settings and compile you will deploy the war file on a tomcat manager or copy the folder "webAR2DTool" (in "target" folder) into webapps folder of tomcat.
For futher information visit: https://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html

## 5) Development <a name="development"></a>

See the next link for developing guide:  
// TODO

## 6) Version <a name="version"></a>
0.0.1
