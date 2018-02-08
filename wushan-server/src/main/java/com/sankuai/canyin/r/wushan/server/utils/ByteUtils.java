package com.sankuai.canyin.r.wushan.server.utils;

public final class ByteUtils {

	public static long getLong(byte[] buf , int start) {
		if(buf.length - start < 8){
			throw new IllegalArgumentException(" buf.length < 8 ");
		}
		long res = 0;
		res = ((res << 8) | (buf[start] & 0xff));
		res = ((res << 8) | (buf[start + 1] & 0xff));
		res = ((res << 8) | (buf[start + 2] & 0xff));
		res = ((res << 8) | (buf[start + 3] & 0xff));
		res = ((res << 8) | (buf[start + 4] & 0xff));
		res = ((res << 8) | (buf[start + 5] & 0xff));
		res = ((res << 8) | (buf[start + 6] & 0xff));
		res = ((res << 8) | (buf[start + 7] & 0xff));
		return res;
	}

	public static void putInt(byte[] buf , int value , int start) {
		buf[start] = (byte) ((value >> 24) & 0xFF);
		buf[start + 1] = (byte) ((value >> 16) & 0xFF);
		buf[start + 2] = (byte) ((value >> 8) & 0xFF);
		buf[start + 3] = (byte) (value & 0xFF);
		start += 4;
	}

	public static void putLong(byte[] buf , long value , int start) {
		if(buf.length < 8){
			throw new IllegalArgumentException(" buf.length < 8 ");
		}
		buf[start] = (byte) ((value >> 56) & 0xFF);
		buf[start + 1] = (byte) ((value >> 48) & 0xFF);
		buf[start + 2] = (byte) ((value >> 40) & 0xFF);
		buf[start + 3] = (byte) ((value >> 32) & 0xFF);
		buf[start + 4] = (byte) ((value >> 24) & 0xFF);
		buf[start + 5] = (byte) ((value >> 16) & 0xFF);
		buf[start + 6] = (byte) ((value >> 8) & 0xFF);
		buf[start + 7] = (byte) (value & 0xFF);
	}
	
}
