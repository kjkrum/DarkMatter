package com.chalcodes.jtx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class Display extends JPanel implements StickyScrollable {
	private final Buffer mBuffer;
	private final SoftFont mFont;
	private final int mGlyphWidth;
	private final int mGlyphHeight;
	private final int mInitialRows;
	private final int mInitialColumns;

	private static final int BLINK_INTERVAL_MS = 750;
	/** True if blinking text is currently visible. */
	private boolean mBlinkOn = true;
	private final Timer mBlinkTimer = new Timer(BLINK_INTERVAL_MS, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mBlinkOn = !mBlinkOn;
			repaint();
		}
	});

	private Extents mExtents;

	private final BufferObserver mBufferObserver = new BufferObserver() {
		@Override
		public void onExtentsChanged(final Extents extents) {
			revalidate();
			if(extents.row != mExtents.row) {
				repaint();
			}
			mExtents = extents;
		}

		@Override
		public void onContentChanged(final Extents changed) {
			repaint((changed.column - mExtents.column) * mGlyphWidth,
					(changed.row - mExtents.row) * mGlyphHeight,
					changed.width * mGlyphWidth,
					changed.height * mGlyphHeight);
		}
	};

	public Display(final Buffer buffer, final SoftFont font, final int initialRows, final int initialColumns) {
		if(buffer == null || font == null) {
			throw new NullPointerException();
		}
		mBuffer = buffer;
		mFont = font;
		final Dimension glyphSize = font.getGlyphSize();
		mGlyphWidth = glyphSize.width;
		mGlyphHeight = glyphSize.height;
		mInitialRows = initialRows;
		mInitialColumns = initialColumns;
		mExtents = mBuffer.getExtents();

		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(final HierarchyEvent e) {
				if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
					if(isShowing()) {
						mBuffer.addObserver(mBufferObserver);
						mBlinkTimer.start();
					}
					else {
						mBuffer.removeObserver(mBufferObserver);
						mBlinkTimer.stop();
					}
				}
			}
		});

		setOpaque(true);
		setBackground(Color.BLACK);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Rectangle clip = g.getClipBounds();
		final int firstRow = mExtents.row + clip.y / mGlyphHeight;
		final int firstColumn = mExtents.column + clip.x / mGlyphWidth;
		final int clipBottom = clip.y + clip.height;
		final int partialRow = clipBottom % mGlyphHeight == 0 ? 0 : 1;
		final int rowLimit = Math.min(mExtents.row + mExtents.height, mExtents.row + clipBottom / mGlyphHeight + partialRow);
		final int clipRight = clip.x + clip.width;
		final int partialColumn = clipRight % mGlyphWidth == 0 ? 0 : 1;
		final int columnLimit = Math.min(mExtents.column + mExtents.width, mExtents.column + clipRight / mGlyphWidth + partialColumn);

		for(int row = firstRow; row < rowLimit; ++row) {
			for(int column = firstColumn; column < columnLimit; ++column) {
				int value = mBuffer.read(row, column);
				if(value != 0) {
					final int x = (column - mExtents.column) * mGlyphWidth;
					final int y = (row - mExtents.row) * mGlyphHeight;
					mFont.drawGlyph(value, mBlinkOn, g, x, y);
				}
			}
		}
	}

	@Override
	public boolean isPreferredSizeSet() {
		return true;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Math.max(mInitialColumns, mExtents.width) * mGlyphWidth,
				Math.max(mInitialRows, mExtents.height) * mGlyphHeight);
	}

	// TODO min and max size

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
		// orientation: SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		// direction: Less than zero to scroll up/left, greater than zero for down/right
		// return should always be positive

		final int increment;
		final int adjustment;

		if(orientation == SwingConstants.VERTICAL) {
			increment = mGlyphHeight;
			adjustment = (visible.y + visible.height) % mGlyphHeight; // align to bottom
		}
		else {
			increment = mGlyphWidth;
			adjustment = visible.x % mGlyphWidth; // align to left
		}

		if(adjustment == 0) return increment;
		else if(direction > 0) return increment - adjustment;
		else return adjustment;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
		// orientation: SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		// direction: Less than zero to scroll up/left, greater than zero for down/right
		// return should always be positive

		final int increment;
		final int adjustment;

		if(orientation == SwingConstants.VERTICAL) {
			increment = visible.height;
			adjustment = (visible.y + visible.height) % mGlyphHeight; // align to bottom
		}
		else {
			increment = visible.width;
			adjustment = visible.x % mGlyphWidth; // align to left
		}

		if(adjustment == 0) return increment;
		else if(direction > 0) return increment - adjustment;
		else return increment + adjustment;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * The value of {@code mExtents.row} on the previous call to {@code
	 * getPreferredViewportPosition}.
	 */
	private int mPreviousRow = 0;

	@Override
	public Point getPreferredViewportPosition(final Rectangle currentViewport, final Rectangle preferredSize) {
		final int rowsScrolled = mExtents.row - mPreviousRow;
		mPreviousRow = mExtents.row;

		final Dimension currentSize = getSize();
		final boolean atBottom = (currentViewport.y + currentViewport.height >= currentSize.height);

		final Point newPosition = currentViewport.getLocation();
		if(atBottom) {
			newPosition.y = preferredSize.height - currentViewport.height;
		}
		else {
			newPosition.y -= rowsScrolled * mGlyphHeight;
		}
		if(newPosition.y < 0) newPosition.y = 0;
		return newPosition;
	}

}
