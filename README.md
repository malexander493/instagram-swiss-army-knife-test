# instagram-swiss-army-knife-test
Instagram swiss army knife is a command line tool to dump Instagram data.

## <a name="toc">Table of Contents</a>
* [Introduction](#introduction)
* [How to Install](#how-to-install)
* [ISAK Commands](#isak-commands)
* [License](#license)

## <a name="introduction">Introduction</a> [&#8593;](#toc)
An unofficial command line library for the [Instagram API](http://instagram.com/developer/).

It is built on [jInstagram](https://github.com/sachin-handiekar/jInstagram).

## <a name="how-to-install">How to Install</a> [&#8593;](#toc)

* Download and extract or clone the repository using
git clone https://github.com/malexander493/instagram-swiss-army-knife-test.git
* Create ISAK_CONF environment variable pointing to the downloaded folder.
* Edit tsak.properties file and fill up with required Instagram API key.
* Build with maven: <code>mvn clean package</code>
* <code>cd target</code>
* Run in console <code>java -jar isak-1.0-SNAPSHOT-jar-with-dependencies.jar</code>

## <a name="isak-commands">ISAK Commands</a> [&#8593;](#toc)
* Run 'isak -help' to display the help index.
* Run 'isak --help <command>' to display help for specific commands.

Please see the [Commands Usage](https://github.com/malexander493/instagram-swiss-army-knife-test/wiki/commands-usage) for more details.

## <a name="license">License</a> [&#8593;](#toc)

The code is licensed under the [Apache License Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0)
