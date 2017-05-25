package com.chalcodes.weaponm.ui;

import javax.swing.SwingUtilities;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple Swing worker.
 *
 * @author Kevin Krumwiede
 */
abstract public class SimpleSwingWorker<Result> {
	private static final Executor gExecutor = Executors.newCachedThreadPool();

	abstract protected Result doInBackground() throws Exception;

	protected void onResult(final Result result) {}

	protected void onError(final Exception error) {}

	public final void execute() {
		gExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					final Result result = doInBackground();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							onResult(result);
						}
					});
				}
				catch(final Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							onError(e);
						}
					});
				}
			}
		});
	}
}
