package k.logix.util;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import k.logix.main.BuildAreaFactory;
import k.logix.xml.SNodeInfo;

public class Convert {
	public static LayoutManager manager = new GridBagLayout();

	public static Component infoToComp(SNodeInfo info) {
		Component c = null;
		switch (info.type) {
		case SNodeInfo.BUTTON:
			c = new JButton(info.name);
			break;
		case SNodeInfo.SCREEN:
			c = new JPanel(manager, true);
			break;
		case SNodeInfo.BUILD_AREA:
			c = BuildAreaFactory.newBuild(info);
			break;
		case SNodeInfo.TITLE:
			c = new JLabel(info.text);
			break;
		case SNodeInfo.UNKNOWN:
			c = new JLabel(
					"Error parsing this element: unknown element "
							+ info.base.getNodeName()
							+ ".\n"
							+ "Please report this error to the developer of the mod pack"
							+ "(the developer if you have not added any mod packs)");
			break;
		default:
			c = null;
			return c;
		}
		c.setName(info.name);
		return c;
	}

}
