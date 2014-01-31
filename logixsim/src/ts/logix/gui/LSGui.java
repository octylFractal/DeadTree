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
import java.io.IOException;
import java.io.Serializable;
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
import ts.logix.positionables.PPyPoint;
import ts.logix.positionables.Positionable;
import ts.logix.py.PointSys;

public class LSGui {
    public static class JCircuitArea extends JComponent {
        private static final long serialVersionUID = 1L;
        private LogixSystem ls;

        public JCircuitArea(Dimension size, LogixSystem sim) {
            SwingAWTUtils.setAllSize(this, size, SwingAWTUtils.SETALL);
            ls = sim;
            setBackground(Color.WHITE);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.clearRect(0, 0, getWidth(), getHeight());
            for (Positionable p : ls.objs) {
                if (p.isVisible())
                    drawPos(g, p);
            }
            super.paint(g);
        }

    }

    public abstract static class JPart extends JLabel implements Cloneable,
            Serializable {
        private static final long serialVersionUID = 1L;

        /*
         * Assumes that this is setup in a LSGui. Doesn't work without one!
         */
        private static class JDraggingPart extends JPart implements Cloneable {
            private static final long serialVersionUID = 1L;

            int dx = -1, dy = -1;
            JPart part = null;
            JCircutParts part_parent = null;

            private boolean fake_invis;

            /*
             * Assumes that j is under a JCircutParts to avoid passing a second
             * parameter
             */
            public JDraggingPart(JPart j) {
                super(j.img, j.getText());
                if (!(j.getParent() instanceof JCircutParts)) {
                    throw new IllegalStateException(
                            "Parent does not extend JCircutParts");
                }
                part_parent = (JCircutParts) j.getParent();
                SwingAWTUtils.setAllSize(this, Test.frame.getContentPane()
                        .getSize(), SwingAWTUtils.SETALL);
                setVisible(true);
                init();
                part = j;
            }

            public BufferedImage getSuperImg() {
                return super.img;
            }

            @Override
            public void paintComponent(Graphics g) {
                if (dx == -1 || dy == -1 || fake_invis) {
                    return;
                }
                g.drawImage(super.img, dx, dy, null);
            }

            public void release(MouseEvent e) {
                SwingAWTUtils.setAllSize(
                        this,
                        new Dimension(super.img.getWidth(), super.img
                                .getHeight()), SwingAWTUtils.SETALL);
                setLocation(dx, dy);
                SwingAWTUtils.validate(getParent());
                int px = dx, py = dy;
                dx = 0;
                dy = 0;
                px -= part_parent.getWidth();
                part.released(e, px, py);
                fake_invis = true;
            }

            public void drag(MouseEvent e) {
                int px = dx, py = dy;
                px -= part_parent.getWidth();
                part.dragged(e, px, py);
                fake_invis = false;
            }

            @Override
            public void released0(MouseEvent e, int x, int y) {
                e.consume();
            }

            @Override
            public void dragged0(MouseEvent e, int x, int y) {
                e.consume();
            }

            private void init() {
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        SwingAWTUtils.setAllSize(JDraggingPart.this, Test.frame
                                .getContentPane().getSize(),
                                SwingAWTUtils.SETALL);
                        dx = getX();
                        dy = getY();
                        setLocation(0, 0);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        release(e);
                    }

                });
                addMouseMotionListener(new MouseMotionAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        Point los = Test.frame.getLocationOnScreen();
                        Insets i = Test.frame.getInsets();
                        BufferedImage img = getSuperImg();
                        int x = e.getXOnScreen() - los.x - i.left
                                - img.getWidth() / 2, y = e.getYOnScreen()
                                - los.y - i.top - img.getHeight() / 2;
                        dx = x;
                        dy = y;
                        if (dx < 0) {
                            dx = 0;
                        }
                        if (dy < 0) {
                            dy = 0;
                        }
                        int width = getWidth() - img.getWidth(), height = getHeight()
                                - img.getHeight();
                        if (dx >= width) {
                            dx = width - 1;
                        }
                        if (dy >= height) {
                            dy = height - 1;
                        }
                        repaint();
                        drag(e);
                    }
                });
            }
        }

        private transient BufferedImage img = null;

        private JDraggingPart drag = null;

        public JPart(BufferedImage image, String name) {
            super(name);
            img = image;

            if (!JDraggingPart.class.isInstance(this)) {
                init();
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
            JPart c = SwingAWTUtils.cloneLikeSerial(this);
            BufferedImage img1 = new BufferedImage(img.getWidth(),
                    img.getHeight(), img.getType());
            Graphics g = img1.createGraphics();
            g.drawImage(img, 0, 0, null);
            c.img = img1;
            c.setText(getText());
            return c;
        }

        public void released(MouseEvent e, int x, int y) {
            released0(e, x, y);
        }

        public void dragged(MouseEvent e, int x, int y) {
            dragged0(e, x, y);
        }

        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            init();
        }

        private void init() {
            addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    if (getParent() == null) {
                        System.err.println("null parent");
                        return;
                    }
                    drag = new JDraggingPart(JPart.this);
                    Test.frame.getLayeredPane().add(drag,
                            JLayeredPane.DEFAULT_LAYER, 0);
                    SwingAWTUtils.validate(getParent());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (drag == null) {
                        return;
                    }
                    e = new MouseEvent(drag, e.getID(), e.getWhen(),
                            e.getModifiers(), e.getX(), e.getY(),
                            e.getXOnScreen(), e.getYOnScreen(),
                            e.getClickCount(), false, e.getButton());
                    drag.processMouseEvent(e);
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

        public abstract void released0(MouseEvent e, int x, int y);

        public abstract void dragged0(MouseEvent e, int x, int y);
    }

    public static class JPointPart extends JPart {
        private static final long serialVersionUID = 1L;

        public static JPointPart create() {
            BufferedImage img = new BufferedImage(7, 7,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, 7, 7);
            g.setColor(Color.GRAY);
            g.fillOval(0, 0, 7, 7);
            return new JPointPart(img, "Point");
        }

        private transient PPyPoint ppp;

        private JPointPart(BufferedImage image, String name) {
            super(image, name);
            ppp = new PPyPoint();
            ppp.setVisible(false);
        }

        @Override
        public void dragged0(MouseEvent e, int x, int y) {
            ppp.setVisible(false);
        }

        @Override
        public void released0(MouseEvent e, int x, int y) {
            ppp.x = x;
            ppp.y = y;
            ppp.setVisible(true);
            if (!sys().objs.contains(ppp)) {
                sys().objs.add(ppp);
            }
        }

        private void writeObject(java.io.ObjectOutputStream s)
                throws IOException, ClassNotFoundException {
            s.defaultWriteObject();
            s.writeInt(ppp.x);
            s.writeInt(ppp.y);
            s.writeBoolean(ppp.isVisible());
        }

        private void readObject(java.io.ObjectInputStream s)
                throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            ppp = new PPyPoint();
            ppp.x = s.readInt();
            ppp.y = s.readInt();
            ppp.setVisible(s.readBoolean());
        }

    }

    public static final JPart POINT = JPointPart.create();

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

    public static LogixSystem sys() {
        return system.get();
    }

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
            final LogixSystem ls = sys();
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
            g.fillOval(p.getX(), p.getY(), 7, 7);
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
