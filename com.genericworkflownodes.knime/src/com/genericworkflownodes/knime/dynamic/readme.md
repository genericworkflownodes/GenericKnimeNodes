# Dynamic Generic KNIME Nodes

The dynamic generic KNIME nodes are a new way of generating KNIME nodes from CTD files and executables. Instead of relying on cumbersome code generation, the dynamic generic KNIME nodes are created at runtime by a so-called *NodeSetFactory*. This factory reads all CTD-files in a folder and creates corresponding KNIME nodes.

Additionally this package contains classes to create a *VersionedNodeSetFactory*. Implementing such a factory allows one to install multiple versions of a plugin (but they have to be in separate features). The nodes in the plugin are then only shown in the KNIME node repository if no newer version of the plugin is installed. Workflows using older nodes can still be loaded, though.

## Creating node set factories for Generic KNIME Nodes

Node set factories are KNIME's way of generating nodes at runtime. To simplify the implementation for GKN, the class `DynamicGenericNodeSetFactory` provides default implementations for executable-based nodes.

For a plugin that provides dynamically generated nodes, three Java classes have to be created:
* A bundle activator class extending `com.genericworkflownodes.knime.custom.GenericActivator`
* A node factory class extending `com.genericworkflownodes.knime.dynamic.DynamicGenericNodeFactory`
* A node set factory class extending `com.genericworkflownodes.knime.dynamic.DynamicGenericNodeSetFactory`

The bundle activator class activates the bundle and provides bundle specific settings. Its implementation is quite straight forward, the only important method is `getPluginConfiguration`.

The node factory class is even more easy to implement. It has a method for supplying a node icon and another for providing the IPluginConfiguration from the bundle activator:

    @Override
	protected String getIconPath() {
		// default.png inside the payload folder
		return "default.png";
	}
	
	@Override
	protected IPluginConfiguration getPluginConfig() {
		return MyBundleActivator.getInstance().getPluginConfiguration();
	}
	
**Important: the icon path is relative to the "payload" folder.**

The node set factory class is the most important of the three, but just as easy to implement. It provides the plugin configuration in a similar fashion as the node factory and additionally implements methods for getting the category path for the nodes and IDs for the individual tools. The method `getNodeFactory` should return the class of the corrsponding node factory class that is described above.

With these three classes in place, one can register the node set factory as an extension. The extension point for that is `com.genericworkflownodes.knime.dynamic.DynamicGenericNodeSetFactory` or, if the nodes should be versioned, `com.genericworkflownodes.knime.dynamic.VersionedNodeSetFactory`. In both cases the extension point requires you to specify the class implementing the DynamicNodeSetFactory.

## Payload Setup

For the executables and CTD files to be discoverable by the framework, they have to be embedded in the correct file system structure. It should look like this:

* payload
    * bin
    * descriptors
    * lib
    * share

The `bin` folder contains the binaries, optionally also in subfolders. The `descriptors` folder contains the CTD files in a flat directory structure, that is without subfolders. The `lib` folder contains libraries required by the tools and `share` contains files such as databases and example data.

To supply different executables for different operating systems, the payload folder can also be provided within a fragment.

## Versioned Dynamic Generic KNIME Nodes

If you register your node set under the extension point `com.genericworkflownodes.knime.dynamic.VersionedNodeSetFactory`, you may install several versions of your plugin. If two versioned node sets are registered from plugins with the same id, only the nodes from the set with the highest version are shown in the node repository. Nodes with lower versions can be loaded in existing workflows, but will be marked as deprecated.

If a node is present in a plugin with a lower version, but not in a newer one, it will show up in the node repository and not be deprecated even if the newer plugin has the same id as the older one.