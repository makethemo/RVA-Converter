package com.makethemo.reversing.rvaconverter.header;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageFileHeader {

	private final int machine;
	private final int numberOfSections;
	private final int sizeOfOptionalHeader;
	private final int startingPointOfOptionalHeader;

	public ImageFileHeader(ByteBuffer bb, int startingPointOfFileHeader) {
		bb.position(startingPointOfFileHeader);
		bb.order(ByteOrder.nativeOrder());
		machine = bb.getChar();
		System.out.println(Integer.toHexString(machine));

		numberOfSections = bb.getChar();
		System.out.println("The number of sections: " + String.valueOf(numberOfSections));

		bb.position(bb.position() + 12);
		sizeOfOptionalHeader = bb.getChar();
		System.out.println("Size of optional header: " + String.valueOf(sizeOfOptionalHeader));

		startingPointOfOptionalHeader = bb.position() + 2;
	}

	public int getMachine() {
		return machine;
	}

	public int getNumberOfSections() {
		return numberOfSections;
	}

	public int getSizeOfOptionalHeader() {
		return sizeOfOptionalHeader;
	}

	public int getStartingPointOfOptionalHeader() {
		return startingPointOfOptionalHeader;
	}
}
