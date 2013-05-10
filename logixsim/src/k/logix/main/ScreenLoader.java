package k.logix.main;

import javax.swing.JFrame;

import k.logix.util.Convert;
import k.logix.util.ScreenUtil;
import k.logix.xml.SNodeInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ScreenLoader {
	private static JFrame workingFrame = null;
	private static Document workingDoc = null;
	private static Document dom = null;

	public static void loadScreenByID(String id) {
		workingFrame = LogixMain.screen;
		dom = ScreenUtil.getDOMByID(id);
		recursiveCheck((Node) (dom));
	}

	private static void recursiveCheck(Node node) {
		SNodeInfo info = SNodeInfo.read(node);
		if(info == null) {
			return;
		}
		setupNode(info);
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node n = node.getChildNodes().item(i);
			recursiveCheck(n);
		}
	}

	private static void setupNode(SNodeInfo info) {
		Component c = Convert.infoToComp(info);
		
	}
}
