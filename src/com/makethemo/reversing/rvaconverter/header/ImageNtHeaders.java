package com.makethemo.reversing.rvaconverter.header;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageNtHeaders {

	private volatile ImageFileHeader fileHeader;
	private volatile ImageOptionalHeader optionalHeader;

	private final ByteBuffer bb;
	private final int startingPointOfFileHeader;

	public ImageNtHeaders(ByteBuffer bb) throws IOException {
		this(bb, PE.ADDR_E_LFANEW);
	}

	public ImageNtHeaders(ByteBuffer bb, int position) throws IOException {
		this.bb = bb;
		bb.order(ByteOrder.nativeOrder());
		checkSignature(position);
		startingPointOfFileHeader = bb.position();
	}

	private void checkSignature(int position) throws IOException {
		bb.position(position);
		int addrNtHeader = bb.getInt();

		bb.order(ByteOrder.BIG_ENDIAN);
		bb.position(addrNtHeader);
		int ntSignature = bb.getInt();

		if (ntSignature != PE.NT_HEADER_SIGNATURE) {
			throw new IOException("PE Header is not founded...");
		}
	}

	public ImageFileHeader getFileHeader() {
		if (fileHeader == null) {
			fileHeader = new ImageFileHeader(bb, startingPointOfFileHeader);
		}
		return fileHeader;
	}

	public ImageOptionalHeader getOptionalHeader() throws IOException {
		if (optionalHeader == null) {
			int position = getFileHeader().getStartingPointOfOptionalHeader();
			optionalHeader = new ImageOptionalHeader(bb, position);
		}
		return optionalHeader;
	}
}
