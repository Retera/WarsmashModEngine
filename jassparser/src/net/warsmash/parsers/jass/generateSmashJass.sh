#!/bin/bash

# NOTE: this was run on a linux computer.
# I used "sudo apt install jflex" and "sudo apt install bison" in a Debian-based distro
# (like Ubuntu, although in my case I used a distro endorsed by the FSF).
# If you need to run this on Windows, I assume it's harder to find jflex and bison
# but if you search the names of those programs, you may find their Windows version(s)
# online. If not, I have included the generated output code, checked in to the repo
# as a Java file, so that you can continue to use the computer generated parser.
# In this case, jflex and bison should only be necessary if you wish to add
# more syntax to the language, at which point you should run the following
# script:

rm SmashJassLexer.java SmashJassParser.java
jflex SmashJassLexer.flex
bison --language=JAVA SmashJassParser.y --verbose -Wcounterexamples
