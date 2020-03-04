package com.ezsyncxz.efficiency.constants;

public class Constant {
	/**
	 * trunck块大小，byte
	 */
	public static final int TRUNCK_SIZE = 512;

	/**
	 * 小于1<<16 (65536) 的最小素数
	 */
	public static final int MOD_ADLER = 65521;

	public static final int SERVER_CMD_WRITE_DATA = 102;

	public static final int SERVER_CMD_GET_CHECKSUM = 103;
}