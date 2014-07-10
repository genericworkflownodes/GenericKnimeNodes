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
package com.genericworkflownodes.knime.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.genericworkflownodes.knime.execution.impl.DummyToolExecutor;

/**
 * Tests for the {@link AsynchronousToolExecutor}.
 * 
 * @author Luis de la Garza
 */
public class AsynchronousToolExecutorTest {

    private DummyToolExecutor dummyTask;
    private AsynchronousToolExecutor asyncExecutor;

    @Before
    public void setUp() {
        dummyTask = new DummyToolExecutor();
        asyncExecutor = new AsynchronousToolExecutor(dummyTask);
    }

    @Test
    public void testNormalExecution() throws Exception {
        dummyTask.setSleepTime(100);
        asyncExecutor.invoke();
        busyWait();
        assertTrue("The underlying task did not complete",
                dummyTask.isCompleted());
    }

    @Test
    public void testFaultyExecution() throws Exception {
        dummyTask.setThrowException(true);
        asyncExecutor.invoke();
        busyWait();
        assertFalse("The underlying task should not have completed",
                dummyTask.isCompleted());
    }

    @Test
    public void testWaitForNormalExecution() throws Exception {
        asyncExecutor.invoke();
        asyncExecutor.waitUntilFinished();
        assertTrue("The underlying task did not complete",
                dummyTask.isCompleted());
    }

    @Test
    public void testWaitForFaultyExecution() throws Exception {
        dummyTask.setThrowException(true);
        asyncExecutor.invoke();
        asyncExecutor.waitUntilFinished();
        assertFalse("The underlying task should not have completed",
                dummyTask.isCompleted());
    }

    @Test(expected = IllegalStateException.class)
    public void testAsynchronousToolExecutorIsNotReusable() throws Exception {
        asyncExecutor.invoke();
        asyncExecutor.invoke();
    }

    @Test
    public void testSeveralThreadsWaitingForNormalExecution() throws Exception {
        final int nThreads = 10;
        Thread[] threads = new Thread[nThreads];
        final AtomicInteger completedThreads = new AtomicInteger(0);
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    asyncExecutor.waitUntilFinished();
                    completedThreads.incrementAndGet();
                }
            };
        }
        asyncExecutor.invoke();
        for (int i = 0; i < nThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < nThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
        assertTrue("The underlying task did not complete",
                dummyTask.isCompleted());
        assertEquals("Some of the threads did not complete", nThreads,
                completedThreads.get());
    }

    @Test
    public void testSeveralThreadsWaitingForFaultyExecution() throws Exception {
        final int nThreads = 10;
        Thread[] threads = new Thread[nThreads];
        final AtomicInteger completedThreads = new AtomicInteger(0);
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    asyncExecutor.waitUntilFinished();
                    completedThreads.incrementAndGet();
                }
            };
        }
        dummyTask.setThrowException(true);
        asyncExecutor.invoke();
        for (int i = 0; i < nThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < nThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
        assertFalse("The underlying task should have not completed",
                dummyTask.isCompleted());
        assertEquals("Some of the threads did not complete", nThreads,
                completedThreads.get());
    }

    @Test
    public void testKill() throws Exception {
        dummyTask.setSleepTime(500000);
        asyncExecutor.invoke();
        asyncExecutor.kill();
        assertTrue("The underlying task should have been killed",
                dummyTask.isKilled());
        assertFalse("The underlying task should not have completed",
                dummyTask.isCompleted());
    }

    private void busyWait() {
        while (!asyncExecutor.isDone()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
