package com.chalcodes.jtx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;

/**
 * A <tt>SoftFont</tt> that blits VGA text mode characters from a pre-rendered
 * glyph sheet.  The glyph sheet is stored as a volatile image to enable
 * hardware acceleration.  The pixel dimensions of the glyph sheet determine
 * the glyph size of the font, and must be a multiple of 256 pixels in width
 * and 128 pixels in height.  The size and position of the underline attribute
 * overlay is calculated automatically.
 * <p>
 * The glyph sheet contains 128 rows of 256 glyphs, rendered in all possible
 * combinations of foreground color, background color, and the "bright"
 * attribute.  The color order of the glyph sheet is black, red, green,
 * yellow, blue, magenta, cyan, and white.  For each background color, a row
 * of glyphs is rendered in each foreground color.  This produces the first 64
 * rows of the glyph sheet.  This sequence is then repeated for glyphs with
 * the "bright" attribute set.  With the rows in this order, the row number
 * for any attribute value is simply the value of its seven rightmost bits.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public class VgaSoftFont implements SoftFont {
	protected final Dimension glyphSize;
	protected final BufferedImage bufferedImage;
	protected VolatileImage volatileImage;

	public VgaSoftFont(BufferedImage glyphSheet) {
		int width = glyphSheet.getWidth();
		int height = glyphSheet.getHeight();
		if(width == 0 || height == 0) {
			throw new IllegalArgumentException("glyph sheet must have non-zero width and height");
		}
		if(width % 256 != 0 || height % 128 != 0) {
			throw new IllegalArgumentException("glyph sheet dimensions must be a multiple of 256x128 pixels");
		}
		glyphSize = new Dimension(width / 256, height / 128);
		bufferedImage = glyphSheet;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gs.getDefaultConfiguration();
	    volatileImage = gc.createCompatibleVolatileImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		Graphics2D graphics = (Graphics2D) volatileImage.getGraphics();
		graphics.drawImage(bufferedImage, 0, 0, null);
		graphics.dispose();
	}

	public VgaSoftFont(String resource) throws IOException {
		this(ImageIO.read(VgaSoftFont.class.getResourceAsStream(resource)));
	}

	public VgaSoftFont() throws IOException {
		this("/com/chalcodes/jtx/vga9x16.png");
	}

	@Override
	public Dimension getGlyphSize() {
		return new Dimension(glyphSize);
	}

	@Override
	public void drawGlyph(int value, boolean blinkOn, Graphics graphics, int x, int y) {
		Graphics2D g2d = (Graphics2D) graphics;
		GraphicsConfiguration gc = g2d.getDeviceConfiguration();

		int colorAttr = (value & 0x7F0000) >> 16;
		if((value & VgaBufferElement.INVERTED) != 0 ^ (value & VgaBufferElement.SELECTED) != 0) {
			colorAttr ^= 0x3F;
		}

		// if value has blink attribute and blink is off, draw glyph 0
		if(!blinkOn && (value & VgaBufferElement.BLINKING) != 0) {
			int dx1 = x;
			int dy1 = y;
			int dx2 = dx1 + glyphSize.width;
			int dy2 = dy1 + glyphSize.height;
			int sx1 = 0;
			int sy1 = colorAttr * glyphSize.height;
			int sx2 = sx1 + glyphSize.width;
			int sy2 = sy1 + glyphSize.height;
			blit(g2d, gc, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
		}
		else { // draw the requested glyph
			int character = value & 0xFFFF;
			if(character > 255) character = '?';
			int dx1 = x;
			int dy1 = y;
			int dx2 = dx1 + glyphSize.width;
			int dy2 = dy1 + glyphSize.height;
			int sx1 = character * glyphSize.width;
			int sy1 = colorAttr * glyphSize.height;
			int sx2 = sx1 + glyphSize.width;
			int sy2 = sy1 + glyphSize.height;
			blit(g2d, gc, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
			
			// if value has underline attribute, copy pixels from glyph 219
			if((value & VgaBufferElement.UNDERLINED) != 0) {
				sx1 = 219 * glyphSize.width;
				sx2 = sx1 + glyphSize.width;
				sy2 = sy1 + 1;

				// set the top of the underline
				int uly1 = Math.round((float) glyphSize.height * 7 / 8);
				if(uly1 == glyphSize.height) {
					--uly1;
				}
				dy1 = dy1 + uly1;
				
				// set the thickness of the underline
				int uly2 = Math.round((float) glyphSize.height * 1 / 16);
				if(uly2 == 0) {
					uly2 = 1;
				}
				dy2 = dy1 + uly2;
				
				blit(g2d, gc, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
			}
		}
	}
	
	protected void blit(Graphics2D g2d, GraphicsConfiguration gc, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		do {
			switch(volatileImage.validate(gc)) {
			case VolatileImage.IMAGE_INCOMPATIBLE:
				volatileImage = gc.createCompatibleVolatileImage(bufferedImage.getWidth(), bufferedImage.getHeight());
				//$FALL-THROUGH$
			case VolatileImage.IMAGE_RESTORED:
				Graphics2D volatileGraphics = (Graphics2D) volatileImage.getGraphics();
				volatileGraphics.drawImage(bufferedImage, 0, 0, null);
				volatileGraphics.dispose();
			}
			g2d.drawImage(volatileImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			// make sure image wasn't invalidated during drawing
		} while(volatileImage.validate(gc) != VolatileImage.IMAGE_OK);
	}
}
