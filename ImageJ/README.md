# README

This is a ready-to-go Maven project that includes the `imagingbook library and plugin sets, all
packaged in a complete ImageJ runtime setup.

* The project's output folder (for compiled `.class` files) is set to `ImageJ/plugins/` (by the `<outputDirectory>` property
  in the project's `pom.xml` file).
  
* The required `imagingbook` libraries and other dependencies are automatically copied to `Image/jars/` during the Maven build.

* The current version of ImageJ itself (specified in `pom.xml`) is copied to `ImageJ/ij.jar`.
  
