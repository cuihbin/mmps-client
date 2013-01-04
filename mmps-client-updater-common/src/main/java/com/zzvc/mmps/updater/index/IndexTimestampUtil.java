package com.zzvc.mmps.updater.index;

public class IndexTimestampUtil {
	private static final long MILLISECONDS_OF_SECOND = 1000;
	private static final long SECONDS_OF_MINUTE = 60;

	public static String createTimestamp(long time) {
		return String.valueOf(time / MILLISECONDS_OF_SECOND);
	}
	
	public static boolean checkTimestamp(String timestamp, long time) {
		long t1 = Long.parseLong(timestamp);
		long t2 = time / MILLISECONDS_OF_SECOND;
		if (t2 % SECONDS_OF_MINUTE == 0) {
			t1 /= SECONDS_OF_MINUTE;
			t2 /= SECONDS_OF_MINUTE;
		}
		return t1 == t2;
	}
}
