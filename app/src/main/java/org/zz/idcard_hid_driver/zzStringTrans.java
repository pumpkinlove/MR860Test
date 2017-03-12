package org.zz.idcard_hid_driver;

import java.util.Vector;

public class zzStringTrans {
	static public Vector<Byte> str2vectorbcd(String str) {
		byte[] a = str.getBytes();
		Vector<Byte> v = new Vector<Byte>();
		v.clear();
		for (int i = 0; i < str.length() / 2; i++) {
			byte b = (byte) (((a[2 * i] - 0x30) << 4) | (a[2 * i + 1] - 0x30));
			v.add(b);
		}
		return v;
	}

	static public byte[] str2bcd(String str) {
		byte[] a = str.getBytes();
		Vector<Byte> v = new Vector<Byte>();
		v.clear();
		for (int i = 0; i < str.length() / 2; i++) {
			byte b = (byte) (((a[2 * i] - 0x30) << 4) | (a[2 * i + 1] - 0x30));
			v.add(b);
		}
		byte[] tmpbt = new byte[v.size()];
		for (int j = 0; j < v.size(); j++) {
			tmpbt[j] = v.get(j);
		}
		return tmpbt;
	}

	static public Vector<Byte> datetime2vectorbcd(String time) {
		String[] a = time.split(" ");
		time = a[0] + a[1];
		a = time.split("[:-]");
		StringBuilder b = new StringBuilder();
		for (String s : a) {
			b.append(s);
		}
		time = b.toString().substring(2);
		return str2vectorbcd(time);
	}

	static public byte[] datetime2bcd(String time) {
		String[] a = time.split(" ");
		time = a[0] + a[1];
		a = time.split("[:-]");
		StringBuilder b = new StringBuilder();
		for (String s : a) {
			b.append(s);
		}
		time = b.toString().substring(2);
		return str2bcd(time);
	}

	static public byte[] time2bcd(String time) {

		return str2bcd(time);
	}

	static public String bcd2time(byte[] time) {
		int hour = (time[0] >> 4) * 10 + (time[0] & 0xf);
		int min = (time[1] >> 4) * 10 + (time[1] & 0xf);
		int sec = (time[2] >> 4) * 10 + (time[2] & 0xf);
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}

	static public String bcd2date(byte[] date) {
		int year = 2000 + 10 * (date[0] >> 4) + (date[0] & 0xf);
		int mon = 10 * (date[1] >> 4) + (date[1] & 0xf);
		int day = 10 * (date[2] >> 4) + (date[2] & 0xf);

		return String.format("%02d-%02d-%02d", year, mon, day);
	}

	static public String bcd2datetime(byte[] datetime) {
		int year = 2000 + 10 * (datetime[0] >> 4) + (datetime[0] & 0xf);
		int mon = 10 * (datetime[1] >> 4) + (datetime[1] & 0xf);
		int day = 10 * (datetime[2] >> 4) + (datetime[2] & 0xf);

		int hour = (datetime[3] >> 4) * 10 + (datetime[3] & 0xf);
		int min = (datetime[4] >> 4) * 10 + (datetime[4] & 0xf);
		int sec = (datetime[5] >> 4) * 10 + (datetime[5] & 0xf);

		return String.format("%02d-%02d-%02d %02d:%02d:%02d", year, mon, day,
				hour, min, sec);
	}

	static public String bcd2str(byte[] bcd) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bcd) {
			sb.append(String.format("%d%d", b >> 4, b & 0xf));
		}
		return sb.toString();
	}

	static public String hex2str(byte[] hex) {
		StringBuilder sb = new StringBuilder();
		for (byte b : hex) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}

	static public byte[] str2hex(String str) {
		byte[] a = str.getBytes();
		Vector<Byte> v = new Vector<Byte>();
		v.clear();
		for (int i = 0; i < str.length() / 2; i++) {
			byte b1;
			byte b2;
			if ('0' <= a[2 * i] && a[2 * i] <= '9') {
				b1 = (byte) (a[2 * i] - '0');
			} else if ('A' <= a[2 * i] && a[2 * i] <= 'F') {
				b1 = (byte) (a[2 * i] - 'A' + 10);
			} else {
				b1 = (byte) (a[2 * i] - 'a' + 10);
			}

			if ('0' <= a[2 * i + 1] && a[2 * i + 1] <= '9') {
				b2 = (byte) (a[2 * i + 1] - '0');
			} else if ('A' <= a[2 * i + 1] && a[2 * i + 1] <= 'F') {
				b2 = (byte) (a[2 * i + 1] - 'A' + 10);
			} else {
				b2 = (byte) (a[2 * i + 1] - 'a' + 10);
			}
			byte b = (byte) ((b1 << 4) | b2);
			v.add(b);
		}
		byte[] tmpbt = new byte[v.size()];
		for (int j = 0; j < v.size(); j++) {
			tmpbt[j] = v.get(j);
		}
		return tmpbt;
	}
}
