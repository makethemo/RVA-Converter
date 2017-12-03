package com.makethemo.reversing.rvaconverter.header;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageOptionalHeader {
	
	private final long imageBase;

	public ImageOptionalHeader(ByteBuffer bb, int startingPointOfOptionalHeader) throws IOException {
		bb.position(startingPointOfOptionalHeader);
		int optionalHeaderSignature = bb.getChar();

		if (optionalHeaderSignature == PE.IMAGE_OPTIONAL_HEADER32) {
			bb.position(bb.position() + 26);
			imageBase = bb.getInt();

		} else if (optionalHeaderSignature == PE.IMAGE_OPTIONAL_HEADER64) {
			bb.position(bb.position() + 22);
			imageBase = bb.getLong();

		} else {
			throw new IOException("Unknown optional header...");
		}

		System.out.println(Long.toHexString(imageBase));
	}

	public long getImageBase() {
		return imageBase;
	}
}
