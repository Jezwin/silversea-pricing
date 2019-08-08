# Table of content

1. [Environment installation](#environment-installation)
	1. [JAVA 8](#java-8)
	2. [Maven](#maven)
	3. [Eclipse](#eclipse)
	4. [Git](#git)
2. [Building the project](#building-the-project)
	1. [Dependencies](#dependencies)
	2. [Available profiles](#available-profiles)
3. [Annexes](#annexes)

# Environment installation

## JAVA 8

1. Download JAVA JDK from Oracle [http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html]()
2. Install JAVA
3. Create a new environment variable:

```
name: JAVA_HOME
value: <path to your jdk directory>\bin
```

4. Add `JAVA_HOME` to `PATH` variable
5. Execute `java -version` in command line console: it should display java version

## Maven

1. Download maven binary package from : [http://maven.apache.org/download.cgi]()
2. Unzip folder and put it in dev directory
3. Create a new environment variable:

```
name: M2_HOME
value: <path to your maven directory>\bin
```

4. Add `M2_HOME` variable to `PATH`
5. Execute `mvn --version` in command line console: it should display maven version
6. Copy the `settings.xml` file from Annexe section into `%HOMEPATH%/.m2` on Windows or into `~/.m2` in Unix based environments

## Eclipse

1. Download eclipse from : https://eclipse.org/downloads/
2. Set path to you settings.xml file:

```
window > prefrences > maven > User settings
```

3. Set css, html, jsp and xml files encoding to ISO 10646/Unicode (UTF-8):

```
window > prefrences > web
window > prefrences > xml
```

## Git

1. Download GIT from: [https://git-scm.com/downloads]()
2. Install according to your system.


# Building the project

The project use maven as build configuration manager. Build and local installation are launched using maven commands, for exemple installing the project package on a local author environment :

```
mvn clean install -Pauto-install-package
```

## Dependencies

The project depends on [silversea-aem-parent](https://gitlab-paris.sqli.com/silversea/silversea-aem-parent) project, which have to be cloned in local environment and built using classical maven command :

```
mvn clean install
```

## Available profiles

The following maven profiles are available for the build:

* `auto-install-package`: install the packages generated from `silversea-com-ui` and `silversea-com-configs` submodules on local author environment
* `auto-install-bundle`: install OSGI bundle generated from `silversea-com-core` submodule on local author environment
* `local-publish`: combined with other profiles, install on local publish environment


## Available variables

The following maven variables are available for the build and by `auto-install-package` and `auto-install-bundle` profiles :

* `cq.url`: URL of AEM instance used (default: `http://localhost:4502`)
* `cq.user`: AEM user (default: `admin`)
* `cq.password`: AEM password (default: `admin`)

# Annexes

## `settings.xml` file

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository/>
    <interactiveMode/>
    <usePluginRegistry/>
    <offline/>
    <pluginGroups/>
    <servers/>
    <mirrors/>
    <proxies/>
    <profiles>
        <!-- ====================================================== -->
        <!-- A D O B E   P U B L I C   P R O F I L E                -->
        <!-- ====================================================== -->
        <profile>
            <id>adobe-public</id>

            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>

            <properties>
                <releaseRepository-Id>adobe-public-releases</releaseRepository-Id>
                <releaseRepository-Name>Adobe Public Releases</releaseRepository-Name>
                <releaseRepository-URL>http://repo.adobe.com/nexus/content/groups/public</releaseRepository-URL>
            </properties>

            <repositories>
                <repository>
                    <id>adobe-public-releases</id>
                    <name>Adobe Public Repository</name>
                    <url>http://repo.adobe.com/nexus/content/groups/public</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>

            <pluginRepositories>
                <pluginRepository>
                    <id>adobe-public-releases</id>
                    <name>Adobe Public Repository</name>
                    <url>http://repo.adobe.com/nexus/content/groups/public</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>
    <activeProfiles/>
</settings>
```