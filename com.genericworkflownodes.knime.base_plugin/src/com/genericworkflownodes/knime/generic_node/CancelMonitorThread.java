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
package com.genericworkflownodes.knime.generic_node;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;

import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.IWaitable;

/**
 * This thread monitors the execution context to determine if a cancelation was requested.
 * 
 * @author Luis de la Garza
 */
public class CancelMonitorThread extends Thread implements IWaitable {
	private final AsynchronousToolExecutor asyncExecutor;
	private final ExecutionContext exec;

	public CancelMonitorThread(final AsynchronousToolExecutor asyncExecutor, final ExecutionContext exec) {
		this.asyncExecutor = asyncExecutor;
		this.exec = exec;
	}

	@Override
	public void run() {
		boolean cancelRequested = false;
		while (!asyncExecutor.isDone() && cancelRequested == false) {
			try {
				// if cancel was requested, an exception will be thrown
				exec.checkCanceled();
			} catch (CanceledExecutionException e) {
				cancelRequested = true;
				asyncExecutor.kill();
			}
			// wait a bit, if needed
			if (cancelRequested == false) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public void waitUntilFinished() {
		try {
			join();
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
