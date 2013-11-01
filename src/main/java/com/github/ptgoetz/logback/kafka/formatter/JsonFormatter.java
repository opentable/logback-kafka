package com.github.ptgoetz.logback.kafka.formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

public class JsonFormatter implements Formatter {
    private String hostname;

	public JsonFormatter() {
		this.hostname = "";
		try {
			this.hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
	}

    public String format(ILoggingEvent event) {
		JSONObject obj = new JSONObject();
		obj.put("level", event.getLevel().levelStr);
        obj.put("logger", event.getLoggerName());
        obj.put("timestamp", event.getTimeStamp());
        obj.put("hostname", this.hostname);
        obj.put("message", event.getFormattedMessage());
		addExceptionsIf(obj, "exceptions", event.getThrowableProxy());
        return obj.toString();
    }

	private void addExceptionsIf(JSONObject obj, String name, IThrowableProxy proxy) {
		if (proxy != null) {
			obj.put(name, addExceptions(proxy));
		}
	}

	private JSONObject addExceptions(IThrowableProxy proxy) {
		JSONObject obj = new JSONObject();
		obj.put("exceptionClass", proxy.getClassName());
		obj.put("exceptionMessage", proxy.getMessage());

		StackTraceElementProxy[] stackTrace = proxy.getStackTraceElementProxyArray();
		if (stackTrace != null) {
			JSONArray list = new JSONArray();
			for (StackTraceElementProxy trace : stackTrace) {
				list.add(trace.toString());
			}
			obj.put("stacktrace", list);
		}

		addExceptionsIf(obj, "cause", proxy.getCause());

		IThrowableProxy[] suppressed = proxy.getSuppressed();
		if (suppressed != null) {
			JSONArray list = new JSONArray();
			for (IThrowableProxy throwable : suppressed) {
				if (throwable != null) {
					list.add(addExceptions(throwable));
				}
			}
			if (!list.isEmpty()) {
				obj.put("suppressed", list);
			}
		}

		return obj;
	}
}
