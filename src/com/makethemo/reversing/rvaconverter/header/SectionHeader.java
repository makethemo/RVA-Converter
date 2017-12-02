package com.makethemo.reversing.rvaconverter.header;

public class SectionHeader {

	private String name;
	private long virtualAddress;
	private long pointerToRawData;

	public SectionHeader(String name, long virtualAddress, long pointerToRawData) {
		super();
		this.name = name;
		this.virtualAddress = virtualAddress;
		this.pointerToRawData = pointerToRawData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getVirtualAddress() {
		return virtualAddress;
	}

	public void setVirtualAddress(long virtualAddress) {
		this.virtualAddress = virtualAddress;
	}

	public long getPointerToRawData() {
		return pointerToRawData;
	}

	public void setPointerToRawData(long pointerToRawData) {
		this.pointerToRawData = pointerToRawData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (pointerToRawData ^ (pointerToRawData >>> 32));
		result = prime * result + (int) (virtualAddress ^ (virtualAddress >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SectionHeader other = (SectionHeader) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pointerToRawData != other.pointerToRawData)
			return false;
		if (virtualAddress != other.virtualAddress)
			return false;
		return true;
	}
}
