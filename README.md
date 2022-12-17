# imagingbook-plugins-all

This repository provides Java source code supplementing 
the digital image processing books by W. Burger & M.J. Burge
(see [imagingbook.com](https://imagingbook.com) for details).
This **Maven-based project** includes a basic [**ImageJ**](https://imagej.nih.gov/ij/) setup and 
the complete **imagingbook** library with associated **plugins** as Maven dependencies (JAR files).
In addition, there is a small source code section for adding custom ImageJ plugins.
The project is set up to run ImageJ out of the box, i.e., no Maven build is needed to
get started.

## How to Use:

* Download the [**latest release of this repository**](https://github.com/imagingbook/imagingbook-plugins-all/releases/latest).
* Open/import as a Maven project in your favorite IDE.
* Run `ImageJ`.
* Edit your own plugins in `src/main/java` (the associated `.class` files are output to `ImageJ/plugins`).
* To update, run Maven `clean` and `install`.

Alternatively, see [**imagingbook-maven-demo-project**](https://github.com/imagingbook/imagingbook-maven-demo-project) for a minimal ImageJ/imagingbook setup.

Main repository: [**imagingbook-public**](https://github.com/imagingbook/imagingbook-public)


