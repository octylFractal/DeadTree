package k.logix.main;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import k.logix.util.Convert;
import k.logix.util.ScreenUtil;
import k.logix.xml.SNodeInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScreenLoader {
	private static JFrame workingContainer = null;
	private static JPanel secondaryContainer = null;
	private static Document dom = null;

	public static void loadScreenByID(String id) {
		workingContainer = LogixMain.screen;
		workingContainer.setVisible(false);
		dom = ScreenUtil.getDOMByID(id);
		recursiveCheck((Node) (dom));
		workingContainer.repaint();
		workingContainer.setVisible(true);
	}

	private static void recursiveCheck(Node node) {
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			SNodeInfo info = SNodeInfo.read(e);
			if (info == null) {
				return;
			}
			setupNode(info);
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node n = e.getChildNodes().item(i);
				recursiveCheck(n);
			}
		} else if (node.getNodeType() == Node.DOCUMENT_NODE) {
			recursiveCheck(node.getFirstChild());
		}
	}

	private static void setupNode(SNodeInfo info) {
		Component c = Convert.infoToComp(info);
		Object constr = info.boundings;
		if (secondaryContainer != null) {
			secondaryContainer.add(c, constr);
		} else {
			workingContainer.add(c);
			if (c instanceof JPanel) {
				secondaryContainer = (JPanel) c;
				Dimension dim = new Dimension(info.width, info.height);
				workingContainer.setPreferredSize(dim);
				secondaryContainer.setPreferredSize(dim);
				workingContainer.setSize(dim);
				secondaryContainer.setSize(dim);
			}
		}
		position(c, info);
		c.setVisible(true);
		System.out.println("Added " + c);
	}

	private static void position(Component c, SNodeInfo info) {
		c.setBounds(info.x, info.y, info.width, info.height);
	}
}
