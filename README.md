# Code/Java Blocker

This project is an OSGi plugin for Domino servers that blocks requests in the form of "foo.nsf/xsp/some/java/File.java". By default, Domino will serve source files in the Code/Java section of Designer using these paths, which is not desirable. This also blocks some known "loose" files in the NSF like ".classpath" and "plugin.xml".

## Installation

To install this plugin, either copy the contents of UpdateSite/plugins to your Domino server's data/domino/workspace/applications/eclipse/plugins directory or use an NSF Update Site: https://ds-infolib.hcltechsw.com/ldd/ddwiki.nsf/xpAPIViewer.xsp?lookupName=XPages+Extensibility+API#action=openDocument&res_title=XPages_Extension_Library_Deployment&content=apicontent