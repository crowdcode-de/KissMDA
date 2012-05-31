KissMDA
=======

Keep It Simple Stupid, MDA!

After working a while with MDA / MDSD technologies like AndroMDA, oAW, ... we feel like we need just another MDA tool!
Why?

1. We still like AndroMDA, yes the project is very much alive and it has already proven itself to be a strong Open Source 
project, since AndroMDA has survived the come and go of its comitters. One problem we see is the complexity of the 
project. Yes, you need to model the cartridge to build your own cartridge. In the beginning it looks like "eat
your own dog food" but at the end it makes the things much more complex to build. 

2. oAW has a different story. After going to Eclipse it seems that the project has no activity anymore. No news anymore
from oAW 5 release. It's really a pitty. The big problem of oAW is its Domain Specific Language (DSL) Xtend und Xpand. Since
they depend on their own editor capability in Eclipse and the project seems to be dead, you definitely stuck with Eclipse 3.4!
No development anymore.

Our solution is KissMDA (Keep It Simple Stupid, MDA!).

1. KissMDA is a pure Java API approach. No DSL. OK, we still need a templating engine. But that's it!

2. No need to have special Eclipse plugin, Java, Java and Java!