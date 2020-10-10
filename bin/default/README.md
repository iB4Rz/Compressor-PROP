# Group Description
##### Ibars Cubel, Albert
##### Muñoz Busto, Isaac
##### Clemente Marín, Daniel
##### Pérez Josende, Alexandre

##### albert.ibars.cubel@est.fib.upc.edu
##### isaac.munoz.busto@est.fib.upc.edu
##### daniel.clemente.marin@est.fib.upc.edu
##### alexandre.perez.josende@est.fib.upc.edu

## Extra Features!
- [x] Algorithm parameters (Dictionary size, JPEG quality...) Available in UI
- [x] Custom Gradle system. Pack project, run program, tests and more in only one simple comand on any supported platform
- [x] Manual in app supporting HTML
- [x] Statistics per file instead of only total statistics
- [x] Random access to both compressed and decompressed data and fast Display functionality for ppm images and other files
- [x] Simple but powerful UI design, following all design rules learnt in IDI

## How-to's:
This will assume you dont have gradle, if you have gradle 6.0 installed you can run the following commands replacing the executable part of the commands with the executable name installed in your system.

### Run
Use the .jar files under EXE/ if some .jar files are missing or recompilation its needed, see the section below

### Re/Packaging the Project
This will regenerate all .jar files under EXE/ and regenerate the documentation under DOCS/
#### On Linux:
`> ./gradlew PackProject`
#### On Windows:
`> gradlew.bat PackProject`

### Update and Run Tests:
Tests can be found under EXE/ but its useful to recompile and run tests on the go. This will also update test .jar's
#### Unit Test:
##### On Linux:
`> ./gradlew UnitTest`
##### On Windows:
`> gradlew.bat UnitTest`
#### Extra Tests
##### On Linux:
`> ./gradlew ExtraTests`
##### On Windows:
`> gradlew.bat ExtraTests`

### Update and Run Main Program:
Main program can be found under EXE/ but its useful to recompile and run on the go. This will also update the .jar file
#### On Linux:
`> ./gradlew Main`
#### On Windows:
`> gradlew.bat Main`

### Re/Generating Documentation Only:
Documentation can be found under DOCS/Documentation but its useful to regenerate it on the go
#### On Linux:
`> ./gradlew javadoc`
#### On Windows:
`> gradlew.bat javadoc`

### Clean
Its not really needed but its useful to know.
#### On Linux:
`> ./gradlew clean`
#### On Windows:
`> gradlew.bat clean`
