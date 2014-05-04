package ch.defiant.purplesky.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class DebugUtility {

	/**
	 * Reads all lines from the input until <code>null</code> occurs, ignoring
	 * IOExceptions. This method may block.
	 * 
	 * @param is
	 *            The input stream to read from
	 * @return Content of all the lines
	 */
	public static String inputStreamToString(InputStream is) {
		if (is == null) {
			return "";
		}
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(is));
		StringBuilder result = new StringBuilder();
		String s = null;
		try {
			while ((s = bufferedReader.readLine()) != null) {
				result.append(s + "\n");
			}
		} catch (IOException e) {
			// Ignore, just return the rest.
		}
		return result.toString();
	}

	public static String documentToString(Document document, boolean indent) {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);

			if (indent) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			}

			transformer.transform(source, result);
			return sw.toString();
		} catch (TransformerConfigurationException e) {
			return "";
		} catch (TransformerException e) {
			return "";
		}

	}
}
