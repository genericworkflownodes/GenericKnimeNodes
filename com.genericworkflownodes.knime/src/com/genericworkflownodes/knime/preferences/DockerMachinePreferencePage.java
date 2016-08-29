package com.genericworkflownodes.knime.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.genericworkflownodes.knime.GenericNodesPlugin;

public class DockerMachinePreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    /**
     * The {@link DirectoryFieldEditor} to select the installation directory for
     * the Docker-Machine command.
     */
    private DirectoryFieldEditor dockerMachineInstallDir;

    /**
     * The {@link DirectoryFieldEditor} to select the installation directory for
     * the underlying VM driver commands used by 'docker-machine'.
     */
    private DirectoryFieldEditor vmDriverInstallDir;

    /**
     * The {@link BiikeanFieldEditor} to specify whether $HOME should be used as 
     * TMP directory, which is needed on Mac and Windows to mount directories into
     * Docker container
     */
    private BooleanFieldEditor homeTMPFieldEditor;

    /**
     * The {@link BiikeanFieldEditor} to specify whether Docker Toolbox is used as 
     * instead of the native Docker implementations, which requires a docker-machine
     * is constantly running.
     * Docker container
     */
    private BooleanFieldEditor dockerToolBoxFieldEditor;
    
    
    public DockerMachinePreferencePage() {
        super(GRID);
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Docker Perfernece Page"); //$NON-NLS-1$
    }
    
    
    @Override
    public void init(IWorkbench workbench) {
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();
        GenericNodesPlugin.setDockerInstallationDir(
                store.getString(PreferenceInitializer.DOCKER_MACHINE_INSTALLATION_DIRECTORY));
        GenericNodesPlugin.setVmInstllationDir(
                store.getString(PreferenceInitializer.VM_INSTALLATION_DIRECTORY));
        GenericNodesPlugin.setDockerToolBoxUsage(
                store.getBoolean(PreferenceInitializer.VM_INSTALLATION_DIRECTORY));
    }

    @Override
    protected void createFieldEditors() {
        // installation directory for docker-machine
        dockerMachineInstallDir = new DirectoryFieldEditor(
                PreferenceInitializer.DOCKER_MACHINE_INSTALLATION_DIRECTORY,
                "Docker Instllation Directory", //$NON-NLS-1$
                getFieldEditorParent());
        addField(this.dockerMachineInstallDir);
        dockerMachineInstallDir.setPreferenceStore(getPreferenceStore());
        // allow empty value if docker-machine is not installed
        dockerMachineInstallDir.setEmptyStringAllowed(true);
        dockerMachineInstallDir
                .setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        dockerMachineInstallDir.setPage(this);
        dockerMachineInstallDir.setErrorMessage(
                "Invalid Docker-Machine installation directory."); //$NON-NLS-1$
        dockerMachineInstallDir.showErrorMessage();
        dockerMachineInstallDir.load();
        
        vmDriverInstallDir = new DirectoryFieldEditor(
                PreferenceInitializer.VM_INSTALLATION_DIRECTORY,
                "Vm Installation Directory of Docker-Machine", //$NON-NLS-1$
                getFieldEditorParent());
        addField(vmDriverInstallDir);
        vmDriverInstallDir.setPreferenceStore(getPreferenceStore());
        // allow empty value if docker-machine is not installed
        vmDriverInstallDir.setPage(this);
        vmDriverInstallDir.setEmptyStringAllowed(true);
        vmDriverInstallDir.setErrorMessage(
                "Invalid VM installation directory."); //$NON-NLS-1$
        vmDriverInstallDir.showErrorMessage();
        vmDriverInstallDir
                .setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        vmDriverInstallDir.load();

        dockerToolBoxFieldEditor = new BooleanFieldEditor(
                PreferenceInitializer.DOCKER_MACHINE_USAGE, 
                "Docker-Toolbox usage", getFieldEditorParent());
        addField(dockerToolBoxFieldEditor);
    }

    
    @Override
    public boolean performOk() {
        IPreferenceStore store = GenericNodesPlugin.getDefault()
                .getPreferenceStore();
        String  dockerInstallDir = dockerMachineInstallDir.getStringValue();
        String vmInstallDir = vmDriverInstallDir.getStringValue();
        boolean flag = dockerToolBoxFieldEditor.getBooleanValue();
        store.setValue(PreferenceInitializer.DOCKER_MACHINE_INSTALLATION_DIRECTORY, dockerInstallDir);
        store.setValue(PreferenceInitializer.VM_INSTALLATION_DIRECTORY, vmInstallDir);
        store.setValue(PreferenceInitializer.DOCKER_MACHINE_USAGE, flag);
        
        GenericNodesPlugin.setDockerToolBoxUsage(flag);
        GenericNodesPlugin.setDockerInstallationDir(dockerInstallDir);
        GenericNodesPlugin.setVmInstllationDir(vmInstallDir);
        return true;
    }
}
