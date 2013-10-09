package com.chalcodes.weaponm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple BugSense client.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class BugSense {
	private static final Logger log = LoggerFactory.getLogger(BugSense.class.getSimpleName());
	private static final URL REPORT_URL;
	private static final String API_KEY = "c92fbf2f"; // TODO use real key
	
	static {
		URL tmp = null;
		try {
			tmp = new URL("http://www.bugsense.com/api/errors");
		} catch (MalformedURLException e) {
			// IMPOSSIBRU!!!
			log.error("Hard-coded URL is malformed!", e);
		}
		REPORT_URL = tmp;
	}
	
	public static void report(Throwable t, boolean handled) throws IOException {
		report(Thread.currentThread(), t, handled);
	}
	
	public static void report(Thread thread, Throwable t, boolean handled) throws IOException {
		System.out.println(spitJson(thread, t, handled));
		
		HttpURLConnection con = (HttpURLConnection) REPORT_URL.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("X-BugSense-Api-Key", API_KEY);
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(spitJson(thread, t, handled));
		out.flush();
		out.close();
		
		int responseCode = con.getResponseCode();
		BufferedReader in;
		if(responseCode == 200) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		}
		else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String line;
		StringBuilder response = new StringBuilder();
 
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();

		log.debug(response.toString());
	}
	
	private static String spitJson(Thread thread, Throwable t, boolean handled) {
		StringBuilder sb = new StringBuilder();
		sb.append("data=");
		sb.append("{\"client\":{\"name\":\"chalcodes\",\"version\":\"3.5\"},\"request\":{\"custom_data\":{\"Java Runtime\":\"");
		sb.append(System.getProperty("java.runtime.name"));
		sb.append(' ');
		sb.append(System.getProperty("java.runtime.version"));
		sb.append("\",\"Processors\":\"");
		sb.append(Integer.toString(Runtime.getRuntime().availableProcessors()));
		sb.append("\",\"Thread\":\"");
		sb.append(thread.getName());
		sb.append("\"}},\"exception\":{\"handled\":");
		sb.append(handled ? '0' : '1');
		sb.append(",\"message\":\"");
		sb.append(t.getMessage());
		sb.append("\",\"where\":\"");
		StackTraceElement[] elements = t.getStackTrace();
		sb.append(elements[0].getFileName());
		sb.append(':');
		sb.append(elements[0].getLineNumber());
		sb.append("\",\"klass\":\"");
		sb.append(elements[0].getClassName());
		sb.append("\",\"backtrace\":\"");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String trace = sw.toString();
		trace = trace.replace("\n", "\\r\\n");
		trace = trace.replace("\t", "");
		if(trace.endsWith("\\r\\n")) {
			trace = trace.substring(0, trace.length() - 4);
		}
		sb.append(trace);
		sb.append("\",\"breadcrumbs\":\"\"},\"application_environment\":{\"phone\":\"");
		sb.append(System.getProperty("os.name"));
		sb.append("\",\"appver\":\"");
		sb.append(Version.VERSION);
		sb.append("\",\"appname\":\"");
		sb.append("com.chalcodes.weaponm");
		sb.append("\",\"osver\":\"");
		sb.append(System.getProperty("os.version"));
		sb.append("\",\"uid\":\"");
		sb.append(AppSettings.getInstallUuid());
		sb.append("\"}}");		
		
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		try {
//			BugSense.report(new RuntimeException("Test"), true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
