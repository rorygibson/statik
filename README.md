# CES - Content Editing System

A very simple content editing system.

The premise is that if you've got a proper designer building your website, they want to work in plain ol' HTML, CSS and maybe JS.
And they're going to give you a zip file of stuff.
If you want to be able to change the words (not the images, or the layout, or the colours - *designers hate that*) then you can't, unless you know a bit about HTML.

The idea of CES is, as a designer who can work an FTP client, it should be possible to throw the static resources into a directory on a server, and it just becomes editable when you're signed in.

You give the client some login details and that's it, they've got a minimal, editable website.

Just like that.

(It's not there yet, but we're well on the way).

## Prerequisites

To build:
 - Java 7
 - Maven 3

To run:
 - Java 7
 - MongoDB

Requires (to do anything useful):
 - a website, expressed as a set of static .html files, CSS, JavaScript, images etc.

## Development setup
    git clone https://github.com/rorygibson/ces.git

    mkdir /home/rory/websites/my-site (or wherever)

    Copy the HTML of your static site into the directory you just created

    Configure $HOME/config.properties with your MongoDB details, path to website and so on (template below)

    mvn clean package tomcat:run-war

    Open browser on http://localhost:8080/index.html


## Server setup

Server side tasks (ex. Tomcat):

 * Obtain the WAR file
 * Make sure it's called ROOT.war (rename if necessary)
 * Copy the ROOT.war file to the servlet container deployment directory ($CATALINA_HOME/webapps)
 * Create a config.properties file in the $HOME directory of the user running the servlet container
 * Create a users.properties file in the $HOME directory of the user running the servlet container
 * Copy your website static files to the directory specified in your config.properties
 * Start Tomcat (./bin/startup.sh)

Example config.properties

    dbName=contentdb
    mongoHost=localhost
    mongoPort=27017
    mongoUsername=hellojava
    mongoPassword=password
    fileBase=/tmp/testFiles
    welcomeFile=index.html

Example users.properties

    fred=password1
    barney=secret-dinosaur-key

