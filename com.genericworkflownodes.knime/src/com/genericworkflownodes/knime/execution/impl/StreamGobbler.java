package com.genericworkflownodes.knime.execution.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class StreamGobbler extends Thread{
    /**
     * Captures the stderr/stdout stream of the running process to avoid
     * deadlocks.
     * 
     * Inspired by
     * http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=4.
     * 
     * and
     * 
     * org.knime.base.node.util.exttool.CommandExecution#StdErrCatchRunnable
     * 
     * @author aiche
     */
        /**
         * The stream that is gobbled.
         */
        final InputStream m_is;

        /**
         * The string where the extracted messages are stored.
         */
        final LinkedList<String> m_buffer;

        StreamGobbler(final InputStream is) {
            m_is = is;
            m_buffer = new LinkedList<String>();
        }

        @Override
        public void run() {
            final InputStreamReader isr = new InputStreamReader(m_is);
            final BufferedReader br = new BufferedReader(isr);

            try {
                String line = null;
                while ((line = br.readLine()) != null) {
                    synchronized (m_buffer) {
                        m_buffer.add(line);
                    }
                }
            } catch (final IOException ioe) {
                //LOGGER.error("LocalToolExecutor: Error in stream gobbler.",ioe);
            } finally {
                try {
                    br.close();
                } catch (final IOException ioe) {
                    // then don't close it..
                }
            }
        }

        /**
         * Gives access to the gobbled string.
         * 
         * @return
         */
        public LinkedList<String> getContent() {
            return m_buffer;
        }
}
