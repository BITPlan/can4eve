### can4eve
[OBDII CAN diagnosis support for electric vehicles](http://can4eve.bitplan.com) 

[![Travis (.org)](https://img.shields.io/travis/BITPlan/can4eve.svg)](https://travis-ci.org/BITPlan/can4eve)
[![Maven Central](https://img.shields.io/maven-central/v/com.bitplan.can4eve/com.bitplan.can4eve.svg)](https://search.maven.org/artifact/com.bitplan.can4eve/com.bitplan.can4eve/0.0.5/jar)
[![GitHub issues](https://img.shields.io/github/issues/BITPlan/can4eve.svg)](https://github.com/BITPlan/can4eve/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/BITPlan/can4eve.svg)](https://github.com/BITPlan/can4eve/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/BITPlan/can4eve.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

### Documentation
* [Wiki](http://can4eve.bitplan.com)
* [can4eve Project pages](https://BITPlan.github.io/can4eve)
* [Javadoc](https://BITPlan.github.io/can4eve/can4eve/can4eve/apidocs/index.html)
* [Test-Report ](https://BITPlan.github.io/can4eve/can4eve/can4eve/surefire-report.html)
### Maven dependency

Maven dependency
```xml
<dependency>
  <groupId>com.bitplan.can4eve</groupId>
  <artifactId>com.bitplan.can4eve</artifactId>
  <version>0.0.5</version>
</dependency>
```

[Current release at repo1.maven.org](http://repo1.maven.org/maven2/com/bitplan/can4eve/com.bitplan.can4eve/0.0.5/)

### How to build
```
git clone https://github.com/BITPlan/can4eve
cd can4eve
mvn install
```
## What is can4eve? 
can4eve 
* is a software for electric vehicles
* it reads, analyzes, visualizes, stores and replays CAN bus data from the vehicle taken via an OBDII adapter
* is an open source software project see https://github.com/BITPlan/can4eve
## Supported Vehicles 
as of 2017-06 only Triplet cars: Mitsubishi i-Miev, Citroe C-Zero, Peugeot-Ion are supported
## State of the project 
* the software is in alpha - experimental state - it has only been tested with a handful of vehicles so far
* alpha and beta testers are welcome
### Supported Platforms 
can4eve is written in Java
It has been tested on
* Raspberry PI
* MacOS
* Microsoft Windows
* an Android version is in preparation.

Since Apple does not support Java on the iPhone and since Microsoft does not support Java on some of its environments can4eve 
would have to be ported to run in these environments. We have obtained a free gluon license to try this out.

## Where to find more info
### Wiki
http://can4eve.bitplan.com/
### Version History
* 2017-06    0.0.1 - non released developer version (alpha test)
* 2017-08    0.0.2 - beta test with OBDLink SX open
* 2018-08-23 0.0.3 - improved gui
* 2019-01-22 0.0.4 - dark mode 
* 2019-01-24 0.0.5 - remembers tabSelection and offers to select tab
