package com.chalcodes.jtx;

import java.util.Iterator;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class ScrollingBuffer extends Buffer {
	private Extents mExtents;

	public ScrollingBuffer(final int rows, final int columns) {
		super(rows, columns);
		mExtents = new Extents(0, 0, 0, columns);
	}

	@Override
	public Extents getExtents() {
		return mExtents;
	}

	@Override
	public int read(final int row, final int column) {
		return super.read(translate(row), column);
	}

	@Override
	public void write(final int row, final int column, final int[] values, final int off, final int len) {
		advance(row);
		super.write(translate(row), column, values, off, len);
	}

	private void advance(final int row) {
		final int nextRow = mExtents.row + mExtents.height;
		if(row >= nextRow) {
			final int limit = row + 1;
			final int height = Math.min(limit, super.getExtents().height);
			mExtents = new Extents(limit - height, mExtents.column, height, mExtents.width);
			for(int r = Math.max(nextRow, mExtents.row); r < limit; ++r) {
				clearRow(translate(r));
			}
			notifyExtentsChanged(mExtents);
		}
		else if (row < mExtents.row) {
			throw new IndexOutOfBoundsException();
		}
	}

	private int translate(final int row) {
		if(row < mExtents.row || row >= mExtents.row + mExtents.height) {
			throw new IndexOutOfBoundsException();
		}
		return row % super.getExtents().height;
	}
}
