package com.nvarghese.funtoo.utils;

import java.nio.ByteBuffer;
import java.util.Random;

public class ByteUtils {

	private static String digits = "0123456789abcdef";

	/**
	 * Return length many bytes of the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @param length
	 *            the number of bytes in the data block to be converted.
	 * @return a hex representation of length bytes of data.
	 */
	public static String toHex(byte[] data, int length) {

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i != length; i++) {
			int v = data[i] & 0xff;

			buf.append(digits.charAt(v >> 4)); // 4-7 bits
			buf.append(digits.charAt(v & 0x0f)); // 0-3 bits
		}

		return buf.toString();
	}

	/**
	 * Return the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @return a hex representation of data.
	 */
	public static String toHex(byte[] data) {

		return toHex(data, data.length);
	}
	
	public static byte[] toBytes(String hexData) {
		
		byte [] bytes = new byte[hexData.length() / 2];
		for(int i = 0; i < hexData.length(); i+=2) {
			bytes[i / 2] = (byte) ((Character.digit(hexData.charAt(i), 16) << 4)
                    + Character.digit(hexData.charAt(i+1), 16));
		}
		return bytes;		
	}

	/**
	 * Returns the xor'd values of input bytes
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	public static byte[] xorBytes(byte[] bytes1, byte[] bytes2) {

		int maxlength = (bytes1.length > bytes2.length) ? bytes1.length : bytes2.length;

		ByteBuffer buffer = ByteBuffer.allocate(maxlength);

		for (int i = 0; i < maxlength; i++) {

			if (bytes1.length == i) {
				buffer.put(bytes2, i, bytes2.length - i);
				break;
			} else if (bytes2.length == i) {
				buffer.put(bytes1, i, bytes1.length - i);
				break;
			} else {
				buffer.put((byte) (bytes1[i] ^ bytes2[i]));
			}

		}
		return buffer.array();
	}

	/**
	 * Returns the AND'd value of input bytes
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	public static byte[] andBytes(byte[] bytes1, byte[] bytes2) {

		int maxlength = (bytes1.length > bytes2.length) ? bytes1.length : bytes2.length;

		ByteBuffer buffer = ByteBuffer.allocate(maxlength);

		for (int i = 0; i < maxlength; i++) {

			if (bytes1.length == i) {
				buffer.put(bytes2, i, bytes2.length - i);
				break;
			} else if (bytes2.length == i) {
				buffer.put(bytes1, i, bytes1.length - i);
				break;
			} else {
				buffer.put((byte) (bytes1[i] & bytes2[i]));
			}

		}
		return buffer.array();
	}

	/**
	 * Returns the OR'd value of input bytes
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	public static byte[] orBytes(byte[] bytes1, byte[] bytes2) {

		int maxlength = (bytes1.length > bytes2.length) ? bytes1.length : bytes2.length;

		ByteBuffer buffer = ByteBuffer.allocate(maxlength);

		for (int i = 0; i < maxlength; i++) {

			if (bytes1.length == i) {
				buffer.put(bytes2, i, bytes2.length - i);
				break;
			} else if (bytes2.length == i) {
				buffer.put(bytes1, i, bytes1.length - i);
				break;
			} else {
				buffer.put((byte) (bytes1[i] | bytes2[i]));
			}

		}
		return buffer.array();
	}

	/**
	 * Gets random bytes
	 * @param lengthOfArray
	 * @return
	 */
	public static byte[] getRandomBytes(int lengthOfArray) {

		Random random = new Random();

		byte[] bytes = new byte[lengthOfArray];
		for (int i = 0; i < lengthOfArray; i++) {
			bytes[i] = (byte) random.nextInt(256);
		}

		return bytes;

	}

}
