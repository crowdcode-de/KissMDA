KissMDA Maven Archetype for Cartridge Developers
================================================
This archetype helps you to begin your simple Cartridge Maven project.

To run the archetype:

```
mvn archetype:generate                                  
  -DarchetypeGroupId=de.crowdcode.kissmda.maven.archetype
  -DarchetypeArtifactId=kissmda-maven-cartridge-archetype
  -DarchetypeVersion=1.0.0-SNAPSHOT
  -DgroupId="my-groupid"
  -DartifactId="my-artifactId"
```

whereas:
* **"my-groupid"** == my newly created KissMDA cartridge Maven group
* **"my-artifactId"** == my newly creadted KissMDA cartridge Maven artifact