package k.logix.main;

import java.awt.Component;

import javax.swing.JFrame;

import k.logix.util.Convert;
import k.logix.util.ScreenUtil;
import k.logix.xml.SNodeInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScreenLoader {
	private static JFrame workingFrame = null;
	private static Document dom = null;

	public static void loadScreenByID(String id) {
		workingFrame = LogixMain.screen; 
		dom = ScreenUtil.getDOMByID(id);
		recursiveCheck((Node) (dom));
		workingFrame.repaint();
	}

	private static void recursiveCheck(Node node) {
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			SNodeInfo info = SNodeInfo.read(e);
			if (info == null || info.type == SNodeInfo.UNKNOWN) {
				return;
			}
			setupNode(info);
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node n = e.getChildNodes().item(i);
				recursiveCheck(n);
			}
		}
	}

	private static void setupNode(SNodeInfo info) {
		Component c = Convert.infoToComp(info);
		workingFrame.add(c);
		position(c, info);
	}

	private static void position(Component c, SNodeInfo info) {
		c.setBounds(info.x, info.y, info.width, info.height);
	}
}
