KissMDA Maven Plugin for Maven App
==================================
You need this Maven plugin to generate the artifacts based on your chosen cartridges.

To use this plugin you can see following example:

```
<plugin>
    <groupId>de.crowdcode.kissmda.maven</groupId>
    <artifactId>kissmda-maven-plugin</artifactId>
    <version>[Version Number]</version>
    <dependencies>
        <dependency>
            <groupId>de.crowdcode.kissmda.cartridges</groupId>
            <artifactId>kissmda-cartridges-simple-java</artifactId>
            <version>[Version Number]</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <transformerScanPackageNames>
		    <transformerScanPackageName>de.crowdcode.kissmda.cartridges</transformerScanPackageName>
		</transformerScanPackageNames>
		<loggingLevel>SEVERE</loggingLevel>
        <targetEncoding>UTF-8</targetEncoding>
		<modelFile>src/main/resources/model/emf/test-uml.uml</modelFile>
		<propertyFile>src/main/resources/application.properties</propertyFile>
    </configuration>
</plugin>
```

A complete example can be found here: [kissmda-app-test pom.xml](https://github.com/crowdcode-de/KissMDA/blob/master/app-examples/kissmda-app-test/pom.xml)
