package com.makethemo.reversing.rvaconverter.header;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class PE {
	
	public static final int DOS_HEADER_SIGNATURE = 0x4d5a;
	public static final int NT_HEADER_SIGNATURE = 0x50450000;

	public static final int IMAGE_FILE_MACHINE_I386 = 0x014c;
	public static final int IMAGE_FILE_MACHINE_IA64 = 0x0200;
	public static final int IMAGE_FILE_MACHINE_AMD64 = 0x8664;

	public static final int IMAGE_OPTIONAL_HEADER32 = 0x10B;
	public static final int IMAGE_OPTIONAL_HEADER64 = 0x20B;

	public static final int SIZE_OF_SECTION_HEADER = 0x28;

	static final int ADDR_E_LFANEW = 0x3c;

	private BufferedInputStream in;
	private byte[] buf;
	private ByteBuffer bb;
	
	private volatile ImageNtHeaders ntHeaders;
	private volatile List<SectionHeader> sectionHeaders;

	public PE(String filePath) throws IOException {
		this(filePath, 1024);
	}

	public PE(File file) throws IOException {
		this(file, 1024);
	}

	public PE(String filePath, int size) throws IOException {
		in = new BufferedInputStream(new FileInputStream(filePath));
		checkSignature(size);

	}

	public PE(File file, int size) throws IOException {
		in = new BufferedInputStream(new FileInputStream(file));
		checkSignature(size);
	}

	private void checkSignature(int size) throws IOException {
		buf = new byte[size];
		in.read(buf);

		bb = ByteBuffer.wrap(buf);
		bb.order(ByteOrder.BIG_ENDIAN);
		int dosSignature = bb.getChar();

		if (dosSignature != DOS_HEADER_SIGNATURE) {
			in.close();
			throw new IOException("This file is not a PE format...");
		}
	}
	
	public ImageNtHeaders getNtHeaders() throws IOException {
		if (ntHeaders == null) {
			ntHeaders = new ImageNtHeaders(bb);
		}
		return ntHeaders;
	}
	
	public List<SectionHeader> getSectionHeaders() throws IOException {
		if (sectionHeaders == null) {
			ImageFileHeader fileHeader = getNtHeaders().getFileHeader();
			int startingPointOfOptionalHeader = fileHeader.getStartingPointOfOptionalHeader();
			int sizeOfOptionalHeader = fileHeader.getSizeOfOptionalHeader();
			
			int startingPointOfSectionHeader = startingPointOfOptionalHeader + sizeOfOptionalHeader;
			int numberOfSections = fileHeader.getNumberOfSections();
			
			SectionHeader[] sectionHeaders = new SectionHeader[numberOfSections];
			StringBuilder sb = new StringBuilder(8);
			byte oneByteChar = 1;

			long virtualAddress = 0;
			long pointerToRawData = 0;

			for (int i = 0; i < numberOfSections; i++) {
				int startingPoint = startingPointOfSectionHeader + SIZE_OF_SECTION_HEADER * i;
				bb.position(startingPoint);

				while ((oneByteChar = bb.get()) != '\0') {
					sb.append((char) oneByteChar);
				}
				String name = sb.toString();
				System.out.println(name);
				sb.setLength(0);
				oneByteChar = 1;

				bb.position(startingPoint + 12);
				virtualAddress = bb.getInt();

				bb.position(startingPoint + 0x14);
				pointerToRawData = bb.getInt();

				sectionHeaders[i] = new SectionHeader(name, virtualAddress, pointerToRawData);
			}
			
			this.sectionHeaders = Arrays.asList(sectionHeaders);
		}
		return sectionHeaders;
	}
	
	public void close() {
		try {
			in.close();
		} catch (Exception e) {
			// do nothing
		}
	}

}
