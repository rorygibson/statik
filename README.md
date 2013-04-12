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

Requires (currently), on the server:
 - Java 7
 - Maven 3 (to build, a packaged binary will be forthcoming soon)
 - MongoDB

Requires (as a designer who wants to give a client an editable website):
 - a website, expressed as a set of static .html files, CSS, JavaScript, images etc.

## Setup
    git clone https://github.com/rorygibson/ces.git

    mkdir /home/rory/websites/my-site (or wherever)

    dump the HTML of your static site into the directory

    configure the src/main/resources/config.properties with your MongoDB details, path to website and the name and password of the author account you want to give to your client

    mvn clean package tomcat:run-war

    open browser on http://[host]:8080/index.html


## TODO

 - Editing of repeated elements (nth in a list, or second of 4 paragraphs) will result in the results appearing the the wrong element - it's not taking account of nth-child selectors in the JS.
 - Editability of an element should be signified by the author, by right clicking on elements of allowed types (headings, paras, sections...), probably with a right click menu
 - Currently there's a single collection for the whole site, and no way to differentiate pages. ContentItem needs to be aware of the page it's on, or similar. At the moment it's only safe to use on a single page.
 - Binary packaging and release, with externalised config
 - Productionise - no tests right now
