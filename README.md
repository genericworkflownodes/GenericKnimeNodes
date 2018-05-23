# Generic KNIME Nodes
is a project aimed at the automatic generation of KNIME nodes for generic (command line) tools. It consists of functionality for KNIME developers (extension points, interfaces, ...), tool developers (automatic wrapping of tools into KNIME nodes based on the Common Tool Description; either by generating source code or recently automatically at KNIME startup, including an alternative plugin versioning approach) and KNIME users (intuitive configuration dialogs and node descriptions for any tool you can imagine plus additional nodes for the interaction with standard KNIME nodes).

## The GenericKnimeNodes KNIME plugin
It provides the source code for the KNIME plugin `com.genericworkflownodes.knime` providing basic functionality for further plugins depending on the Generic Workflow Nodes for KNIME mechanism. **NOTE:** For this functionality, at least KNIME 2.7.2 is required and you need to install 
the [KNIME File Handling Nodes](https://www.knime.com/file-handling). This functionality includes additional nodes for reading, writing, looping over and converting tables to and from files. For developers it provides extension points for:
   * registering conversion capabilities for new filetypes that show up in the table to file (and vice versa) nodes (so called mangler and demangler in the sources)
   * command line generation (to specify how to generate the command line from a tool description, if the standard way is not sufficient)
   * execution strategies (to specify how to schedule execution of the underlying tools; want to run it from inside a container? via ssh?)
   * versioned node set factories (to register new node generating plugins with a new version such that nodes shadowed by new versions show up as deprecated and are hidden in the node repository)

## The GenericKnimeNodes Plugin/Node generator
Via "ant" or exporting an executable jar file it provides a mechanism to create source code for your own complete
KNIME plugin:

1. Clone this repo
1. Switch to your GenericKnimeNodes directory
1. Call `ant`. This generates the required base plugin and runs the node generator. Further it will generate the source code for the KNIME plugin in a directory called ```generated_plugin/```
1. Load the GenericKnimeNodes folder and the generated plugin into the KNIME SDK (TODO Link) as Java projects.
1. Run the preconfigured KNIME Instance
1. Enjoy your new nodes!
1. If you want to build an update site for your new nodes, you can do that by creating an update site project
in Eclipse or use buckminster (TODO link to Thorstens blog post).

If you want to build your KNIME nodes from another directory
just call:
```
ant -Dplugin.dir=[your_path]
```
Do not forget to replace `[your_path]` with a valid directory.
e.g.
```
ant -Dplugin.dir=/home/jon.doe/my_plugin_sources
```
(without trailing slash!).

The following directory structure must be provided within
the specified directory:

```
/home/jon.doe/my_plugin_sources
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
```
  
See the sample* directories [here](https://github.com/genericworkflownodes/GenericKnimeNodes/tree/develop/com.genericworkflownodes.knime.node_generator/) for examples.

You can supply the executables for each node in the plugin.
The wrapped binaries have to be supplied in the payload directory
Pleaser refer to payload.README (TODO link and update) for further details.

Alternatively the user can use already installed versions of the 
wrapped programs. These can be configured from within KNIME (see 
Generic Workflow Nodes for KNIME preference tab) (TODO really? Verify).

If you want to ship additional custom nodes written as any other KNIME node in Java code,
you can create one or more OSGI bundles or Eclipse plugin projects (see KNIME node development documentation)
and copy them to a `contributing-plugins` directory.
The supplied plugins will be copied over and registered as part of the overall Eclipse feature (a collection of plugins)
that is generated and thus being installed together with the KNIME nodes themselves.

You have two options to make the target system execute your code:
1) You use the Activator/Plugin class (should be done automatically)
2) You extend the extension point "org.eclipse.ui.startup" if you need to do additional things during startup of your plugin.


