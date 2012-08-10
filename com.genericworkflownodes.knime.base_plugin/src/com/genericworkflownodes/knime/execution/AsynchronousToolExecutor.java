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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles asynchronous execution of IToolExecutor
 * 
 * Note: AsynchronousToolExecutor is based on the AsyncToolRunner implemented by Marc Röttig.
 * 
 * @author aiche
 */
public class AsynchronousToolExecutor implements Callable<Integer> {
	private final CountDownLatch countdownLatch;
	private final AtomicBoolean calledInvoked;

	/**
	 * The executor which should be handled asynchronously.
	 */
	private final IToolExecutor executor;

	/**
	 * C'tor.
	 * 
	 * @param executor
	 *            The executor which should be handled asynchronously.
	 */
	public AsynchronousToolExecutor(final IToolExecutor executor) {
		this.executor = executor;
		countdownLatch = new CountDownLatch(1);
		calledInvoked = new AtomicBoolean(false);
	}

	@Override
	public Integer call() throws Exception {
		// set the atomic value to true and check, atomically, the previous value
		// check getAndSet javadoc :)
		if (calledInvoked.getAndSet(true) == true) {
			throw new IllegalStateException("The method call can be executed only once!");
		}
		try {
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// regardless of what hapenned, make sure to decrease the count in the latch
			countdownLatch.countDown();
		}
		return executor.getReturnCode();
	}

	/**
	 * Kills the executed process.
	 */
	public void kill() {
		executor.kill();
	}

	/**
	 * The thread invoking this method will wait until the execution has completed, regardless of the result.
	 */
	public void waitUntilFinished() {
		try {
			countdownLatch.await();
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
