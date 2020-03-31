
Included is my application for the mediaPlayer for assignment 2. Here's a little information about the implementation.
The when using the top menus:
	
	To add:
	- type into the fields for searching for either series or episode, then from the top menu select "Add" from the 
	appropriate selection to add your typed results to the library (assuming they provide a fetch result).

	To Remove:
	- select the Episode to remove and select Episode->remove from the top menu. A pop-up window will prompt confirmation to remove.
	- to remove a series, select anywhere within the series in the tree panel to the left (e.g., any episode, root series directory).
		Then select Series->remove from the top menu panel and a window will prompt confirmation to remove. 

	The search:
	- The search function will automatically add whatever series and season you searched to the library (if it is not a duplicate) and tree.
		To remove a searched item, use the top menu for series and episode panels to remove.

	I believe all other implementation is straightforward :)


Thanks!
Adam Clifton (akclifto@asu.edu)



------------------------------
------------------------------
Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
Version: January 2020

See http://pooh.poly.asu.edu/Ser321

Purpose: provide basis for assignment 2 solution for Ser321 Spring 2020.
This sample contains open source source code that you may use in generating
student solutions. It also contains in compiled form code that is not
open source, but is freely availble through download in the US. This includes
the classes to manipulate JSON, Gluon's JavaFX, and the instructor's view
user-interface for the assignment. You will need to register with
OMDb to obtain an api key to get this example to run properly.
See: https://www.omdbapi.com/apikey.aspx

This project is designed to run on either Debian Buster Linux or MacOS. It
requires jdk13.

Building and running the sample is done with Ant.
This example depends on the following frameworks:
1. Ant
   see: http://ant.apache.org/
2. Json for Java as implemented (reference) by Doug Crockford.
   See: https://github.com/stleary/JSON-java
3. Gluon's JavaFX for Java13 on Linux.
   See: https://gluonhq.com/products/javafx/

To build and run the example, you will need to have Ant installed on
your system, with the antlib target extensions.
(see the CppFraction example from unit1 material for installing these in your home directory).

Run the sample with the command:
ant sample -Domdbkey=place-your-omdb-key-here -DuserId=Tim.Lindquist

To clean the project directory:
ant clean

The project directory  includes docs directory containing the javadocs
for the instructor provided software for the user-interface view,
which you will use in creating your solution.

end
