This library includes the data types used by Siafu. All such types implement 
the  Publishable interface, meaning they can be "flattened". Flattening is 
Siafu's way of serializing data, and it can be a pain to un-flatten. For 
this reason, the SiafuExternalSerialization library is provided, which 
contains stripped down versions of the Siafu data types for the purpose of:

- Receiving Siafu flatdata data through the external command interface, and 
  rebuild it using the datatype's String contrstuctor and,
  
- Flattening parameters (e.g. a position) to be sent to Siafu using the
  external command interface, using the type's flatten() method.
  
You should not use this library from within Siafu itself or one of its
simulations. The packages have been modified (ading an external subpackage),
but you'll probablly confuse the hell out of yourself if you do so...

Miquel Martin
August 2008