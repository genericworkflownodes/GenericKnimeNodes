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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles asynchronous execution of IToolExecutor.
 * 
 * Note: AsynchronousToolExecutor is based on the AsyncToolRunner implemented by
 * Marc Röttig.
 * 
 * This class is a simple wrapper that goes around any {@link IToolExecutor}.
 * Note that the {@link #invoke()} method can be used only once. Invoking it
 * more than once will result in a {@link IllegalStateException}.
 * 
 * @author aiche
 * @author Luis de la Garza
 */
public class AsynchronousToolExecutor implements IWaitable {
	// all instances will use the same thread pool
	// using a cached thread pool has the following advantages:
	// 1. if after some time no threads are used, the pool will shrink
	// 2. the pool grows as needed
	private final static ExecutorService EXECUTOR_SERVICE = Executors
			.newCachedThreadPool();

	/**
	 * The executor which should be handled asynchronously.
	 */
	private final IToolExecutor executor;
	// determines if the invoke method has already been called
	private final AtomicBoolean invokeAlreadyCalled;
	// useful when other threads call the waitUntilFinished method
	private final CountDownLatch countdownLatch;
	// the future that wraps around the callable
	private FutureTask<Integer> futureTask;

	/**
	 * C'tor.
	 * 
	 * @param executor
	 *            The executor which should be handled asynchronously.
	 */
	public AsynchronousToolExecutor(final IToolExecutor executor) {
		this.executor = executor;
		countdownLatch = new CountDownLatch(1);
		invokeAlreadyCalled = new AtomicBoolean(false);
		futureTask = new FutureTask<Integer>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return doCall();
			}
		});
	}

	/**
	 * Returns whether the underlying task has completed, regardless of its
	 * status.
	 * 
	 * @return
	 */
	public boolean isDone() {
		return futureTask.isDone();
	}

	/**
	 * Retrieves the return status from the underlying task. If the task is not
	 * yet completed, this method will block the invoker.
	 * 
	 * @return The return code.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public int getReturnCode() throws InterruptedException, ExecutionException {
		return futureTask.get();
	}

	private int doCall() throws Exception {
		try {
			return executor.execute();
		} finally {
			// regardless of what hapenned, make sure to decrease the count in
			// the latch
			countdownLatch.countDown();
		}
	}

	/**
	 * Invokes the {@link IToolExecutor#execute()} method in an asynchronous
	 * way, that is, the invoker will not block while the underlying
	 * {@link IToolExecutor} performs its tasks.
	 * 
	 * @return The return code of the underlying executor.
	 * 
	 * @throws Exception
	 */
	public void invoke() throws Exception {
		// set the atomic value to true and check, atomically, the previous
		// value. for more information, check out getAndSet javadoc
		if (invokeAlreadyCalled.getAndSet(true)) {
			throw new IllegalStateException(
					"The method 'invoke()' can be executed only once!");
		}
		EXECUTOR_SERVICE.execute(futureTask);
	}

	/**
	 * Kills the executed process.
	 */
	public void kill() {
		try {
			executor.kill();
			futureTask.cancel(true);
		} finally {
			// make sure to wake up any thread that is waiting
			countdownLatch.countDown();
		}
	}

	/**
	 * The thread invoking this method will wait until the execution has
	 * completed, regardless of the result.
	 */
	@Override
	public void waitUntilFinished() {
		try {
			countdownLatch.await();
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
