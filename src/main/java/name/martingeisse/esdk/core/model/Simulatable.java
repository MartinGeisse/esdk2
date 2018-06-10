/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

/**
 * This interface is used to mark objects for which a simulation model object (of type T) is available. This gives
 * some help in not passing the wrong objects as keys when fetching dependency simulation objects.
 */
public interface Simulatable<T> {
}
