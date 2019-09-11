## Simple JSON Parser

Parses JSON strings to Java data structures, in particular:

* Numbers go to long or double and might be promoted to BigInteger or BigDecimal if required.
* Strings go to java strings
* Booleans go to java boolean
* JSON objects go to `java.util.Map` (`HashMap`)
* JSON arrays go to `java.util.List` (`ArrayList`)

This parser is **slower** than [Jackson](https://github.com/FasterXML/jackson) but is smaller and it was fun to build.
