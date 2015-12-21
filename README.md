# instagram-swiss-army-knife-test
Instagram swiss army knife is a command line tool to dump Instagram data.

## <a name="toc">Table of Contents</a>
* [Introduction](#introduction)
* [How to Install](#how-to-install)
* [ISAK Commands](#isak-commands)
* [Java Docs](#java-docs)
* [License](#license)

## <a name="introduction">Introduction</a> [&#8593;](#toc)
An unofficial command line library for the [Instagram API](http://instagram.com/developer/).

It is built on [jInstagram](https://github.com/sachin-handiekar/jInstagram).

## <a name="how-to-install">How to Install</a> [&#8593;](#toc)

* Download and extract or clone the repository using

<code>git clone https://github.com/malexander493/instagram-swiss-army-knife-test.git</code>
* Create TSAK_CONF environment variable pointing to the downloaded folder.
* Edit tsak.properties file and fill up with required Instagram API key.
* Build with maven: <code>mvn clean package</code>
* <code>cd target</code>
* Run in console <code>java -jar isak-1.0-SNAPSHOT-jar-with-dependencies.jar</code>

## <a name="isak-commands">ISAK Commands</a> [&#8593;](#toc)
* Run 'isak -help' to display the help index.
* Run 'isak --help &lt;command&gt;' to display help for specific commands.

Please see the [Commands Usage](https://github.com/malexander493/instagram-swiss-army-knife-test/wiki/Commands-Usage) for more details.

## <a name="java-docs">JAVA DOCS</a> [&#8593;](#toc)
JavaDoc’s are available [here](http://malexander493.github.io/instagram-swiss-army-knife-test/)


## <a name="license">License</a> [&#8593;](#toc)

        Copyright (C) 2015 Orbit Software Solutions
 
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.
 
        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.
