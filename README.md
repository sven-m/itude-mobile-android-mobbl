# Android MOBBL

A development framework for data centric mobile web apps.

## Overview

Most current app development is about connecting consumers and employees to back-end systems through their tablet or smartphone. MOBBL aims to make these type of apps easier to develop, port and maintain.

## Documentation

Generic documentation can be found at [Mobbl.org](http://mobbl.org/doc.html)

## [Changelog](https://github.com/ItudeMobile/itude-mobile-android-mobbl/wiki/Changelog)
Current version: 3.2.0.11

## Build
#### Maven

To use Android Mobbl within your maven build simply add

```xml
<dependency>
  <groupId>com.itude.mobile.android.mobbl</groupId>
  <artifactId>mobbl-core-lib</artifactId>
  <version>${core.lib.version}</version>
  <type>apklib</type>
</dependency>
```
and the [Android Util](https://github.com/ItudeMobile/itude-mobile-android-util)

```xml
<dependency>
  <groupId>com.itude.mobile.android.util</groupId>
	<artifactId>android-util-lib</artifactId>
	<version>${util.lib.version}</version>
	<type>apklib</type>
</dependency
```

to your pom.xml.

## Contribute

If you find a bug or have a new feature you want to add, just create a pull request and submit it to us. You can also [file an issue](https://github.com/ItudeMobile/itude-mobile-android-mobbl/issues/new).

Please note, if you have a pull request, make sure to use the [develop branch](https://github.com/ItudeMobile/itude-mobile-android-mobbl/tree/develop) as your base.

#### Formatting

For contributors using Eclipse there's a [formatter](http://mobbl.org/downloads/code-format.xml) available.

## License
The code in this project is licensed under the Apache Software License 2.0, per the terms of the included LICENSE file.
