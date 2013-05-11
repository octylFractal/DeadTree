package k.logix.util;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import k.logix.xml.SNodeInfo;

public class Convert {

	public static Component infoToComp(SNodeInfo info) {
		Component c = null;
		switch (info.type) {
		case SNodeInfo.BUTTON:
			c = new JButton(info.name);
			break;
		case SNodeInfo.SCREEN:
			c = new JPanel(true);
			break;
		case SNodeInfo.UNKNOWN:
			c = new JLabel(
					"Error parsing this element: unknown element "
							+ info.base.getNodeName()
							+ ".\n"
							+ "Please report this error to the developer of the level pack"
							+ "(the developer if you have not added any level packs");
			break;
		default:
			c = null;
			break;
		}
		return c;
	}

}
