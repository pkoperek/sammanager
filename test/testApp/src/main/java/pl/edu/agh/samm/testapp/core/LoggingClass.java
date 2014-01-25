package pl.edu.agh.samm.testapp.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoggingClass {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS");

	protected static void logMessage(String message) {
		logMessage("INFO", message);
	}

	protected static void logMessage(String prefix, String message) {
		StackTraceElement[] elements = new Throwable().getStackTrace();
		StackTraceElement caller = elements[2];
		Date date = Calendar.getInstance().getTime();
		String dateString = sdf.format(date);
		System.out.println(dateString + "| " + caller.getClassName() + "."
				+ caller.getMethodName() + "():" + caller.getLineNumber() + " "
				+ prefix + " " + message);
	}
}
