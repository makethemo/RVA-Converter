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
	
	private static final int SIZE_OF_SECTION_HEADER = 0x1c;
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
				System.out.println("Number of sections: " + String.valueOf(numberOfSections));
				
			} else {
				System.out.println("PE Header is not founded...");
			}
			

		} else {
			System.out.println("This file's header is not PE format...");
		}
	}

}
