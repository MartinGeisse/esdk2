package name.martingeisse.esdk.core.model.thread;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.util.ConcurrentModificationException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 *
 */
public class ThreadItem<M> extends Item {

	private final ThreadController<M> controller;
	private final SynchronousQueue<M> messageQueue = new SynchronousQueue<>();
	private final BlockingQueue<Runnable> responseQueue = new LinkedBlockingQueue<>();
	private boolean lastResponse;

	public ThreadItem(Design design, ThreadController<M> controller) {
		super(design);
		this.controller = controller;
		controller.setThreadItem(this);
	}

	public ThreadController getController() {
		return controller;
	}

	@Override
	protected void initializeSimulation() {
		new Thread(() -> {
			try {
				controller.run();
			} catch (InterruptedException e) {
				// ignore
			}
		}).start();
		fire(this::consumeResponses, 0);
	}

	private void consumeResponses() {
		try {
			lastResponse = false;
			while (!lastResponse) {
				responseQueue.take().run();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected final void sendMessageToController(M message) {
		if (!responseQueue.isEmpty()) {
			throw new ConcurrentModificationException("response queue not empty -- responses have been sent at the wrong time");
		}
		try {
			messageQueue.put(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		fire(this::consumeResponses, 0);
	}

	final void sendFireEventToItem(Runnable callback, long ticks) {
		responseQueue.add(() -> {
			fire(callback, ticks);
		});
	}

	final M yield() throws InterruptedException {
		responseQueue.add(() -> {
			lastResponse = true;
		});
		return messageQueue.take();
	}

}
