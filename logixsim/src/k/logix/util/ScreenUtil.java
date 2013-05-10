package k.logix.util;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilderFactory;

import k.logix.main.LogixMain;

import org.w3c.dom.Document;

public class ScreenUtil {
	private static HashMap<String, Document> idToDOM = new HashMap<String, Document>();
	private static boolean init;
	public static boolean currentlyInit;

	public static void center(JFrame frame) {
		frame.setLocationRelativeTo(null);
	}

	private static void init() {
		if (init)
			return;
		currentlyInit = true;
		init = true;
		for(File f : new File(LogixMain.TOP_LEVEL, "/screen/").listFiles()) {
			try {
				Document d = loadDocument(f);
				idToDOM.put(getID(d), d);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		currentlyInit = false;
	}

	private static String getID(Document d) {
		String id = d.getElementById("screen").getAttribute("id");
		return id;
	}

	public static Document loadDocument(File dom) throws Exception {
		init();
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(dom);
		doc.normalizeDocument();
		return doc;
	}

	public static Document getDOMByID(String id) {
		init();
		return idToDOM.get(id);
	}

}
