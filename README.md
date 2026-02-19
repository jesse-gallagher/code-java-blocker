# Code/Java Blocker

This project is an OSGi plugin for Domino servers that blocks requests in the form of "foo.nsf/xsp/some/java/File.java". By default, Domino will serve source files in the Code/Java section of Designer using these paths, which is not desirable. This also blocks some known "loose" files in the NSF like ".classpath" and "plugin.xml".