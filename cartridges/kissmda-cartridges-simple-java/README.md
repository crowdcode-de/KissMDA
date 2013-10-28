KissMDA cartridge: kissmda-cartridges-simple-java
=================================================
This cartridge generates from Entity and Service classes Java interfaces including their 
attributes, methods and relationships.

UML Profile
-----------

Stereotypes
-----------

**Entity**
* Applied on: class
* Produces Java interfaces with all the attributes, methods and relationships.

**enumeration**
* Applied on: enumeration
* Produces Java enumeration.

**Service**
* Applied on: class
* Produces Java interfaces with all the attributes, methods and relationships. Service stereotype is just the same as Entity stereotype.

**SourceDirectory**
* Applied on: package
* Marks the source model which will be generated.

**Exception**
* Applied on: class
* Produces java checked exception objects. This will produce the same thing as a classifier stereotyped with ApplicationException stereotype.

**ApplicationException**
* Applied on: class
* Produces java application exception objects. Application exceptions are exceptions that are checked exceptions. These are exceptions that client should be able to catch in order to decide whether or not to take some special action.

**UnexpectedException**
* Applied on: class
* Produces java unexpected exception objects. Unexpected exceptions are unchecked runtime exceptions. These are exceptions that clients shouldn't worry about catching but are thrown when some "unexpected" error in the application flow occurs.


Events published
----------------

**Not Available**


Events handled
--------------

**Not Available**

