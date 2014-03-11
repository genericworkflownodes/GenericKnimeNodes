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

/**
 * Exposes one single method, {@link #waitUntilFinished()} in order to simplify
 * the common task of waiting for completion of a task.
 * 
 * @author Luis de la Garza
 */
public interface IWaitable {

    /**
     * Forces the invoking thread to wait until completion of the executed task.
     * Note that this method does not throw any exception, forcing
     * implementations to handly any possible {@link InterruptedException}.
     */
    void waitUntilFinished();
}
