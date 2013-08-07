/*
 * Copyright (c) 2012, Luis de la Garza.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.execution.impl;

import java.io.File;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;

/**
 * Class useful for unit testing.
 * 
 * Simulates real work by just waiting (using {@link Thread#sleep(long)}). It is
 * possible to adjust the behaviour of this class by using
 * {@link #setSleepTime(long)}, {@link #setReturnCode(int)} and
 * {@link #setThrowException(boolean)}.
 * 
 * This class provides debugging information via the methods:
 * {@link #isCompleted()} and {@link #isKilled()}.
 * 
 * @author Luis de la Garza
 */
public class DummyToolExecutor implements IToolExecutor {

	private volatile long sleepTime = 5000;
	private volatile int returnCode = 0;
	private final Object monitor = new Object();
	private volatile boolean killed = false;
	private volatile boolean completed = false;
	private volatile boolean throwException = false;

	/**
	 * Instructs this executor to throw an exception when {@link #execute()} is
	 * invoked.
	 * 
	 * @param throwException
	 */
	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	/**
	 * Sets the sleep time.
	 * 
	 * @param sleepTime
	 *            the sleep time. Must be greater than zero.
	 */
	public void setSleepTime(long sleepTime) {
		if (sleepTime < 0) {
			throw new IllegalArgumentException(
					"sleepTime must be greater than zero.");
		}
		this.sleepTime = sleepTime;
	}

	/**
	 * Sets the return code.
	 * 
	 * @param returnCode
	 *            The return code.
	 */
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#setCommandGenerator
	 * (com.genericworkflownodes.knime.execution.ICommandGenerator)
	 */
	@Override
	public void setCommandGenerator(ICommandGenerator generator) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.genericworkflownodes.knime.execution.IToolExecutor#execute()
	 */
	@Override
	public int execute() throws Exception {
		completed = false;
		killed = false;
		if (throwException) {
			throw new Exception("I failed");
		}
		try {
			synchronized (monitor) {
				monitor.wait(sleepTime);
			}
		} catch (InterruptedException e) {
			// ignore
		} finally {
			completed = true;
		}
		return returnCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#prepareExecution
	 * (com.genericworkflownodes.knime.config.INodeConfiguration,
	 * com.genericworkflownodes.knime.config.INodeConfigurationStore,
	 * com.genericworkflownodes.knime.config.IPluginConfiguration)
	 */
	@Override
	public void prepareExecution(INodeConfiguration nodeConfiguration,
			IPluginConfiguration pluginConfiguration) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.genericworkflownodes.knime.execution.IToolExecutor#kill()
	 */
	@Override
	public void kill() {
		synchronized (monitor) {
			monitor.notifyAll();
		}
		killed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#getReturnCode()
	 */
	@Override
	public int getReturnCode() {
		return returnCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#setWorkingDirectory
	 * (java.io.File)
	 */
	@Override
	public void setWorkingDirectory(File directory) throws Exception {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#getToolOutput()
	 */
	@Override
	public String getToolOutput() {
		return "Slept " + sleepTime + "ms, got killed=" + killed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genericworkflownodes.knime.execution.IToolExecutor#getToolErrorOutput
	 * ()
	 */
	@Override
	public String getToolErrorOutput() {
		return "Slept " + sleepTime + "ms, got killed=" + killed;
	}

	/**
	 * Returns {@code true} if the method {@link #execute()} completed.
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * Returns {@code true} if the method {@link #kill()} was invoked.
	 * 
	 * @return
	 */
	public boolean isKilled() {
		return killed;
	}

}
