package ts.logix.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import k.core.util.gui.SwingAWTUtils;
import ts.logix.Test;
import ts.logix.file.LogixSystem;
import ts.logix.interfaces.Positionable;
import ts.logix.positionables.PPyPoint;
import ts.logix.py.PointSys;

public class LSGui {
    public static class JCircuitArea extends JComponent {
        private static final long serialVersionUID = 1L;
        private LogixSystem ls;

        public JCircuitArea(Dimension size, LogixSystem sim) {
            setMinimumSize(size);
            setPreferredSize(getMinimumSize());
            setSize(getPreferredSize());
            ls = sim;
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.clearRect(0, 0, getWidth(), getHeight());
            for (Positionable p : ls.objs) {
                drawPos(g, p);
            }
            super.paint(g);
        }

    }

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
            if (fake.isLocked()) {
                continue;
            }
            final LogixSystem ls = system.get();
            if (ls != null) {
                try {
                    SwingAWTUtils.runOnDispatch(new Runnable() {

                        @Override
                        public void run() {
                            gui_ls(ls);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void gui_ls(LogixSystem ls) {
        JFrame f = Test.frame;
        f.setLayout(Test.DEFAULT_MANAGER);
        Dimension s = f.getSize();
        Insets i = f.getInsets();
        s.width -= i.left + i.right;
        s.height -= i.top + i.bottom;
        JCircuitArea jca = new JCircuitArea(s, ls);
        jca.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jca.setVisible(true);
        f.add(jca, Test.center);
        SwingAWTUtils.validate(f);
    }

    protected static void drawPos(Graphics g, Positionable p) {
        if (p instanceof PPyPoint) {
            PPyPoint point = (PPyPoint) p;
            g.setColor(PointSys.readConnection(point.point).isEmpty() ? Color.GRAY
                    : Color.GREEN);
            g.fillOval(p.getX(), p.getY(), 5, 5);
        }
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
