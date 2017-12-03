package com.makethemo.reversing.rvaconverter;

import java.io.IOException;
import java.util.List;

import com.makethemo.reversing.rvaconverter.header.PE;
import com.makethemo.reversing.rvaconverter.header.SectionHeader;

public class RvaConverter {

	private static final String TYPE_RAW_TO_RVA = "raw";
	private static final String TYPE_RVA_TO_RAW = "rva";

	public static void main(String[] args) throws IOException {
		String filePath = args[0];
		String type = args[1];
		long offset = Long.parseLong(args[2], 16);

		if (!(type.equalsIgnoreCase(TYPE_RVA_TO_RAW) || type.equalsIgnoreCase(TYPE_RAW_TO_RVA))) {
			System.out.println("Second argument must be 'raw' or 'rva'...");
			return;
		}

		System.out.println(filePath);

		PE pe = new PE(filePath);
		long imageBase = pe.getNtHeaders().getOptionalHeader().getImageBase();

		List<SectionHeader> list = pe.getSectionHeaders();

		if (type.equalsIgnoreCase(TYPE_RVA_TO_RAW) && offset > imageBase) {
			offset -= imageBase;
		}

		long virtualAddress = 0;
		long pointerToRawData = 0;

		if (type.equalsIgnoreCase(TYPE_RVA_TO_RAW)) {

			for (int i = list.size() - 1; i >= 0; i--) {

				if (offset < list.get(i).getVirtualAddress()) {
					continue;
				}

				virtualAddress = list.get(i).getVirtualAddress();
				pointerToRawData = list.get(i).getPointerToRawData();
				break;
			}
			System.out.println("Raw offset is " + Long.toHexString(rvaToRaw(offset, virtualAddress, pointerToRawData)));
		} else {

			for (int i = list.size() - 1; i >= 0; i--) {

				if (offset < list.get(i).getPointerToRawData()) {
					continue;
				}

				virtualAddress = list.get(i).getVirtualAddress();
				pointerToRawData = list.get(i).getPointerToRawData();
				break;
			}
			System.out.println("RVA is " + Long.toHexString(rawToRva(offset, virtualAddress, pointerToRawData)));
		}

		pe.close();
	}

	public static long rvaToRaw(long rva, long virtualAddress, long pointerToRawData) {
		return rva - virtualAddress + pointerToRawData;
	}

	public static long rawToRva(long raw, long virtualAddress, long pointerToRawData) {
		return raw - pointerToRawData + virtualAddress;
	}
}
