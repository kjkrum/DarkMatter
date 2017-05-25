package com.chalcodes.jtx;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Renders glyphs into <tt>Graphics</tt> objects.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface SoftFont {
	/** 
	 * Gets the size of glyphs drawn by this <tt>SoftFont</tt>.  This is used
	 * to determine the pixel dimensions of a <tt>Display</tt>.
	 */
	public abstract Dimension getGlyphSize();
	
	/**
	 * Draws a glyph.
	 * 
	 * @param value packed character and attributes 
	 * @param blinkOn whether characters with the blink attribute should be rendered on or off
	 * @param graphics the graphics context to render into
	 * @param x
	 * @param y
	 * @see VgaBufferElement
	 */
	public abstract void drawGlyph(int value, boolean blinkOn, Graphics graphics, int x, int y);
	
	// applications can draw underlines and inverted colors by manipulating attributes
}