@echo off
rem 
rem Used for compiling on windows.
rem 

set CLASSPATH=D:\borknet

javac *.java
javac core/*.java
javac core/commands/*.java
javac core/modules/basic/*.java
javac core/modules/bob/*.java
javac core/modules/g/*.java
javac core/modules/logserv/*.java
javac core/modules/q/*.java
javac core/modules/r/*.java
javac core/modules/s/*.java
javac core/modules/t/*.java
javac core/modules/tutor/*.java
javac core/modules/u/*.java
javac core/modules/v/*.java

pause