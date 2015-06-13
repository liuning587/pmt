package utilTest;

import java.util.Arrays;

public class M9 {
	public static void main(String[] args) {
		System.out.println(String.valueOf(bytesToChr(new byte[]{0x6c,0x6d,39,31,31})));
	}
	
	
	public static char[] bytesToChr(final byte[] data) {
		int length = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0x00) {
				length += 1;
			}
		}

		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) data[i];
		}

		return chars;
	}
}
