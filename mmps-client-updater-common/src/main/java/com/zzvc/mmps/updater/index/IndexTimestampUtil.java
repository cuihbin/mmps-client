package com.zzvc.mmps.updater.index;

public class IndexTimestampUtil {
	private static final long MILLISECONDS_OF_SECOND = 1000;
	private static final long SECONDS_OF_MINUTE = 60;
	private static final long MINUTES_OF_HOUR = 60;
	
	private static final long SECONDS_OF_HOUR = SECONDS_OF_MINUTE * MINUTES_OF_HOUR;

	public static String createTimestamp(long time) {
		return String.valueOf(time / MILLISECONDS_OF_SECOND);
	}
	
	public static boolean checkTimestamp(String timestamp, long time) {
		long t1 = Long.parseLong(timestamp);
		long t2 = time / MILLISECONDS_OF_SECOND;
		
		// If time stamps are exactly identical
		if (t1 == t2) {
			return true;
		}
		
		// Or identical to minute
		if (t1 / SECONDS_OF_MINUTE == t2 / SECONDS_OF_MINUTE) {
			return true;
		}
		
		// Or identical to hour
		if (t1 / SECONDS_OF_HOUR == t2 / SECONDS_OF_HOUR) {
			return true;
		}
		
		// Or have same minute and seconds but different hour no more than 24 hours in case of different time zone
		if (Math.abs(t2 - t1) % SECONDS_OF_HOUR == 0 && Math.abs(t2 - t1) / SECONDS_OF_HOUR < 24) {
			return true;
		}
		
		// Failed the check otherwise
		return false;
	}
}
