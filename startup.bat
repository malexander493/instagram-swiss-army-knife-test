@echo off
:title           :startup.sh
:description     :Batch script to run Instagram Swiss Army Knife Project.
:author		 	 :orbit software solutions
:date            :2015-11-13
:version         :1.0    
:usage		 	 :startup.bat
:==============================================================================

java -Xmx128m -jar ./target/isak-1.0-SNAPSHOT-jar-with-dependencies.jar %*
