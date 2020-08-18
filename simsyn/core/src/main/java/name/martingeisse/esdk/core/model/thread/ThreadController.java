package name.martingeisse.esdk.core.model.thread;

/**
 *
 */
public abstract class ThreadController<M> {

	private ThreadItem<M> threadItem;

	void setThreadItem(ThreadItem<M> threadItem) {
		this.threadItem = threadItem;
	}

	public abstract void run() throws InterruptedException;

	protected final void fire(Runnable callback, long ticks) {
		threadItem.sendFireEventToItem(callback, ticks);
	}

	protected final M yield() throws InterruptedException {
		return threadItem.yield();
	}

}
