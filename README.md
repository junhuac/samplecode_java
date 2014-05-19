PayNearMe Callback Bootstrap
----------------------------

This is a starting point for integrating your service with PayNearMe's callback
system. Provided in this project is a framework for handling incoming callbacks
from PayNearMe and constructing the appropriate response. Each callback method
includes an example implementation that is wired into the servlet

This is able to run in servlet containers which support the 3.x servlet API,
such as Tomcat, JBoss, and Jetty.

Building
========

Maven is required to build this project. Once maven is installed, simply run

    mvn clean install

The final artifact will be target/callback-server.war

Maven will handle resolving dependencies and adding them to the classpath.

Running
=======

Running the project (for development and testing only!) can be done by running

    mvn install -Prun-local

Which will build the project (if necessary) and run it. The server will be
accessible on port 8080 of the machine that the command was run on, with the
context of /.

    http://127.0.0.1:8080/authorize
    http://127.0.0.1:8080/confirm

Note that it will bind to 0.0.0.0.

Deployment
==========

The implemented server can be integrated with an existing app, or ran next to
it as a servlet on the same (or a different) application server. This example
avoids any specific features or dependencies on a particular application server
so any server which supports JSR-315 (Servlet 3.0) should support it.

Recommendations include: Jetty (used during development), Tomcat (tested on
briefly), and JBoss-AS/Wildfly (early development).

Troubleshooting and debugging
=============================

This project uses SLF4J as a logging framework, with the simple logging backend
by default. Configure your application server to route logging to an appropriate
place, and adjust logging levels via properties.
