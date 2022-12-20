This is a local ImageJ installation set up for testing the
plugins contained in this subproject. Note that the other
plugin collections hold only copies of the plugins contained here
and no other collection has a dedicated ImageJ setup.

* The default output folder is set to ImageJ/plugins/ (by <outputDirectory>
  in the project's pom.xml file), which makes Eclipse set the default output folder
  accordingly.
  
* The required imagingbook libraries are imported as a Maven dependency and
  copied to Image/jars.
  
* All other required jar files (dependencies) are copied to ImageJ/jars/ by Maven.

* The specified version of ImageJ itself is copied to ImageJ/ij.jar.
  