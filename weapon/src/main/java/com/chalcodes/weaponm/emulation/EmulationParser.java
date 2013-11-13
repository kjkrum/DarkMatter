package com.chalcodes.weaponm.emulation;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Arrays;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.jtx.ScrollbackBuffer;
import com.chalcodes.jtx.VgaBufferElement;
import com.chalcodes.weaponm.emulation.lexer.EmulationEventListener;

// FIXME: don't assume that buffer extents x is always 0

public class EmulationParser implements EmulationEventListener {
	private static final Logger log = LoggerFactory.getLogger(EmulationParser.class);
	private final ScrollbackBuffer buffer;

	/** The cursor position. */
	private final Point cursor;
	/** Width of the buffer. */
	private final int columns;
	/** The maximum <tt>y</tt> value the cursor has ever had. */
	private int maxLine;
	/** For manipulating character attributes. */
	private int attributes = VgaBufferElement.DEFAULT_VALUE;
	/**
	 * The cursor <tt>y</tt> value after the last page clear.  The cursor up
	 * command cannot move the cursor above this mark.  This is because the
	 * clear page command does not actually clear anything; it just moves the
	 * cursor to a new line.
	 */
	private int pageMark;
	/** The cursor position at the last save cursor command. */
	private final Point cursorMark;	
	
	public EmulationParser(ScrollbackBuffer buffer) {
		this.buffer = buffer;
		Rectangle extents = buffer.getExtents();		
		columns = extents.width;
		// position cursor to new row at bottom of buffer
		cursor = new Point(0, extents.x + extents.height);
		maxLine = cursor.y;
		buffer.advance(maxLine);
		pageMark = cursor.y;
		cursorMark = new Point(cursor);
	}
	
	public void reset() {
		Rectangle extents = buffer.getExtents();
		cursor.setLocation(0, extents.x + extents.height);
		maxLine = cursor.y;
		buffer.advance(maxLine);
		pageMark = cursor.y;
		cursorMark.setLocation(cursor);
	}

	@Override
	public void literalText(CharSequence seq, int off, int len) {
		//System.out.println("literalText: " + seq.subSequence(off, off + len));
		// TODO: line wrap
		buffer.write(cursor.x, cursor.y, seq, off, len, attributes);
		// advance the cursor
		cursor.x += len;
		if(cursor.x == columns) {
			cursor.x = 0;
			++cursor.y;
			if(cursor.y > maxLine) {
				++maxLine;
				buffer.advance(maxLine);
			}
		}		
	}

	@Override
	public void setAttributes(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		for(int param : params) {
			switch(param) {
			case 0:
				attributes = VgaBufferElement.DEFAULT_VALUE;
				break;
			case 1:
				attributes = VgaBufferElement.setBright(attributes, true);
				break;
			case 2:
				attributes = VgaBufferElement.setBright(attributes, false);
				break;
			case 4:
				attributes = VgaBufferElement.setUnderlined(attributes, true);
				break;
			case 5:
				attributes = VgaBufferElement.setBlinking(attributes, true);
				break;
			case 7:
				attributes = VgaBufferElement.setInverted(attributes, true);
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
				attributes = VgaBufferElement.setForegroundColor(attributes, param - 30);
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
				attributes = VgaBufferElement.setBackgroundColor(attributes, param - 40);
				break;
			default:
				log.warn("unknown character attribute: {}", param);
			}
		}
	}

	@Override
	public void cursorUp(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		if(params.length == 0) --cursor.y;
		else if(params.length == 1) cursor.y -= params[0];
		if(cursor.y < pageMark) cursor.y = pageMark;		
	}

	@Override
	public void cursorDown(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		if(params.length == 0) ++cursor.y;
		else if(params.length == 1) cursor.y += params[0];
		if(cursor.y > maxLine) {
			buffer.advance(cursor.y);
			maxLine = cursor.y;
		}
	}

	@Override
	public void cursorLeft(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		if(params.length == 0) --cursor.x;
		else if(params.length == 1) cursor.x -= params[0];
		if(cursor.x < 0) cursor.x = 0;
	}

	@Override
	public void cursorRight(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		if(params.length == 0) ++cursor.x;
		else if(params.length == 1) cursor.x += params[0];
		if(cursor.x >= columns) cursor.x = columns - 1;		
	}

	@Override
	public void cursorPosition(CharSequence seq, int off, int len) {
		int[] params = extractParams(seq, off, len);
		switch(params.length) {
		case 0:
			cursor.setLocation(0, pageMark);
			break;
		case 1:
			cursor.setLocation(0, pageMark + params[0] - 1);
			break;
		case 2:
			cursor.setLocation(params[1] - 1, pageMark + params[0] - 1);
			break;
		}
		if(cursor.x < 0) cursor.x = 0;
		if(cursor.x >= columns) cursor.x = columns - 1;
		if(cursor.y < pageMark) cursor.y = pageMark;
		if(cursor.y > maxLine) {
			buffer.advance(cursor.y);
			maxLine = cursor.y;
		}
		
	}

	@Override
	public void saveCursor(CharSequence seq, int off, int len) {
		cursorMark.setLocation(cursor);		
	}

	@Override
	public void restoreCursor(CharSequence seq, int off, int len) {
		// FIXME: check buffer extents?
		if(cursorMark.y < pageMark) return;
		cursor.setLocation(cursorMark);
	}

	@Override
	public void carriageReturn(CharSequence seq, int off, int len) {
		cursor.x = 0;		
	}

	@Override
	public void lineFeed(CharSequence seq, int off, int len) {
		++cursor.y;
		if(cursor.y > maxLine) {
			++maxLine;
			buffer.advance(maxLine);
		}		
	}

	@Override
	public void tab(CharSequence seq, int off, int len) {
		cursor.x += 4 - cursor.x % 4;
		if(cursor.x >= columns) cursor.x = columns - 1;		
	}

	@Override
	public void backspace(CharSequence seq, int off, int len) {
		--cursor.x;
		if(cursor.x < 0) cursor.x = 0;		
	}

	@Override
	public void bell(CharSequence seq, int off, int len) {
		Toolkit.getDefaultToolkit().beep();		
	}

	@Override
	public void clearLine(CharSequence seq, int off, int len) {
		// clear from the cursor position to the end of the line
		//synchronized(buffer) {
			//for(int col = cursor.x; col < columns; ++col) {
			//	buffer.setValue(col, cursor.y, BufferElement.DEFAULT_VALUE);
			//}
		//}
		int[] values = new int[columns - cursor.x];
		Arrays.fill(values, VgaBufferElement.DEFAULT_VALUE);
		buffer.setContent(cursor.x, cursor.y, values, 0, values.length);
	}

	@Override
	public void clearScreen(CharSequence seq, int off, int len) {
		++maxLine;
		buffer.advance(maxLine);
		pageMark = maxLine;
		cursor.setLocation(0, maxLine);		
	}
	
	@Override
	public void unknownEscape(CharSequence seq, int off, int len) {
		log.warn("unknown escape code: esc{}", seq.subSequence(off + 1,  off + len));		
	}

	/**
	 * Extracts numeric parameters from an ANSI control sequence.  Blank
	 * parameters (identified by semicolons that do not immediately follow a
	 * numeric parameter) are assigned a value of 1.
	 * 
	 * @param seq
	 * @param off
	 * @param len
	 * @return
	 */
	private int[] extractParams(CharSequence seq, int off, int len) {
		// first count the elements using a mini state machine
		int count = 0;
		boolean inNumber = false;
		for(int i = off; i < off + len; ++i) {
			if(inNumber) {
				inNumber = Character.isDigit(seq.charAt(i));
			}
			else if(seq.charAt(i) == ';') { // blank param
				++count;
			}
			else if(Character.isDigit(seq.charAt(i))) {
				inNumber = true;
				++count;
			}
		}
		// now go through again and parse the numbers
		int[] ints = new int[count];
		int begin = -1;
		count = 0;
		inNumber = false;
		for(int i = off; i < off + len; ++i) {
			if(inNumber) {
				inNumber = Character.isDigit(seq.charAt(i));
				if(!inNumber) {
					ints[count] = Integer.parseInt(seq.subSequence(begin, i).toString());
					++count;
				}
			}
			else if(seq.charAt(i) == ';') { // blank param
				ints[count] = 1; // ANSI screen coordinates are 1-based
				++count;
			}
			else if (Character.isDigit(seq.charAt(i))) {
				inNumber = true;
				begin = i;
			}
		}		
		return ints;		
	}
}
