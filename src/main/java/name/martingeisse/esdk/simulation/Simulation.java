/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.simulation;

import java.util.PriorityQueue;

/**
 *
 */
public final class Simulation implements SimulationContext {

	private final PriorityQueue<ScheduledEvent> eventQueue = new PriorityQueue<>();
	private long now = 0;
	private boolean stopped = false;

	@Override
	public void fire(Runnable eventCallback, long ticks) {
		if (eventCallback == null) {
			throw new IllegalArgumentException("eventCallback cannot be null");
		}
		if (ticks < 0) {
			throw new IllegalArgumentException("ticks cannot be negative");
		}
		eventQueue.add(new ScheduledEvent(now + ticks, eventCallback));
	}

	public void run() {
		while (!stopped && !eventQueue.isEmpty()) {
			ScheduledEvent event = eventQueue.remove();
			now = event.when;
			event.callback.run();
		}
	}

	public void stop() {
		stopped = true;
	}

	private static class ScheduledEvent implements Comparable<ScheduledEvent> {

		final long when;
		final Runnable callback;

		ScheduledEvent(long when, Runnable callback) {
			this.when = when;
			this.callback = callback;
		}

		@Override
		public int compareTo(ScheduledEvent o) {
			return Long.compare(when, o.when);
		}

	}

}
