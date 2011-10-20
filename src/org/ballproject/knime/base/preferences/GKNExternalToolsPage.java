package org.ballproject.knime.base.preferences;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.external.ExtToolDB;
import org.ballproject.knime.base.external.ExtToolDB.ExternalTool;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class GKNExternalToolsPage extends PreferencePage implements IWorkbenchPreferencePage 
{

	public GKNExternalToolsPage()
	{
		super();
		IPreferenceStore store = GenericNodesPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
        setDescription("KNIME GKN external tools DB");
	}
	
	@Override
	public void init(IWorkbench wb)
	{
	}

	@Override
	protected Control createContents(Composite parent)
	{
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite c = new Composite(sc, SWT.NONE);
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		c.setLayout(fillLayout);

		sc.setContent(c);
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	    
	    IPreferenceStore preferenceStore = getPreferenceStore();
	    
	    Map<String,List<ExternalTool>> plugin2tools = ExtToolDB.getInstance().getToolsByPlugin();
	    
	    for(String pluginname: plugin2tools.keySet())
	    {
	    	for(ExternalTool tool: plugin2tools.get(pluginname))
			{
	    		String[] toks = pluginname.split("\\.");
	    		
	    		String name = tool.getToolname();
	    		if(toks!=null)
	    			name = toks[toks.length-1]+" - "+tool.getToolname();
	    		
	    		FileFieldEditor toolpath = new FileFieldEditor(tool.getKey(), name, c);
	    		
	    		toolpath.setPreferenceStore(getPreferenceStore());
	    		toolpath.load();
	    		String val = preferenceStore.getString(toolpath.getPreferenceName());
	    		toolpath.setStringValue((val==null?"":val));
	    		toolpathes.add(toolpath);
	    		extTools.add(tool);
			}	
	    }
	    
	    return sc;
	}
	
	private List<FileFieldEditor> toolpathes = new ArrayList<FileFieldEditor>();
	private List<ExternalTool>    extTools   = new ArrayList<ExternalTool>();
	
	@Override
	public boolean performOk() 
	{
	    // Get the preference store
	    IPreferenceStore preferenceStore = getPreferenceStore();
	    
	    int idx = 0;
	    for(FileFieldEditor fe : toolpathes)
	    {
	    	ExtToolDB.getInstance().setToolPath(extTools.get(idx), fe.getStringValue());
	    	GenericNodesPlugin.log("[OK] setting toolpath to "+fe.getStringValue()+" for tool "+extTools.get(idx));
	    	preferenceStore.setValue(fe.getPreferenceName(),fe.getStringValue());
	    	idx++;
	    }

	    // Return true to allow dialog to close
	    return true;
	}

	@Override
	protected void performApply()
	{
	    // Get the preference store
	    IPreferenceStore preferenceStore = getPreferenceStore();
	    
	    int idx = 0;
	    for(FileFieldEditor fe : toolpathes)
	    {
	    	ExtToolDB.getInstance().setToolPath(extTools.get(idx), fe.getStringValue());
	    	GenericNodesPlugin.log("[Apply] setting toolpath to "+fe.getStringValue()+" for tool "+extTools.get(idx));
	    	preferenceStore.setValue(fe.getPreferenceName(),fe.getStringValue());
	    	idx++;
	    }
	}
	
	

}
