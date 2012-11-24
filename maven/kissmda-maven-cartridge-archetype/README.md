KissMDA Maven Archetype for Cartridge Developers
================================================
This archetype helps you to begin your simple Cartridge Maven project.

To run the archetype:

```
mvn archetype:generate                                  
  -DarchetypeGroupId=de.crowdcode.kissmda.maven
  -DarchetypeArtifactId=kissmda-maven-cartridge-archetype
  -DarchetypeVersion=1.0.0
  -DgroupId="my-groupid"
  -DartifactId="my-artifactId"
  -DprojectName="my-project-name"
  -DprojectDescription="my-project-description"
  -DtransformerName="the-transformer-classname"
```

where:
* **"my-groupid"** == my newly created KissMDA cartridge Maven group
* **"my-artifactId"** == my newly creadted KissMDA cartridge Maven artifact
* **"my-project-name"** == the name of my project
* **"my-project-description"** == the description of my project
* **"the-transformer-classname"** == the name of the transformer class