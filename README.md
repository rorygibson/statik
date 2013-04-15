# CES - Content Editing System

## The Elevator Pitch (tl;dr)

A very simple content editing system.

 1. Get a web designer to make you a website (in HTML, CSS and maybe some JavaScript)
 2. Copy the website into CES, and change the content whenever you want, immediately.
 3. ...
 4. Profit!

## In a bit more detail

Generally, content managed websites require the designer to build the code in "Wordpress", or "FreeMarker", or "Smarty templates".
And that makes sense for great big sites with thousands of pages and complicated features.

But what if you've got a few pages of HTML and you'd just like, as the owner of a site you paid someone to design for you, to change the words on them sometimes?
Generally it's hard. You either have to know HTML, or pay someone.
And if you're a designer, and you want to make a site content managed - then again, you're back to writing-it-in-Wordpress, instead of working in your favourite tools of HTML and CSS.

CES lets you copy a static site into a directory on the server (probably using FTP, like back in the day), where it serves from, just like Apache.
Except, whenever you want to, you can fire a browser at http://website/login, punch in a username and password, and live-edit the content on the page.

That's it.

## Project status
It works. It's got a couple of bugs and the edtor functionality is very basic.
Try it out and gimme feedback, please!


## Prerequisites

To build:

 - Java 7
 - Maven 3

To run:

 - Java 7
 - MongoDB

To serve a website:

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

