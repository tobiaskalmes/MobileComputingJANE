/*****************************************************************************
 * 
 * SimpleOnDemandPseudoXMLParser.java
 * 
 * $Id: SimpleOnDemandPseudoXMLParser.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.dynamic;

import java.io.*;

/**
 * Please note that this parser is not a "real" XML parser: it does not validate and it does not 
 * really parse XML files. But it *does* read the files written by LinkCalculator and that is
 * its sole purpose!
 * 
 * As soon as I find some "real" XML parser that allows "on demand parsing" I will replace
 * the current parser with it.
 * 
 * For internal use only. Do not use this class in your code.
 */
public class SimpleOnDemandPseudoXMLParser {
	private BufferedInputStream inputStream;
	private TagHandler tagHandler;
	private LookAhead lookAhead;
	private CharBuffer buffer;
	private final static int START = 0;
	private final static int READ_ELEMENT = 1;
	private final static int READ_TEXT = 2;
	private final static int FINISHED = 3;

	/**
	 * @param inputStream
	 * @param tagHandler
	 * @throws PrematureEOFException
	 * @throws IOException
	 */
	public SimpleOnDemandPseudoXMLParser(InputStream inputStream, TagHandler tagHandler)
		throws PrematureEOFException, IOException {
		if (inputStream == null || tagHandler == null) {
			throw new IllegalArgumentException("inputStream and tagHandler must be != null");
		}
		this.inputStream = new BufferedInputStream(inputStream);
		this.tagHandler = tagHandler;
		int first = inputStream.read();
		if (first == -1) {
			throw new PrematureEOFException("inputStream does not contain any data!");
		} else {
			lookAhead = new LookAhead((char) first);
		}
		buffer = new CharBuffer(2048);
	}

	/**
	 * @throws IOException
	 * @throws PrematureEOFException
	 * @throws ParseException
	 */
	public void parseNext() throws IOException, PrematureEOFException, ParseException {
		int state = START;
		while (state != FINISHED) {
			switch (state) {
				case START :
					skipWhiteSpace();
					if (lookAhead.isDefined()) {
						char next = lookAhead.getCharacter();
						if (next == '<') {
							nextChar();
							if (lookAhead.isDefined()) {
								next = lookAhead.getCharacter();
								if (next == '!') {
									skipComment();
									continue;
								} else if (next == '?') {
									skipHeader();
									continue;
								} else {
									state = READ_ELEMENT;
									continue;
								}
							} else {
								throw new PrematureEOFException();
							}
						} else {
							state = READ_TEXT;
							continue;
						}
					} else {
						throw new PrematureEOFException();
					}
					//break;

				case READ_TEXT :
					tagHandler.text(readText());
					state = FINISHED;
					break;

				case READ_ELEMENT :
					if (lookAhead.isDefined()) {
						if (lookAhead.getCharacter() == '/') {
							nextChar();
							String name = readName();
							readEnd();
							tagHandler.endElement(name);
							state = FINISHED;
							continue;
						} else {
							String name = readName();
							XMLAttributes attributes = readAttributes();
							tagHandler.startElement(name, attributes);
							skipWhiteSpace();
							if (lookAhead.isDefined()) {
								char next = lookAhead.getCharacter();
								if (next == '/') {
									nextChar();
									readEnd();
									tagHandler.endElement(name);
									state = FINISHED;
									continue;
								} else if (next == '>') {
									nextChar();
									state = FINISHED;
									continue;
								} else {
									throw new ParseException("Expected /> or >");
								}
							} else {
								throw new PrematureEOFException("EOF occured while parsing element start");
							}

						}
					} else {
						throw new PrematureEOFException("EOF occured while parsing element");
					}
					//break;
			}
		}
	}

	private void skipComment() throws IOException, PrematureEOFException {
		while (lookAhead.isDefined() && lookAhead.getCharacter() != '>') {
			nextChar();
		}
		if (lookAhead.isDefined() && lookAhead.getCharacter() == '>') {
			nextChar();
		} else {
			throw new PrematureEOFException("EOF occured while parsing comment");
		}
	}

	private char nextChar() throws IOException, PrematureEOFException {
		if (lookAhead.isDefined()) {
			char toReturn = lookAhead.getCharacter();
			int next = inputStream.read();
			if (next == -1) {
				lookAhead.redefine();
			} else {
				lookAhead.redefine((char) next);
			}
			return toReturn;
		} else {
			throw new PrematureEOFException();
		}
	}

	private String readText() throws PrematureEOFException, IOException {
		//			StringBuffer buf = new StringBuffer();
		buffer.clear();
		while (lookAhead.isDefined() && lookAhead.getCharacter() != '<') {
			//				buf.append(nextChar());
			buffer.append(nextChar());
		}
		if (!lookAhead.isDefined()) {
			throw new PrematureEOFException("EOF occured while reading text");
		}
		//			return buf.toString();
		return buffer.getAsString();
	}

	private void readEnd() throws IOException, PrematureEOFException {
		if (lookAhead.isDefined() && lookAhead.getCharacter() == '>') {
			nextChar();
		} else {
			throw new PrematureEOFException("EOF occured while expecting end of element");
		}

	}

	private void skipWhiteSpace() throws IOException, PrematureEOFException {
		while (lookAhead.isDefined() && Character.isWhitespace(lookAhead.getCharacter())) {
			nextChar();
		}
	}

	private void skipHeader() throws IOException, PrematureEOFException {
		while (lookAhead.isDefined() && lookAhead.getCharacter() != '>') {
			nextChar();
		}
		if (lookAhead.isDefined() && lookAhead.getCharacter() == '>') {
			nextChar();
		} else {
			throw new PrematureEOFException("EOF occured while parsing header");
		}
	}

	private String readName() throws IOException, PrematureEOFException {
		//			StringBuffer buf = new StringBuffer();
		buffer.clear();
		while (lookAhead.isDefined()
			&& lookAhead.getCharacter() != ' '
			&& lookAhead.getCharacter() != '>'
			&& lookAhead.getCharacter() != '=') {
			char next = nextChar();
			if (next != '/') {
				//					buf.append(next);
				buffer.append(Character.toLowerCase(next));
			}
		}
		if (!lookAhead.isDefined()) {
			throw new PrematureEOFException("EOF occured while parsing name");
		}
		//			if (buf.length() == 0) {
		if (buffer.getLength() == 0) {
			return null;
		}
		//			return buf.toString().toLowerCase();
		return buffer.getAsString();
	}

	private XMLAttributes readAttributes() throws IOException, PrematureEOFException, ParseException {
		XMLAttributes attributes = new XMLAttributes();
		while (true) {
			skipWhiteSpace();
			if (lookAhead.isDefined()) {
				if (lookAhead.getCharacter() == '/' || lookAhead.getCharacter() == '>') {
					break;
				}
			} else {
				throw new PrematureEOFException("EOF occured while parsing attributes");
			}
			attributes.addAttribute(readAttribute());
		}
		return attributes;
	}

	private XMLAttribute readAttribute() throws IOException, PrematureEOFException, ParseException {
		skipWhiteSpace();
		String name = readName();
		skipWhiteSpace(); // FIXME: is whitespace allowed between name and = ???
		if (lookAhead.isDefined()) {
			if (lookAhead.getCharacter() == '=') {
				nextChar();
				String value = readValue();
				return new XMLAttribute(name, value);
			} else {
				throw new ParseException("= expected");
			}
		} else {
			throw new PrematureEOFException("EOF occured when expecting =");
		}
	}

	private String readValue() throws IOException, PrematureEOFException, ParseException {
		skipWhiteSpace(); // FIXME: is whitespace allowed between = and value ???
		buffer.clear();
		if (lookAhead.isDefined()) {
			if (lookAhead.getCharacter() != '"' && lookAhead.getCharacter() != '\'') {
				throw new ParseException("Expecting \" or '");
			}
			char marker = nextChar();
			//				StringBuffer buf = new StringBuffer();
			while (lookAhead.isDefined() && lookAhead.getCharacter() != marker) {
				//					buf.append(nextChar());
				buffer.append(nextChar());
			}
			if (!lookAhead.isDefined()) {
				throw new PrematureEOFException("EOF occured while parsing attribute value");
			}
			nextChar();
			//				return buf.toString();
			return buffer.getAsString();
		} else {
			throw new PrematureEOFException("EOF occured while parsing attribute value");
		}
	}
	
	private static class CharBuffer {
		private char[] buffer;
		private int pos;
	
		public CharBuffer(int initialSize) {
			buffer = new char[initialSize];
			pos = 0;
		}
	
		public void append(char c) {
			if (pos == buffer.length) {
				char[] newBuffer = new char[buffer.length*2];
				System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
				buffer = newBuffer;
			}
			buffer[pos++] = c;
		}
	
		public void clear() {
			pos = 0;
		}

		public int getLength() {
			return pos+1;
		}
	
		public String getAsString() {
			return new String(buffer, 0, pos);
		}
	
		public String getAsStringAndClear() {
			String result = getAsString();
			clear();
			return result;
		}
	}
	
	private static class LookAhead {
		private char character;
		private boolean defined;
	
		public LookAhead() {
			defined = false;
		}
	
		public LookAhead(char ch) {
			this.character = ch;
			defined = true;
		}
	
		public char getCharacter() {
			return character;
		}
	
		public boolean isDefined() {
			return defined;
		}
	
		public void redefine() {
			defined = false;
		}
	
		public void redefine(char ch) {
			character = ch;
			defined = true;
		}
	}
}