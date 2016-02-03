# DictionaryClient
A command line dictionary client that interfaces with RFC2229 Protocol to access dictionary servers

## To Start:

1. Open up a terminal window in the directory containing the files
2. Run "make" to build the jar file
3. Run "java -jar CSdict.jar" (You can include [-d] argument to enter Debug mode and show server info)
4. A CSDict> prompt should now appear

## Available Commands:

1. open SERVER PORT
  * This will open a connection to the specified dictionary server on a given port (ie "open dict.org 2628) Default port is 2628

2. dict
  * Retrieves the list of dictionaries that the server supports

3. set DICTIONARY
  * Allows you to set a specified dictionary (ie "set wn")

4. currdict
  * Specifies the current dictionary

5. define WORD
  * Finds a definition for a provided word in the current dictionary. If no definitions are found it will look through all available dictionaries on the server.

6. match WORD
  * Retrieves an exact match for a provided word and determines the dictionaries that it is available in

7. prefixmatch WORD
  * Locates all the words that begin with a provided prefix

8. close
  * Closes the connection to the server

9 quit
  * exits the program
