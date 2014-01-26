package ts.logix.gui;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import k.core.util.gui.SwingAWTUtils;

import ts.logix.Test;
import ts.logix.file.LogixSystem;

public class LSGui {
    private static Semaphore run = new Semaphore(1);
    private static ReentrantLock fake = new ReentrantLock();
    private static AtomicReference<LogixSystem> system = new AtomicReference<LogixSystem>();
    private static Runnable rtm_runnable = new Runnable() {

        @Override
        public void run() {
            Test.change_gui(Test.MAINMENU);
        }
    };

    public static void begin(boolean newLS) {
        if (newLS) {
            String name = JOptionPane.showInputDialog(null,
                    "What should this logixsim be called?", "Choose a Name",
                    JOptionPane.QUESTION_MESSAGE);
            if (name == null) {
                JOptionPane.showMessageDialog(null,
                        "You didn't enter a name, returning to main menu.",
                        "logixsys", JOptionPane.INFORMATION_MESSAGE, null);
                returnToMenu();
                return;
            }
            system.set(new LogixSystem(name));
        } else {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                system.set(LogixSystem.load(jfc.getSelectedFile()));
            } else {
                JOptionPane.showMessageDialog(null,
                        "You didn't pick a file, returning to main menu.",
                        "logixsys", JOptionPane.INFORMATION_MESSAGE, null);
                returnToMenu();
                return;
            }
        }
        run.release();
    }

    public static void keepMain() {
        run.acquireUninterruptibly();
        while (!fake.isLocked()) {
            run.acquireUninterruptibly();
            LogixSystem ls = system.get();
            if (ls != null) {
                gui_ls(ls);
            }
        }
    }

    private static void gui_ls(LogixSystem ls) {
        JPanel p = Test.pane;
        SwingAWTUtils.removeAll(p);
        SwingAWTUtils.validate(p);
    }

    private static void returnToMenu() {
        try {
            SwingAWTUtils.runOnDispatch(rtm_runnable);
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(rtm_runnable);
        }
    }

    public static void close() {
        fake.lock();
        run.release();
    }

}
