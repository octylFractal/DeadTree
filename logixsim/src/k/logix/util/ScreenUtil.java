package k.logix.util;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilderFactory;

import k.logix.main.LogixMain;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScreenUtil {
	private static HashMap<String, Document> idToDOM = new HashMap<String, Document>();
	private static String sep = File.separator;
	private static File screens = new File(
			LogixMain.TOP_LEVEL.getAbsolutePath() + sep + "screens" + sep);
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
		System.out.println("Path: "
				+ screens.getAbsolutePath()
				+ " "
				+ (screens.isDirectory() ? "is a directory"
						: "is not a directory"));
		for (File f : screens.listFiles()) {
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
	
	static {
		init();
	}

	private static String getID(Document d) {
		String id = ((Element) d.getElementsByTagName("screen").item(0))
				.getAttribute("id");
		return id;
	}

	public static Document loadDocument(File dom) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(dom);
		doc.normalizeDocument();
		return doc;
	}

	public static Document getDOMByID(String id) {
		return idToDOM.get(id);
	}

}
