package fr.aboucorp.variantchess.app.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtil {

    private static final int CALL_STACK_INDEX = 2;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    public static String prependCallLocation(String message) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        String clazz = extractClassName(stackTrace[CALL_STACK_INDEX]);
        int lineNumber = stackTrace[CALL_STACK_INDEX].getLineNumber();
        message = "" + clazz + ".java:" + lineNumber + " - " + message;
        return message;
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g., {@code Foo$1}
     * becomes {@code Foo}).
     */
    private static String extractClassName(StackTraceElement element) {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    public static void d(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.d(tag, newMessage);
    }

    public static void w(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.w(tag, newMessage);
    }

    public static void e(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.e(tag, newMessage);
    }

    public static void v(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.v(tag, newMessage);
    }

    public static void i(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.i(tag, newMessage);
    }

    public static void wtf(String tag, String message) {
        String newMessage = LogUtil.prependCallLocation(message);
        Log.wtf(tag, newMessage);
    }
}