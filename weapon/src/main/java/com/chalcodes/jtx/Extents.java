package com.chalcodes.jtx;

/**
 * A range of elements in a {@code Buffer}.
 *
 * @author Kevin Krumwiede
 */
public class Extents {
	public final int row;
	public final int column;
	public final int height;
	public final int width;

	public Extents(final int row, final int column, final int height, final int width) {
		this.row = row;
		this.column = column;
		this.height = height;
		this.width = width;
	}

	@Override
	public String toString() {
		return String.format("row %d, column %d, height %d, width %d", row, column, height, width);
	}
}
