package k.logix.xml;

import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import k.logix.util.Safe;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SNodeInfo {

	public static final int ATTRIBUTES_TOTAL = 8;
	public static final int BUTTON = 0x01;
	public static final int SCREEN = 0x02;
	public static final int BUILD_AREA = 0x03;
	public static final int TITLE = 0x04;
	public static final int UNKNOWN = 0xDEADBEEF;

	private static HashMap<String, Integer> nameToType = new HashMap<String, Integer>();
	private static List<SNodeInfo> infos = new ArrayList<SNodeInfo>();

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
	public Object boundings;

	public static SNodeInfo read(Element node) {
		SNodeInfo ni = new SNodeInfo();
		String[] atts = getAttVals(node);
		ni.base = node;
		ni.type = getType(node.getNodeName());
		ni.width = Safe.intCast(atts[1]);
		ni.height = Safe.intCast(atts[2]);
		ni.x = Safe.intCast(atts[3]);
		ni.y = Safe.intCast(atts[4]);
		ni.text = node.getTextContent();
		ni.name = atts[6];
		int w = Toolkit.getDefaultToolkit().getScreenSize().width;
		int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		if (atts[5].indexOf('h') != -1) {
			ni.x = (w / 2) - (ni.width / 2);
		}
		if (atts[5].indexOf('v') != -1) {
			ni.y = (h / 2) - (ni.height / 2);
		}
		if (atts[7].equals("true")) {
			ni.helperNode(node);
		}
		infos.add(ni);
		return ni;
	}

	private void helperNode(Element node) {
		SNodeInfo info = getInfoByElement(node.getParentNode());
		try {
			info.boundings = readBoundingData(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("A fatal error has occured while loading "
					+ info.name + ".");
			System.exit(1);
		}
	}

	@SuppressWarnings("rawtypes")
	private Object readBoundingData(SNodeInfo info) throws Exception {
		Element e = info.base;
		String bcls = e.getAttribute("package") + "." + e.getNodeName();
		Class boundingc = Class.forName(bcls);
		Object inst = boundingc.newInstance();
		Method[] mlist = boundingc.getDeclaredMethods();
		NamedNodeMap map = e.getAttributes();
		NamedNodeMapIter iter = new NamedNodeMapIter(map);
		for (Node n : iter) {
			Attr a = (Attr) n;
			Field f = boundingc.getDeclaredField(a.getName());
			if (f == null) {
				System.err.println("Warning: unknown field for " + n);
			} else {
				f.set(inst, transform(a.getValue(), f));
			}
		}
		for (Method m : mlist) {
			m.setAccessible(true);
			Node n = e.getAttributes().getNamedItem(m.getName());
			if (n != null) {
				m.invoke(inst, ((Object[]) n.getNodeValue().split(":")));
			}
		}
		return inst;
	}

	private Object transform(String attribute, Field f) {
		Class<?> type = f.getType();
		return type.cast(attribute);
	}

	private static SNodeInfo getInfoByElement(Node parentNode) {
		for (SNodeInfo i : infos) {
			if (i.base == parentNode) {
				return i;
			}
		}
		return null;
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
		vals[0] = node.getAttribute("id").toLowerCase();
		vals[1] = node.getAttribute("width").toLowerCase();
		vals[2] = node.getAttribute("height").toLowerCase();
		vals[3] = node.getAttribute("x").toLowerCase();
		vals[4] = node.getAttribute("y").toLowerCase();
		vals[5] = node.getAttribute("center").toLowerCase();
		vals[6] = node.getAttribute("name").toLowerCase();
		vals[7] = node.getAttribute("helper").toLowerCase();
		return vals;
	}

	private void init() {
		nameToType.put("button", BUTTON);
		nameToType.put("screen", SCREEN);
		nameToType.put("buildarea", BUILD_AREA);
		nameToType.put("title", TITLE);
	}

	{
		init();
	}

}
