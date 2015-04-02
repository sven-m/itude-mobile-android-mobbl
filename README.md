# Android MOBBL
![MOBBL](http://itudemobiledev.files.wordpress.com/2014/02/mobbl-logo.png?w=362&h=203 "MOBBL logo")

A development framework for data centric Android apps.

## Overview

Most current app development is about connecting consumers and employees to back-end systems through their tablet or smartphone. MOBBL aims to make these type of apps easier to develop, port and maintain.

## Documentation

Generic documentation can be found at [Mobbl.org](http://mobbl.org/doc.html).
Documentation about how to get started can be [here](http://mobbl.org/android.html).
For Api documentation, see the [Android API Reference](http://mobbl.org/apis/android/index.html).

## [Changelog](https://github.com/ItudeMobile/itude-mobile-android-mobbl/wiki/Changelog)
Current version: 7.0.0

## Build
### Gradle

#### From maven central

Add maven central to your `build.gradle`:

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
}
 
repositories {
  mavenCentral()
}
```

Then declare Android Util within your dependencies:

```groovy
dependencies {
  ...
  compile ('com.itude.mobile.android.mobbl:android-mobbl-lib:7.0.0@aar') {
        transitive=true
	}
}
```

### Maven

#### From maven central

To use Android Util within your Maven build simply add

```xml
<dependency>
	<groupId>com.itude.mobile.android.mobbl</groupId>
	<artifactId>android-mobbl-lib</artifactId>
	<version>7.0.0</version>
	<type>aar</type>
</dependency>
```

to your pom.xml

## Contribute

If you find a bug or have a new feature you want to add, just create a pull request and submit it to us. You can also [file an issue](https://github.com/ItudeMobile/itude-mobile-android-mobbl/issues/new).

Please note, if you have a pull request, make sure to use the [develop branch](https://github.com/ItudeMobile/itude-mobile-android-mobbl/tree/develop) as your base.

## License
The code in this project is licensed under the Apache Software License 2.0, per the terms of the included LICENSE file.
