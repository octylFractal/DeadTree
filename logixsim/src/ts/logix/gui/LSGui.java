package ts.logix.gui;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
        }
        run.release();
    }

    public static void keepMain() {
        run.acquireUninterruptibly();
        while (!fake.isLocked()) {
            run.acquireUninterruptibly();
        }
    }

    private static void returnToMenu() {
        SwingUtilities.invokeLater(rtm_runnable);
    }

    public static void close() {
        fake.lock();
        run.release();
    }

}
