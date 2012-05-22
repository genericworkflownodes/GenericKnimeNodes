/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.execution;

import java.util.concurrent.Callable;

/**
 * Handles asynchronous execution of IToolExecutor
 * 
 * @note AsynchronousToolExecutor is based on the AsyncToolRunner implemented by
 *       Marc Röttig.
 * 
 * @author aiche
 */
public class AsynchronousToolExecutor implements Callable<Integer> {

	private IToolExecutor executor;

	public AsynchronousToolExecutor(IToolExecutor executor) {
		this.executor = executor;
	}

	@Override
	public Integer call() throws Exception {
		try {
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return executor.getReturnCode();
	}

	public void kill() {
		executor.kill();
	}

}
