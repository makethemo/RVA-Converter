package com.makethemo.reversing.rvaconverter;

import java.io.IOException;
import java.util.List;

import com.makethemo.reversing.rvaconverter.header.ImageFileHeader;
import com.makethemo.reversing.rvaconverter.header.ImageNtHeaders;
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

		PE pe = new PE(filePath);
		ImageNtHeaders ntHeaders = pe.getNtHeaders();
		ImageFileHeader fileHeader = ntHeaders.getFileHeader();
		long imageBase = ntHeaders.getOptionalHeader().getImageBase();

		List<SectionHeader> list = pe.getSectionHeaders();

		String lineSeparator = System.lineSeparator();
		StringBuilder builder = new StringBuilder();
		builder.append(filePath).append(lineSeparator).append(lineSeparator);

		builder.append("Machine:\t\t 0x").append(Integer.toHexString(fileHeader.getMachine())).append(lineSeparator);
		builder.append("The number of sections:\t ").append(list.size()).append(lineSeparator);
		builder.append("Imagebase:\t\t 0x").append(Long.toHexString(imageBase)).append(lineSeparator);

		builder.append(lineSeparator).append("<Sections>").append(lineSeparator);
		for (SectionHeader header : list) {
			builder.append(header.getName()).append('\t');
			builder.append("0x").append(Long.toHexString(header.getVirtualAddress())).append('\t');
			builder.append("0x").append(Long.toHexString(header.getPointerToRawData()));
			builder.append(lineSeparator);
		}
		builder.append(lineSeparator);

		if (type.equalsIgnoreCase(TYPE_RVA_TO_RAW) && offset > imageBase) {
			offset -= imageBase;
		}

		long virtualAddress = 0;
		long pointerToRawData = 0;
		String sectionName = "";

		if (type.equalsIgnoreCase(TYPE_RVA_TO_RAW)) {

			for (int i = list.size() - 1; i >= 0; i--) {

				if (offset < list.get(i).getVirtualAddress()) {
					continue;
				}

				sectionName = list.get(i).getName();
				virtualAddress = list.get(i).getVirtualAddress();
				pointerToRawData = list.get(i).getPointerToRawData();
				break;
			}
			builder.append("Raw offset is 0x");
			builder.append(Long.toHexString(rvaToRaw(offset, virtualAddress, pointerToRawData)));
			builder.append(" in '").append(sectionName).append("' section.");
			builder.append(lineSeparator);
		} else {

			for (int i = list.size() - 1; i >= 0; i--) {

				if (offset < list.get(i).getPointerToRawData()) {
					continue;
				}

				sectionName = list.get(i).getName();
				virtualAddress = list.get(i).getVirtualAddress();
				pointerToRawData = list.get(i).getPointerToRawData();
				break;
			}
			builder.append("RVA is 0x");
			builder.append(Long.toHexString(rawToRva(offset, virtualAddress, pointerToRawData)));
			builder.append(" in '").append(sectionName).append("' section.");
			builder.append(lineSeparator);
		}
		System.out.println(builder.toString());
		pe.close();
	}

	public static long rvaToRaw(long rva, long virtualAddress, long pointerToRawData) {
		return rva - virtualAddress + pointerToRawData;
	}

	public static long rawToRva(long raw, long virtualAddress, long pointerToRawData) {
		return raw - pointerToRawData + virtualAddress;
	}
}
