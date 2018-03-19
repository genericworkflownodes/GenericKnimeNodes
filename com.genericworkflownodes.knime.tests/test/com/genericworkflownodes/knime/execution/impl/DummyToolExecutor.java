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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutionFailedException;

/**
 * Class useful for unit testing.
 * 
 * Simulates real work by just waiting (using {@link Thread#sleep(long)}). It is possible to adjust the behaviour of
 * this class by using {@link #setSleepTime(long)}, {@link #setReturnCode(int)} and {@link #setThrowException(boolean)}.
 * 
 * This class provides debugging information via the methods: {@link #isCompleted()} and {@link #isKilled()}.
 * 
 * @author Luis de la Garza
 */
public class DummyToolExecutor implements IToolExecutor {
    private volatile long sleepTime = 2000;
    private volatile int returnCode = 0;
    private final Object monitor = new Object();
    private volatile boolean started = false;
    private volatile boolean killed = false;
    private volatile boolean completed = false;
    private volatile boolean throwException = false;
    private volatile long timeBeforeSleep;
    
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
     * @param newSleepTime
     *            the sleep time. Must be greater than zero.
     */
    public void setSleepTime(long newSleepTime) {
        if (sleepTime < 0) {
            throw new IllegalArgumentException(
                    "sleepTime must be greater than zero.");
        }
        sleepTime = newSleepTime;
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

    @Override
    public void setCommandGenerator(ICommandGenerator generator) {
    }
    
    @Override
	public ICommandGenerator getCommandGenerator() {
		return null;
	}

    @Override
    public int execute() throws ToolExecutionFailedException {    	
        completed = false;
        killed = false;        
        if (throwException) {
            throw new ToolExecutionFailedException("I failed");
        }
        try {
            synchronized (monitor) {
            	started = true;
            	timeBeforeSleep = System.currentTimeMillis();
                monitor.wait(sleepTime);
            }
        } catch (InterruptedException e) {
            
        } finally {
            completed = started && ((System.currentTimeMillis() - timeBeforeSleep) >= sleepTime);
        }
        return returnCode;
    }

    @Override
    public void prepareExecution(INodeConfiguration nodeConfiguration,
            IPluginConfiguration pluginConfiguration) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void kill() {
        synchronized (monitor) {
        	monitor.notifyAll();
        }
        killed = true;
    }

    @Override
    public int getReturnCode() {
        return returnCode;
    }

    @Override
    public void setWorkingDirectory(File directory) throws IOException {
    }

    @Override
    public List<String> getCommand() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("wait " + sleepTime + "ms");
        return ret;
    }

    @Override
    public LinkedList<String> getToolOutput() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Slept " + sleepTime + "ms, got killed=" + killed);
        return ret;
    }

    @Override
    public LinkedList<String> getToolErrorOutput() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add("Slept " + sleepTime + "ms, got killed=" + killed);
        return ret;
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
