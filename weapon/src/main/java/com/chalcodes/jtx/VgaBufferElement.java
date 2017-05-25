package com.chalcodes.jtx;

/**
 * Provides methods for packing a VGA text-mode character into an integer.
 * This class is used by {@link VgaSoftFont} to render the contents of a
 * {@link XxBuffer}.
 * <p>
 * The right 16 bits of the packed value are the character code point, and the
 * left 16 bits contain the display attributes.  From right to left, the
 * attributes are foreground color (3 bits), background color (3 bits), and
 * one bit each for bright, blinking, underlined, and inverted.  Color
 * constants are defined in {@link VgaColors}.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public class VgaBufferElement {
	/** The mask for the foreground color bits. */
	public static final int FG_COLOR		= 0x70000;
	/** The number of bits the foreground color is left-shifted in <tt>value</tt>. */
	public static final int FG_SHIFT		= 16;
	/** The mask for the background color bits. */
	public static final int BG_COLOR		= 0x380000;
	/** The number of bits the background color is left-shifted in <tt>value</tt>. */
	public static final int BG_SHIFT		= 19;
	/** The mask for the shifted bright attribute bit. */
	public static final int BRIGHT			= 0x400000;
	/** The mask for the blinking attribute bit. */
	public static final int BLINKING		= 0x800000;
	/** The mask for the underlined attribute bit. */
	public static final int UNDERLINED		= 0x1000000;
	/** The mask for the inverted attribute bit. */
	public static final int INVERTED		= 0x2000000;
	/** The mask for the selected attribute bit. */
	public static final int SELECTED		= 0x4000000;
	/** 
	 * The default character and attributes.  Represents character 0 and gray
	 * on black.
	 */
	public static final int DEFAULT_VALUE	= 0x70000;
	
	public static int getValue(char c, int fgColor, int bgColor, boolean bright, boolean blinking, boolean underlined, boolean inverted) {
		if(fgColor < 0 || fgColor > 7 || bgColor < 0 || bgColor > 7) {
			throw new IllegalArgumentException("valid color values are 0-7 inclusive");
		}
		int value = c;
		value += (fgColor << FG_SHIFT);
		value += (bgColor << BG_SHIFT);
		if(bright) value += BRIGHT;
		if(blinking) value += BLINKING;
		if(underlined) value += UNDERLINED;
		if(inverted) value += INVERTED;
		return value;
	}
	
	/**
	 * Retrieves the character portion of the packed value.
	 * 
	 * @return
	 */
	public static char getCharacter(int value) {
		return (char) value;
	}
	
	/**
	 * Sets the character portion of the packed value.
	 * 
	 * @param c
	 */
	public static int setCharacter(int value, char c) {
		return (value & 0xFFFF0000) + c;
	}

	/**
	 * Retrieves the foreground color portion of the packed value.
	 * 
	 * @return
	 * @see VgaColors
	 */
	public static int getForegroundColor(int value) {
		return (value & FG_COLOR) >> FG_SHIFT; 
	}
	
	/**
	 * Sets the foreground color portion of the packed value.
	 * 
	 * @param color
	 * @see VgaColors
	 */
	public static int setForegroundColor(int value, int color) {
		return (value & ~FG_COLOR) + ((color & 0x07) << FG_SHIFT);
	}
	
	/**
	 * Retrieves the background color portion of the packed value.
	 * 
	 * @return
	 * @see VgaColors
	 */
	public static int getBackgroundColor(int value) {
		return (value & BG_COLOR) >> BG_SHIFT;
	}
	
	/**
	 * Sets the background color portion of the packed value.
	 * 
	 * @param color
	 * @see VgaColors
	 */
	public static int setBackgroundColor(int value, int color) {
		return(value & ~BG_COLOR) + ((color & 0x07) << BG_SHIFT);
	}
	
	/**
	 * Retrieves the bright property from the packed value.
	 * 
	 * @return
	 */
	public static boolean isBright(int value) {
		return (value & BRIGHT) != 0;
	}
	
	/**
	 * Sets the bright property of the packed value.
	 * 
	 * @param bright
	 */
	public static int setBright(int value, boolean bright) {
		return setBit(value, BRIGHT, bright);
	}
	
	/**
	 * Retrieves the blinking property from the packed value.
	 * 
	 * @return
	 */
	public static boolean isBlinking(int value) {
		return (value & BLINKING) != 0;
	}
	
	/**
	 * Sets the blinking property of the packed value.
	 * 
	 * @param blinking
	 */
	public static int setBlinking(int value, boolean blinking) {
		return setBit(value, BLINKING, blinking);
	}
	
	/**
	 * Retrieves the underlined property from the packed value.
	 * 
	 * @return
	 */
	public static boolean isUnderlined(int value) {
		return (value & UNDERLINED) != 0;
	}
	
	/**
	 * Sets the underlined property of the packed value.
	 * 
	 * @param underlined
	 */
	public static int setUnderlined(int value, boolean underlined) {
		return setBit(value, UNDERLINED, underlined);
	}
	
	/**
	 * Retrieves the inverted property from the packed value.
	 * 
	 * @return
	 */
	public static boolean isInverted(int value) {
		return (value & INVERTED) != 0;
	}
	
	/**
	 * Sets the inverted property of the packed value.
	 * 
	 * @param inverted
	 */
	public static int setInverted(int value, boolean inverted) {
		return setBit(value, INVERTED, inverted);
	}
	
	public static boolean isSelected(int value) {
		return (value & SELECTED) != 0;
	}
	
	public static int setSelected(int value, boolean selected) {
		return setBit(value, SELECTED, selected);
	}
	
	/**
	 * Gets the color from the specified packed value.  Color consists of
	 * foreground color, background color, and the bright attribute.
	 * 
	 * @param value
	 * @return
	 */
	public static int getColor(int value) {
		return value & 0x7F0000;
	}
	
	/**
	 * Sets the color of the packed value to the specified color.  Color
	 * consists of foreground color, background color, and the bright
	 * attribute.
	 * 
	 * @param value
	 * @param color
	 * @return
	 */
	public static int setColor(int value, int color) {
		return (value & ~0x7F0000) + (color & 0x7F0000);
	}
	
	protected static int setBit(int value, int mask, boolean on) {
		if(on) value = value | mask;
		else value = value & ~mask;
		return value;
	}
	
	private VgaBufferElement() { }
}
