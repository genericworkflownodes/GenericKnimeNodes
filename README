This package provides the source code for the KNIME plugin

  com.genericworkflownodes.knime

providing basic functionality for further plugins depending 
on the Generic Workflow Nodes for KNIME mechanism.

NOTE: At least KNIME 2.7.2 is required and you need to install 
the "KNIME File Handling Nodes".

Creating your personal KNIME nodes is as easy as typing "ant"!

1. Switch to you Generic Workflow Nodes for KNIME directory
2. Call "ant"
   This generates the required base plugin and the node generator. 
   Further it will generate the source code for the KNIME plugin in 
   a directory called ```generated_plugin/```
3. Load the generic knime node projects and the generated plugin into 
   the KNIME SDK.
4. Run the preconfigured KNIME Instance
5. Enjoy your new nodes!

If you want to build your KNIME nodes from another directory
just call:

   ant -Dplugin.dir=[your_path]

Do not forget to replace [your_path] with a valid directory.
e.g.

   ant -Dplugin.dir=/home/jon.doe/my_plugin_sources
   (without trailing slash!)

The following directory structure must be provided within
the specified directory:

your
plugin
directory
  │
  ├── plugin.properties
  │
  ├── descriptors (place your ctd files and mime.types
  │                here)
  │
  ├── payload (place your binaries here)
  │
  ├── icons (the icons to be used must be here)
  │
  ├── contributing-plugins (optional; place all your OSGI
  │                         bundles and Eclipse plugin projects
  │                         here)
  ├── DESCRIPTION (A short description of the project)
  │
  ├── LICENSE (Licensing information of the project)
  │
  └── COPYRIGHT (Copyright information of the project)
  
  
See the sample directory of your Generic Workflow Nodes
for KNIME directory for an example.

You can supply the executables for each node in the plugin.
The wrapped binaries have to be supplied in the payload directory
Pleaser refer to payload.README for further details.

Alternatively the user can use already installed versions of the 
wrapped programs. These can be configured from within KNIME (see 
Generic Workflow Nodes for KNIME preference tab).

If you want to ship custom Java code as well you can create
one or more OSGI bundles or Eclipse plugin projects and
copy them to the "contributing-plugins" directory.
The supplied plugins will be part of the Eclipse feature
and thus being installed together with the KNIME nodes themselves.

You have two options to make the target system execute your code:
1) You use the Activator/Plugin class.
2) You extend the extension point "org.eclipse.ui.startup".

If you want to build an update site for your new nodes, please
see the KNIME developer forum first, then contact us.
