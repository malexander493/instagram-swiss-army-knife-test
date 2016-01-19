@echo off
:title           :startup.bat
:description     :Batch script to run Instagram Swiss Army Knife Project.
:author		 :orbit software solutions
:usage		 :startup.bat
:==============================================================================
java -Xmx128m -jar ./target/isak-1.0-SNAPSHOT-jar-with-dependencies.jar %*