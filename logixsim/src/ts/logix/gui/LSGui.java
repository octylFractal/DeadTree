package ts.logix.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
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
            SwingAWTUtils.setAllSize(this, size, SwingAWTUtils.SETALL);
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

    public static class JPart extends JLabel implements Cloneable {
        private static final long serialVersionUID = 1L;

        private static class JDraggingPart extends JPart implements Cloneable {
            private static final long serialVersionUID = 1L;

            int dx = -1, dy = -1;

            public JDraggingPart(JPart j) {
                super(j.img, j.getText());
                SwingAWTUtils.setAllSize(this, Test.frame.getContentPane()
                        .getSize(), SwingAWTUtils.SETALL);
                setVisible(true);
                addMouseMotionListener(new MouseMotionAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        Point los = Test.frame.getLocationOnScreen();
                        Insets i = Test.frame.getInsets();
                        BufferedImage img = getSuperImg();
                        int x = e.getXOnScreen() - los.x - i.left
                                - img.getWidth(), y = e.getYOnScreen() - los.y
                                - i.top - img.getHeight();
                        dx = x;
                        dy = y;
                        repaint();
                    }
                });
            }

            public BufferedImage getSuperImg() {
                return super.img;
            }

            @Override
            public void paintComponent(Graphics g) {
                if (dx == -1 || dy == -1) {
                    return;
                }
                g.drawImage(super.img, dx, dy, null);
            }

        }

        private BufferedImage img = null;

        private JDraggingPart drag = null;

        public JPart(BufferedImage image, String name) {
            super(name);
            img = image;

            if (getClass() == JPart.class) {
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        drag = new JDraggingPart(JPart.this.clone());
                        Test.frame.getLayeredPane().add(drag,
                                JLayeredPane.DEFAULT_LAYER, 0);
                        SwingAWTUtils.validate(getParent());
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Test.frame.getLayeredPane().remove(drag);
                        SwingAWTUtils.validate(getParent());
                        drag = null;
                    }

                });
                addMouseMotionListener(new MouseMotionAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (drag != null) {
                            e = new MouseEvent(drag, e.getID(), e.getWhen(),
                                    e.getModifiers(), e.getX(), e.getY(),
                                    e.getXOnScreen(), e.getYOnScreen(),
                                    e.getClickCount(), false, e.getButton());
                            drag.processMouseMotionEvent(e);
                        }
                    }
                });
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = (getWidth() / 2) - (img.getWidth()), y = 0;
            g.drawImage(img, x, y, null);
        }

        @Override
        public JPart clone() {
            BufferedImage img2 = new BufferedImage(img.getWidth(),
                    img.getHeight(), img.getType());
            img2.createGraphics().drawImage(img, 0, 0, null);
            return new JPart(img2, super.getText());
        }
    }

    public static final JPart POINT;
    static {
        BufferedImage img = new BufferedImage(10, 10,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, 10, 10);
        g.setColor(Color.GRAY);
        g.fillOval(0, 0, 7, 7);
        POINT = new JPart(img, "Point");
    }

    public static class JCircutParts extends JPanel {
        private static final long serialVersionUID = 1L;

        public static final JPart[] parts = {//
        POINT //
        };

        private static final Dimension DEFAULT_DIMENSIONS = new Dimension(200,
                0);

        public static final Dimension getDefaultForHeight(int height) {
            Dimension ret = (Dimension) DEFAULT_DIMENSIONS.clone();
            ret.height = height;
            return ret;
        }

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
        SwingAWTUtils.setAllSize(jcp,
                JCircutParts.getDefaultForHeight(s.height),
                SwingAWTUtils.SETALL);
        jca.setBorder(b);
        jcp.setBorder(b);
        jca.setVisible(true);
        jcp.setVisible(true);
        GridBagConstraints gbc = (GridBagConstraints) Test.center.clone();
        f.add(jcp, Test.nextX(gbc), 0);
        f.add(jca, Test.nextX(gbc), 1);
        f.pack();
        SwingAWTUtils.validate(f);
        SwingAWTUtils.drop(f);
        jca.setLocation(-1, -1);
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
