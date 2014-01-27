package ts.logix.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

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

    public static class JPart extends JComponent implements Cloneable {
        private static final long serialVersionUID = 1L;

        private BufferedImage img = null;

        public JPart(BufferedImage image) {
            img = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }

        @Override
        public JPart clone() {
            BufferedImage img2 = new BufferedImage(img.getWidth(),
                    img.getHeight(), img.getType());
            img2.createGraphics().drawImage(img, 0, 0, null);
            return new JPart(img2);
        }
    }

    public static final JPart POINT;
    static {
        BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, 5, 5);
        g.setColor(Color.GRAY);
        g.fillOval(0, 0, 5, 5);
        POINT = new JPart(img);
    }

    public static class JCircutParts extends JPanel {
        private static final long serialVersionUID = 1L;

        public static final JPart[] parts = {//
        POINT //
        };

        private JPart[] myParts = {};

        public JCircutParts() {
            setLayout(new GridBagLayout());
            myParts = parts.clone();
            GridBagConstraints gbc = (GridBagConstraints) Test.center.clone();
            for (int i = 0; i < myParts.length; i++) {
                JPart j = myParts[i].clone();
                add(j, Test.nextY(gbc));
            }
            SwingAWTUtils.validate(this);
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
            if (name == null || name.equals("")) {
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
        Border b = BorderFactory.createLineBorder(Color.BLACK);
        Insets i = f.getInsets(), bi = new Insets(1, 1, 1, 1);
        s.width -= i.left + i.right + bi.right + bi.left;
        s.height -= i.top + i.bottom + bi.top;
        JCircuitArea jca = new JCircuitArea(s, ls);
        JCircutParts jcp = new JCircutParts();
        jca.setBorder(b);
        jcp.setBorder(b);
        jca.setVisible(true);
        jcp.setVisible(true);
        GridBagConstraints gbc = (GridBagConstraints) Test.center.clone();
        f.add(jcp, Test.nextX(gbc), 0);
        f.add(jca, Test.nextX(gbc), 1);
        jca.setLocation(-1, -1);
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
