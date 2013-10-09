KissMDA - Keep It Simple Stupid, MDA!
=====================================

After working a while with MDA / MDSD technologies like AndroMDA, oAW, ... we feel like we need just another MDA tool!
Why?

1. We still like [AndroMDA](http://www.andromda.org), yes the project is very much alive and it has already proven itself to be a strong Open Source 
project, since AndroMDA has survived the come and go of its comitters. AndroMDA is in our humble opinion the best 
project in MDA / MDSD area sofar.
One problem we see is the complexity of the project. Yes, you need to model the cartridge to build your own cartridge. 
In the beginning it looks like "eat your own dog food" but at the end it makes the things much more complex to build. 
Just take a look [how you can build your own cartrige in AndroMDA](http://www.andromda.org/docs/andromda-documentation/steps-to-write-a-cartridge/index.html).
You also need to use a lot of XML files and elements to [configure your cartridge](http://www.andromda.org/docs/andromda-documentation/steps-to-write-a-cartridge/site9.html).
As an application developer you have all those XML elements to be able [configure AndroMDA](http://www.andromda.org/docs/configuration.html) 
which makes AndroMDA a configuration monster.

2. [oAW](http://www.openarchitectureware.org) has a different story. Until the version 4.3 it seems everything looks very smooth. After the project went 
to Eclipse it seems that the project has no activity anymore. No news anymore from oAW 5 release. 
It's really a pitty. The big problem of oAW is its Domain Specific Language (DSL) Xtend und Xpand. Since
they depend on their own editor capabilities in Eclipse and because the project seems to be dead, you will 
definitely stuck with Eclipse 3.4! No development anymore in the future.

3. [Acceleo](http://www.eclipse.org/acceleo) looks pretty nice. The project is active and alive. 
The main problem is that it uses a DSL named MOFM2T (MTL) to generate artifacts. You can read the [getting started document
of Acceleo](http://wiki.eclipse.org/Acceleo/Getting_Started) to understand how Acceleo generator works. Additionally everything in Acceleo is quite Eclipse centered.
So you need the MTL editor from Eclipse, imagine if something happens just like oAW. You will stuck with an old Eclipse
version!

4. [TigerTeam TRIMM – Model Driven Generator](http://trimm.tigerteam.dk) is just Open Sourced in July 2013.
We analyzed the code at [Bitbucket.org](https://bitbucket.org/tigerteam/trimm/src) and found out that the idea with the Events and Listeners as [Extensions](http://trimm.tigerteam.dk/trimm-java/trimm-java-extensions) is a very good idea.
TRIMM also uses Maven and does not have any dependencies to Eclipse. Special to this framework is that it uses its own [metamodel](http://goo.gl/MHIfLw) and does not use UML2 metamodel for the generators. 
This framework works for [MagicDraw and Enterprise Architect](http://goo.gl/Z5QQrq) and it can read the files directly from those UML tools. It also uses its [own Java code generator](http://goo.gl/A5HLEm) which is basically based on String outputs.
TRIMM offers some ready to use cartridges like Java, JPA and Webservice. It seems that TRIMM does not suffer of the problems mentioned above and uses almost the same approach of KissMDA (see our solution below). Following are in our opinion the weak points:
** Using its own Java generator which is basically based on String outputs is not the best way. Using Eclipse JDT is a lot more easier and you always get syntaxtically correct Java codes which can be easily unit tested, see this [example](http://goo.gl/Au42Ql).
** Interesting to compare the transformers for Java code generation. In TRIMM you have the class [JavaGenerator.java](http://goo.gl/XpKQSM). In KissMDA you use the class [SimpleJavaTransformer.java](http://goo.gl/YuUVRS). 
They both are the main entering point for the generation of Java codes. Both classes look quite similar from the way of doing the works they need to do. Using DI framework like Guice in KissMDA makes the code more readable.

There are some other MDA / MDSD tools available outside: [Taylor](http://taylor.sourceforge.net), [Topcased](http://www.topcased.org), [openMDX](http://www.openmdx.org). But all of them suffer the problems we mentioned above.

***Our solution is KissMDA (Keep It Simple Stupid, MDA!)***.

1. KissMDA is a pure Java API approach 
--------------------------------------
[No DSL, no Polyglot Programming](http://lofidewanto.blogspot.de/2011/10/why-is-polyglot-programming-or-do-it.html). OK, we still need a templating engine and it also depends on an Eclipse plugin. Therefore we will choose the best templating engine available wisely. Nope! This is not true!
* For generating Java code we will use Eclipse JDT. Check this out: http://stackoverflow.com/questions/121324/a-java-api-to-generate-java-source-files
* For generating XML documents we will use Java DOM API, StAX API or just simple JAXB from already generated Java classes. Check this out for comparison: http://docs.oracle.com/javase/tutorial/jaxp/stax/why.html
* For generating any other things we will use Java! Only Java, no DSL!    
     
2. No need to have special Eclipse plugins
------------------------------------------
We just use Java with its Java editor in [Eclipse](http://www.eclipse.org)! Or if you like you can choose other Java IDE. 
We are independent from Eclipse.

3. We use the best UML tool available 
-------------------------------------
At the moment we prefer [MagicDraw](http://www.nomagic.com/products/magicdraw.html) as our UML tool. Important is that we use Eclipse UML2 API to access our UML model.
As long as your UML tool offers Eclipse UML2 API you can integrate it in KissMDA. 
Just don't forget this: your UML model is your source code so treat it as a first class citizen just like your Java code.

4. We'll try not to use XML in our framework
--------------------------------------------
Instead we'll implement annotations as much as we need it.

5. Maven is the core part of KissMDA 
-----------------------------------------
We use [Maven](http://maven.apache.org) standard project layout. We'll generate the codes with a Maven plugin, we'll generate the skeleton of 
the MDA application and the cartridge project with Maven archetypes. No other choice! Maven is our core!

6. IoC / DI is everywhere
--------------------------------------------
We use DI (Dependency Injection) everywhere to make our code testable. 
We choose [Guice](https://code.google.com/p/google-guice) for our DI framework.


We are still in the beginning of the project, so if you want to influence our direction, you are welcome to do so!

**For more information please check out our Wiki pages: https://github.com/crowdcode-de/KissMDA/wiki**   
**For the latest news please check out our [KissMDA Google+](https://plus.google.com/u/0/b/118347092823913152560/118347092823913152560/posts)**

Our current build status at BuildHive CloudBees: [![Build Status](https://buildhive.cloudbees.com/job/crowdcode-de/job/KissMDA/badge/icon)](https://buildhive.cloudbees.com/job/crowdcode-de/job/KissMDA/)

**KissMDA team    
[Lofi](http://lofidewanto.blogspot.com), [Ingo](http://www.dueppe.com), Markus**
