# imagingbook-plugins-all

This repository provides Java source code supplementing 
the digital image processing books by W. Burger & M. J. Burge
(see [imagingbook.com](https://imagingbook.com) for details).

It contains a Maven-based ImageJ project that includes the `imagingbook` 
library and the associated ImageJ plugins as Maven dependencies.
In addition, there is a source code section for adding custom ImageJ plugins.
The project is set up to run ImageJ out of the box, i.e., no Maven build is needed to
get started.
The project also includes the required setup files to be opened in Eclipse.

## To Use:

* Clone this repository.
* Open/import as a Maven project in your favorite IDE.
* Run `ImageJ`.
* Edit your own plugins in `src/main/java` (the associated `.class` files are output to `ImageJ/plugins`).
* To update, run Maven `clean` and `install`.

See also https://github.com/imagingbook/imagingbook-maven-demo-project

Main repository: [**imagingbook-public**](https://github.com/imagingbook/imagingbook-public)


