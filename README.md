KissMDA - Keep It Simple Stupid, MDA!
=====================================

After working a while with MDA / MDSD technologies like AndroMDA, oAW, ... we feel like we need just another MDA tool!
Why?

1. We still like [AndroMDA](http://www.andromda.org), yes the project is very much alive and it has already proven itself to be a strong Open Source 
project, since AndroMDA has survived the come and go of its comitters. One problem we see is the complexity of the 
project. Yes, you need to model the cartridge to build your own cartridge. In the beginning it looks like "eat
your own dog food" but at the end it makes the things much more complex to build. 

2. [oAW](http://www.openarchitectureware.org) has a different story. Until the version 4.3 it seems everything looks very smooth. After the project went 
to Eclipse it seems that the project has no activity anymore. No news anymore from oAW 5 release. 
It's really a pitty. The big problem of oAW is its Domain Specific Language (DSL) Xtend und Xpand. Since
they depend on their own editor capabilities in Eclipse and because the project seems to be dead, you will 
definitely stuck with Eclipse 3.4! No development anymore in the future.

Our solution is KissMDA (Keep It Simple Stupid, MDA!).

1. KissMDA is a pure Java API approach 
--------------------------------------
No DSL. OK, we still need a templating engine and it also depends on an Eclipse plugin. Therefore we will choose the best templating engine available wisely. Nope! This is not true!
* For generating Java code we will use Eclipse JDT. Check this out: http://stackoverflow.com/questions/121324/a-java-api-to-generate-java-source-files
* For generating XML documents we will use Java DOM API, StAX API or just simple JAXB from already generated Java classes. Check this out for comparison: http://docs.oracle.com/javase/tutorial/jaxp/stax/why.html
* For generating any other things we will use Java! Only Java, no DSL!    
     
2. No need to have special Eclipse plugins
------------------------------------------
We just use Java with its Java editor in Eclipse! Or if you like you can choose other Java IDE. 
We are independent from Eclipse.

3. We use the best UML tool available 
-------------------------------------
At the moment we prefer [MagicDraw](http://www.nomagic.com/products/magicdraw.html) as our UML tool. Important is that we use Eclipse UML2 API to access our UML model.
As long as your UML tool offers Eclipse UML2 API you can integrate it in KissMDA. 
Just don't forget this: your UML model is your source code so treat it as a first class citizen just like your Java code.

4. We'll try not to use XML in our framework
--------------------------------------------
Instead we'll implement annotations as much as we need it.

5. Maven will be the core part of KissMDA 
-----------------------------------------
We'll generate the codes with a Maven plugin, we'll generate the skeleton of 
the MDA application and the cartridge project with Maven archetypes. No other choice! Maven is our core!

6. IoC / DI is everywhere
--------------------------------------------
We use DI (Dependency Injection) everywhere to make our code testable. 
We choose [Guice](https://code.google.com/p/google-guice) for our DI framework.


We are still in the beginning of the project, so if you want to influence our direction, you are welcome to do so!

For more information please check out our Wiki pages: https://github.com/crowdcode-de/KissMDA/wiki

**KissMDA team    
[Lofi](http://lofidewanto.blogspot.com), [Ingo](http://www.dueppe.com), Markus**