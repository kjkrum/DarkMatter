package com.chalcodes.jtx;

import java.awt.Rectangle;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public interface BufferObserver {
	void onExtentsChanged(final Extents extents);
	void onContentChanged(final Extents changed);
}
