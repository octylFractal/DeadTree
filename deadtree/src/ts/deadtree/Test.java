package ts.deadtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ts.deadtree.gui.LSGui;
import k.core.util.Helper;
import k.core.util.gui.JClickableText;
import k.core.util.gui.SwingAWTUtils;

@SuppressWarnings("serial")
public class Test {

    public static final int LOADINGSCREEN = 0x00, MAINMENU = 0x01,
            NEWSYS = 0x02, LOADSYS = 0x03;
    public static final LayoutManager DEFAULT_MANAGER = new GridBagLayout();
    public static JFrame frame;
    public static JPanel pane = new JPanel();
    public static JClickableText welcome = new JClickableText(
            "Welcome to DeadTree!"), new_mm_button = new JClickableText(
            "New logixsys..."), load_mm_button = new JClickableText(
            "Load logixsys..."), quit_mm_button = new JClickableText(
            "Quit DeadTree...");
    public static GridBagConstraints center = new GridBagConstraints();
    public static int loadedID = LOADINGSCREEN;

    static {
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
        LSGui.keepMain();
    }

    public static void change_gui(int id) {
        SwingAWTUtils.removeAll(pane);
        pane.setLayout(DEFAULT_MANAGER);
        if (id == MAINMENU) {
            GridBagConstraints proxy = (GridBagConstraints) center.clone();
            pane.add(new_mm_button, nextY(proxy));
            pane.add(load_mm_button, nextY(proxy));
            pane.add(quit_mm_button, nextY(proxy));
        } else if (id == NEWSYS) {
            handoffControl(true);
        } else if (id == LOADSYS) {
            handoffControl(false);
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

    private static void handoffControl(boolean newLS) {
        LSGui.begin(newLS);
    }

    private static void display_init() {
        frame = new JFrame("DeadTree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                LSGui.close();
            }
        });
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
