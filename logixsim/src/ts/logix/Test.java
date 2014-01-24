package ts.logix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;

import k.core.util.Helper;
import k.core.util.gui.JClickableText;
import k.core.util.gui.SwingAWTUtils;

@SuppressWarnings("serial")
public class Test {

    protected static final int LOADINGSCREEN = 0x00, MAINMENU = 0x01,
            NEWSYS = 0x02, LOADSYS = 0x03;
    private static JFrame frame;
    private static JPanel pane = new JPanel(new GridBagLayout());
    private static JClickableText welcome = new JClickableText(
            "Welcome to LogixSim!"), new_mm_button = new JClickableText(
            "New logixsys..."), load_mm_button = new JClickableText(
            "Load logixsys..."), quit_mm_button = new JClickableText(
            "Quit logixsim...");
    private static GridBagConstraints center = new GridBagConstraints();
    private static int loadedID = LOADINGSCREEN;

    static {
        center.anchor = GridBagConstraints.CENTER;
        center.ipady = center.ipadx = 10;
        welcome.setFont(SwingAWTUtils.getDefaultModdedFont(welcome, Font.BOLD
                | Font.ITALIC, 36));
        Font menu = SwingAWTUtils.getDefaultModdedFont(new_mm_button,
                Font.BOLD, 24);
        new_mm_button.setFont(menu);
        load_mm_button.setFont(menu);
        quit_mm_button.setFont(menu);
        welcome.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                change_gui(MAINMENU);
            }
        });
        new_mm_button.setAction(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                change_gui(NEWSYS);
            }
        });
        load_mm_button.setAction(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                change_gui(LOADSYS);
            }
        });
        quit_mm_button.setAction(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println("Shutting down...");
                SwingAWTUtils.kill(frame);
            }
        });
    }

    public static void main(String[] args) {
        args = Helper.ProgramProps.normalizeCommandArgs(args);
        Helper.ProgramProps.acceptAll(args);
        display_init();
    }

    protected static void change_gui(int id) {
        SwingAWTUtils.removeAll(pane);
        if (id == MAINMENU) {
            GridBagConstraints proxy = (GridBagConstraints) center.clone();
            pane.add(new_mm_button, nextY(proxy));
            pane.add(load_mm_button, nextY(proxy));
            pane.add(quit_mm_button, nextY(proxy));
        } else if (id == LOADINGSCREEN) {
            pane.add(welcome, center);
        } else {
            System.err.println("Skipping id " + id);
            change_gui(loadedID);
            return;
        }
        SwingAWTUtils.validate(pane);
        loadedID = id;
    }

    private static void display_init() {
        frame = new JFrame("logixsim");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        visual_setup();
        frame.pack();
        SwingAWTUtils.drop(frame);
        SwingAWTUtils.setBackground(new Color(140, 0, 0), frame);
        frame.setVisible(true);
    }

    private static void visual_setup() {
        frame.add(pane);
        change_gui(LOADINGSCREEN);
    }

    public static GridBagConstraints nextX(GridBagConstraints gbc) {
        gbc.gridx++;
        return gbc;
    }

    public static GridBagConstraints nextY(GridBagConstraints gbc) {
        gbc.gridy++;
        return gbc;
    }

}
