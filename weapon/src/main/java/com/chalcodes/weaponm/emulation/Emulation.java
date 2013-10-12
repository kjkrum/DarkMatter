package com.chalcodes.weaponm.emulation;

import java.nio.BufferOverflowException;
import java.nio.CharBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.emulation.lexer.EmulationLexer;
import com.chalcodes.weaponm.emulation.lexer.UnderflowException;

/**
 * Handles buffering for the emulation lexer.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Emulation {
	private static final Logger log = LoggerFactory.getLogger(Emulation.class.getSimpleName());
	private final CharBuffer buffer = CharBuffer.allocate(4096);
	private final EmulationLexer lexer;
	
	public Emulation(EmulationLexer lexer) {
		this.lexer = lexer;
	}
	
	public void write(String string) {
		try {
			buffer.put(string);
		}
		catch(BufferOverflowException e) {
			log.warn("emulation buffer overflow");
		}
		buffer.flip();
		try {
			int consumed = lexer.lex(buffer, 0, buffer.length(), false);
			buffer.position(consumed);
			buffer.compact();
		} catch (UnderflowException e) {
			if(buffer.length() == buffer.capacity()) {
				log.warn("emulation buffer underflow");
				buffer.clear();
			}
		}
	}
}
