package com.chalcodes.weaponm.terminal;

import com.chalcodes.weaponm.emulation.analysis.AnalysisAdapter;
import com.chalcodes.weaponm.emulation.lexer.Lexer;
import com.chalcodes.weaponm.emulation.lexer.LexerException;
import com.chalcodes.weaponm.emulation.node.EOF;
import com.chalcodes.weaponm.emulation.node.Token;
import org.apache.commons.io.input.CharSequenceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.CharBuffer;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class Emulation {
	private static final Logger gLog = LoggerFactory.getLogger(Emulation.class);
	private final CharBuffer mBuffer = CharBuffer.allocate(4096);
	private final Lexer mLexer = new Lexer(new PushbackReader(new CharSequenceReader(mBuffer)));
	private final Parser mParser = new Parser();

	public void write(final String text) {
		mBuffer.put(text);
		mBuffer.flip();
		while(true) {
			try {
				final Token token = mLexer.next();
				token.apply(mParser);
				if(token instanceof EOF) {
					if(mBuffer.hasRemaining()) {
						gLog.warn("unrecognized token: " + token.getText());
					}
					else {
						mBuffer.clear();
						mBuffer.put(token.getText());
						break;
					}
				}
			} catch(IOException | LexerException e) {
				// TODO log
			}
		}
	}

	private class Parser extends AnalysisAdapter {
		// TODO all the overrides!
	}
}

