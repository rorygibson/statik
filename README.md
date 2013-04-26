# Statik - a content editing system

## The Elevator Pitch (tl;dr)

A very simple content editing system.

 1. Get a web designer to make you a website (in HTML, CSS and maybe some JavaScript)
 2. Copy the website into Statik, and change the content whenever you want, immediately.
 3. ...
 4. Profit!

## In a bit more detail

Generally, content managed websites require the designer to build the code in "Wordpress", or "FreeMarker", or "Smarty templates".
And that makes sense for great big sites with thousands of pages and complicated features.

But what if you've got a few pages of HTML and you'd just like, as the owner of a site you paid someone to design for you, to change the words on them sometimes?
Generally it's hard. You either have to know HTML, or pay someone.
And if you're a designer, and you want to make a site content managed - then again, you're back to writing-it-in-Wordpress, instead of working in your favourite tools of HTML and CSS.

Statik lets you copy a static site into a directory on the server (probably using FTP, like back in the day), where it serves from, just like Apache.
Except, whenever you want to, you can fire a browser at http://website/login, punch in a username and password, and live-edit the content on the page.

That's it.

## Features

 - Works with any static website
 - WYSIWYG content editing
 - Supports multiple authors
 - Serves a single website per instance of Statik (each instance should be configured with a unique fileBase and MongoDB database name)
 - Right click on elements (paragraphs, list items, anchors) to edit
 - Can run in an installed servlet container (Tomcat, Jetty etc) or standalone as an executable JAR


## Status
It works. It's got a couple of bugs.
Try it out and gimme feedback, please!


## Prerequisites

To build:

 - Java 7
 - Maven 3
 - MongoDB
 - Firefox (to run the acceptance tests)

To run:

 - Java 7
 - MongoDB
 - a website, expressed as a set of static .html files, CSS, JavaScript, images etc.

## Development setup

### Get the code
    git clone https://github.com/rorygibson/statik.git

### Build (defaults to webapp packaging)
    cd statik
    mvn -DfileBase=$PWD/demo-website clean install

### Run locally in Jetty (port 8080)
    cd statik
    mvn -DfileBase=$PWD/demo-website jetty:run-war

### Build (as standalone uber-jar for non-servlet-container deployment)
    cd statik
    mvn clean install -Pstandalone

### Run locally as standalone service (port 4567)
    cd statik
    java -jar -DfileBase=$PWD/demo-website target/statik-1.0-SNAPSHOT.jar



## Setup for using it "for real"

Example instructions (for e.g. Tomcat servlet container)

 * Obtain the WAR file
 * Make sure it's called ROOT.war (rename if necessary)
 * Copy the ROOT.war file to the deployment directory ($CATALINA_HOME/webapps)
 * Create a config.properties file in the $HOME directory of the user running Tomcat
 * Create a users.properties file in the $HOME directory of the user running Tomcat
 * Copy your website static files to the directory specified in your config.properties
 * Make sure MongoDB is running (and on the port specified in config.properties)
 * Start Tomcat (./bin/startup.sh)

Example config.properties

    dbName=statik
    mongoHost=localhost
    mongoPort=27017
    mongoAuth=true
    mongoUsername=myFirstStatik
    mongoPassword=password
    fileBase=/home/bob/statik-websites/my-first-website
    welcomeFile=index.html

Example users.properties

    fred=password1
    barney=secret-dinosaur-password
