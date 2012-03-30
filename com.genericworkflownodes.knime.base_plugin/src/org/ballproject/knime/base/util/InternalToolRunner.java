package org.ballproject.knime.base.util;

import java.io.File;

public class InternalToolRunner extends ToolRunner {
	private String paramSwitch;
	private File executablePath;

	public void setParamSwitch(String paramSwitch) {
		this.paramSwitch = paramSwitch;
	}

	public void setExecutablePath(File executablePath) {
		this.executablePath = executablePath;
	}

	@Override
	public int run(String... cmds) throws Exception {
		String[] cmds_ = new String[3];

		cmds_[0] = executablePath.getCanonicalPath();
		cmds_[1] = paramSwitch;
		cmds_[2] = "params.xml";

		return super.run(cmds_);
	}
}
