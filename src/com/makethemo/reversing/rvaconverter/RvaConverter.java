package com.makethemo.reversing.rvaconverter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RvaConverter {

	private static final String TYPE_RAW_TO_RVA = "raw";
	private static final String TYPE_RVA_TO_RAW = "rva";

	private static final int DOS_HEADER_SIGNATURE = 0x4d5a;
	private static final int NT_HEADER_SIGNATURE = 0x50450000;

	private static final int IMAGE_FILE_MACHINE_I386 = 0x014c;
	private static final int IMAGE_FILE_MACHINE_IA64 = 0x0200;
	private static final int IMAGE_FILE_MACHINE_AMD64 = 0x8664;

	private static final int IMAGE_OPTIONAL_HEADER32 = 0x10B;
	private static final int IMAGE_OPTIONAL_HEADER64 = 0x20B;

	private static final int SIZE_OF_SECTION_HEADER = 0x28;
	private static final int SIZE_OF_NT_HEADER = 0xf8;

	private static final int ADDR_E_LFANEW = 0x3c;

	public static void main(String[] args) throws IOException {
		System.out.println(args[0]);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(args[0]));
		byte[] buf = new byte[1024];
		in.read(buf);

		ByteBuffer bb = ByteBuffer.wrap(buf);
		bb.order(ByteOrder.BIG_ENDIAN);
		int dosSignature = bb.getChar();

		if (dosSignature == DOS_HEADER_SIGNATURE) {
			bb.order(ByteOrder.nativeOrder());
			bb.position(ADDR_E_LFANEW);
			int addrNtHeader = bb.getInt();

			bb.order(ByteOrder.BIG_ENDIAN);
			bb.position(addrNtHeader);
			int ntSignature = bb.getInt();

			if (ntSignature == NT_HEADER_SIGNATURE) {
				System.out.println("PE Header!!!");

				bb.order(ByteOrder.nativeOrder());
				int machine = bb.getChar();
				System.out.println(Integer.toHexString(machine));

				int numberOfSections = bb.getChar();
				System.out.println("The number of sections: " + String.valueOf(numberOfSections));

				bb.position(bb.position() + 12);
				int sizeOfOptionalHeader = bb.getChar();
				System.out.println("Size of optional header: " + String.valueOf(sizeOfOptionalHeader));

				int startingPointOfOptionalHeader = bb.position() + 2;

				bb.position(startingPointOfOptionalHeader);
				int optionalHeaderSignature = bb.getChar();

				long imageBase;

				if (optionalHeaderSignature == IMAGE_OPTIONAL_HEADER32) {
					bb.position(bb.position() + 26);
					imageBase = bb.getInt();

				} else if (optionalHeaderSignature == IMAGE_OPTIONAL_HEADER64) {
					bb.position(bb.position() + 22);
					imageBase = bb.getLong();

				} else {
					System.out.println("Unknown optional header...");
					return;
				}

				System.out.println(Long.toHexString(imageBase));

				int startingPointOfSectionHeader = startingPointOfOptionalHeader + sizeOfOptionalHeader;

				StringBuilder sb = new StringBuilder(8);
				byte oneByteChar = 1;

				for (int i = 0; i < numberOfSections; i++) {
					bb.position(startingPointOfSectionHeader + SIZE_OF_SECTION_HEADER * i);
					while ((oneByteChar = bb.get()) != '\0') {
						sb.append((char) oneByteChar);
					}
					System.out.println(sb.toString());
					sb.setLength(0);
					oneByteChar = 1;
				}

			} else {
				System.out.println("PE Header is not founded...");
				return;
			}

		} else {
			System.out.println("This file's header is not PE format...");
		}
	}

}
