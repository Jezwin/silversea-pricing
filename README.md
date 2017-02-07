JAVA 8:
-------
1. Download java from https://www.java.com/fr/download/
2. Install java
3. Create a new environment variable:
```
name: JAVA_HOME
value: <path to your jdk directory>\bin
```
4. Add `JAVA_HOME` to `PATH` variable
5. Execute `java -version` in command line console: it should display java version

Maven:
------
1. Download maven binary package from : http://maven.apache.org/download.cgi
2. Unzip folder and put it in dev directory
3. Create a new environment variable:
```
name: M2_HOME
value: <path to your maven directory>\bin
```
4. Add M2_HOME variable to PATH
5. Execute `mvn --version` in command line console: it should display maven version
6. create new folder under your maven directory and call it "repository".
7. Change path to local repository in the settings.xml file cated under "conf" directory:
```
<localRepository>path to local repository created in step 6</localRepository>
```
8. Add "adobe-public" profile in settings.xml from  https://repo.adobe.com
9. Activate `adobe-public` profile:
```
<activeProfiles>
    <activeProfile>adobe-public</activeProfile>
</activeProfiles>
```

Eclipse:
--------
1. Download eclipse from : https://eclipse.org/downloads/
2. Set path to you settings.xml file:
```
window > prefrences > maven > User settings
```
3. Set css, html, jsp and xml files encoding to ISO 10646 /Unicode(UTF-8):
```
window > prefrences > web
window > prefrences > xml
```

Git:
----
1. Download git from: https://git-scm.com/downloads
2. install .exe file