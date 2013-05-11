package k.logix.xml;

import java.awt.Toolkit;
import java.util.HashMap;

import k.logix.util.Safe;

import org.w3c.dom.Element;

public class SNodeInfo {

	public static final int ATTRIBUTES_TOTAL = 6;
	public static final int BUTTON = 0x01;
	public static final int SCREEN = 0x02;
	public static final int UNKNOWN = 0xDEADBEEF;

	private static final HashMap<String, Integer> nameToType = new HashMap<String, Integer>();

	public Element base = null;
	public int type = 0;
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	/* Titles of things */
	public String name = "";
	/* The stuff IN the component, maybe predefined text */
	public String text = "";

	public static SNodeInfo read(Element node) {
		SNodeInfo ni = new SNodeInfo();
		String[] atts = getAttVals(node);
		ni.base = node;
		ni.type = getType(node.getNodeName());
		ni.width = Safe.intCast(atts[1]);
		ni.height = Safe.intCast(atts[2]);
		ni.x = Safe.intCast(atts[3]);
		ni.y = Safe.intCast(atts[4]);
		int w = Toolkit.getDefaultToolkit().getScreenSize().width;
		int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		if (atts[5].indexOf('h') != -1) {
			ni.x = (w / 2) - (ni.width / 2);
		}
		if (atts[5].indexOf('v') != -1) {
			ni.y = (h / 2) - (ni.height / 2);
		}
		return ni;
	}

	private static int getType(String nodeName) {
		Integer val = nameToType.get(nodeName.toLowerCase());
		if (val != null) {
			return val;
		}
		return UNKNOWN;
	}

	/* Always returns SNodeInfo.ATTRIBUTES_TOTAL size array */
	private static String[] getAttVals(Element node) {
		String[] vals = new String[ATTRIBUTES_TOTAL];
		vals[0] = node.getAttribute("id");
		vals[1] = node.getAttribute("width");
		vals[2] = node.getAttribute("height");
		vals[3] = node.getAttribute("x");
		vals[4] = node.getAttribute("y");
		vals[5] = node.getAttribute("center");
		return vals;
	}

	private void init() {
		nameToType.put("button", BUTTON);
		nameToType.put("screen", SCREEN);
	}

	{
		init();
	}

}
