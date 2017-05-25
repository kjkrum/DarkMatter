package com.chalcodes.jtx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class Buffer {
	/** Indexed by row, column. */
	private final int[][] mBuffer;
	private final Extents mExtents;

	protected Buffer(final int rows, final int columns) {
		if(rows <= 0 || columns <= 0) {
			throw new IllegalArgumentException("rows <= 0 || columns <= 0");
		}
		mBuffer = new int[rows][columns];
		mExtents = new Extents(0, 0, rows, columns);
	}

	public Extents getExtents() {
		return mExtents;
	}

	public int read(final int row, final int column) {
		return mBuffer[row][column];
	}

//	public void read(final int row, final int column, final int[] dest, final int off, final int len) {
//		System.arraycopy(mBuffer[row], column, dest, off, len);
//	};

	public void write(final int row, final int column, final int[] src, final int off, final int len) {
		System.arraycopy(src, off, mBuffer[row], column, len);
		notifyContentChanged(new Extents(row, column, 1, len));
	}

	protected void clearRow(final int row) {
		Arrays.fill(mBuffer[row], 0);
	}

	private final Set<BufferObserver> mObservers = new CopyOnWriteArraySet<>();

	public boolean addObserver(final BufferObserver observer) {
		if(observer == null) {
			throw new NullPointerException();
		}
		return mObservers.add(observer);
	}

	public boolean removeObserver(final BufferObserver observer) {
		if(observer == null) {
			throw new NullPointerException();
		}
		return mObservers.remove(observer);
	}

	protected void notifyExtentsChanged(final Extents bufferExtents) {
		for(final BufferObserver observer : mObservers) {
			observer.onExtentsChanged(bufferExtents);
		}
	}

	protected void notifyContentChanged(final Extents changedRegion) {
		for(final BufferObserver observer : mObservers) {
			observer.onContentChanged(changedRegion);
		}
	}
}
