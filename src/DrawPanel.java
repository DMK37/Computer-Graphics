import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener, ChangeListener {
    private List<Polygon> polygons;
    private int x;
    private int y;

    JFileChooser fileChooser = new JFileChooser();

    private int draggedPointIdx;
    private int[] draggedLineIdx;

    private Polygon draggedPolygon;
    private Point prevPosition;

    private JPopupMenu pointMenu;
    private JMenuItem deleteItem;

    private JPopupMenu lineMenu;
    private JMenuItem addMiddlePoint;

    private JMenuItem setVertical;

    private JMenuItem setHorizontal;

    private JMenuItem setDefault;

    private JPopupMenu polygonMenu;
    private JMenuItem deletePolygon;

    private int prevIdx;
    private Point deletePointPos;
    private Polygon currentPolygon;

    private static ImageIcon verticalIcon = new ImageIcon("icons/vertical.png");

    private static ImageIcon horizontalIcon = new ImageIcon("icons/horizontal.png");

    public DrawPanel() {

        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML FILES", "xml", "XML");
        fileChooser.setFileFilter(filter);
        draggedPointIdx = -1;
        draggedLineIdx = null;
        polygons = new ArrayList<>();
        polygons.add(new Polygon());
        polygons.get(0).add(new Point(169, 99));
        polygons.get(0).add(new Point(95, 446));
        polygons.get(0).add(new Point(530, 419));
        polygons.get(0).add(new Point(530, 151));
        polygons.get(0).add(new Point(445, 99));
        polygons.get(0).add(new Point(169, 99));
        polygons.get(0).setHorizontal(4);
        polygons.get(0).setVertical(2);

        int move = 150;
        polygons.add(new Polygon());
        polygons.get(1).add(new Point(639,511-move));
        polygons.get(1).add(new Point(716,544-move));
        polygons.get(1).add(new Point(920,544-move));
        polygons.get(1).add(new Point(920,723-move));
        polygons.get(1).add(new Point(855, 810-move));
        polygons.get(1).add(new Point(637,827-move));
        polygons.get(1).add(new Point(504, 827-move));
        polygons.get(1).add(new Point(469, 740-move));
        polygons.get(1).add(new Point(651, 760-move));
        polygons.get(1).add(new Point(771, 733-move));
        polygons.get(1).add(new Point(788, 614-move));
        polygons.get(1).add(new Point(659, 589-move));
        polygons.get(1).add(new Point(578,636-move));
        polygons.get(1).add(new Point(639,511-move));
        polygons.get(1).setHorizontal(1);
        polygons.get(1).setVertical(2);
        polygons.get(1).setHorizontal(5);




        JPanel j = new JPanel();
        pointMenu = new JPopupMenu();
        polygonMenu = new JPopupMenu();
        deleteItem = new JMenuItem("Delete Point");
        deleteItem.addActionListener(this);
        pointMenu.add(deleteItem);
        lineMenu = new JPopupMenu();
        addMiddlePoint = new JMenuItem("Add point");
        addMiddlePoint.addActionListener(this);
        lineMenu.add(addMiddlePoint);
        setVertical = new JMenuItem("Set Vertical");
        setVertical.addActionListener(this);
        lineMenu.add(setVertical);
        setHorizontal = new JMenuItem("Set Horizontal");
        setHorizontal.addActionListener(this);
        lineMenu.add(setHorizontal);
        setDefault = new JMenuItem("Set Default");
        setDefault.addActionListener(this);
        lineMenu.add(setDefault);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setBackground(Color.PINK);
        MainPanel.slider.addChangeListener(this);
        deletePolygon = new JMenuItem("Delete Polygon");
        deletePolygon.addActionListener(this);
        polygonMenu.add(deletePolygon);
    }

    public void pickAndDraw(Graphics2D graphics, Point a, Point b,int r, int g, int bl) {
        if(MainPanel.wuBox.isSelected())
        {
            Calculation.WuAlgo(graphics,a.x,a.y,b.x,b.y,r,g,bl);
        }
        if (MainPanel.bresenhamBox.isSelected()) {
            Calculation.bresenham(graphics, a.x, a.y, b.x, b.y);
        } else {
            graphics.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics = (Graphics2D) g;
        var val = Color.RGBtoHSB(44, 115, 85, null);
        for (Polygon polygon :
                polygons) {
            g.setColor(Color.BLACK);
            if(MainPanel.sliderBox.isSelected() && polygon.isDone()) {
                polygon.createOffset(MainPanel.slider.getValue());
                polygon.findOffset();

                var map = polygon.polishOffset();
                for(int i = 0; i < polygon.offsetPoints.size(); i++) {
                    var ls = map.get(i);
                    if(ls.get(1) == null || ls.get(0) == null)
                        continue;
                    pickAndDraw(graphics, polygon.offsetPoints.get(i), ls.get(0),0,0,0);
                    pickAndDraw(graphics, polygon.offsetPoints.get(i), ls.get(1),0,0,0);
                }
            }

            g.setColor(Color.getHSBColor(val[0], val[1], val[2]));
            for (int i = 0; i < polygon.getPoints().size() - 1; i++) {
                Point a = polygon.getPoints().get(i);
                Point b = polygon.getPoints().get(i + 1);
                pickAndDraw(graphics, a, b,44,115,85);
                g.setColor(Color.getHSBColor(val[0], val[1], val[2]));
                graphics.drawOval(a.x - 4, a.y - 4, 8, 8);
                graphics.fillOval(a.x - 4, a.y - 4, 8, 8);
                if (polygon.getPosition(i) == Position.Vertical) {
                    graphics.drawImage(verticalIcon.getImage(), a.x - 10, (b.y + a.y) / 2 - 11, 20, 23, this);
                }
                if (polygon.getPosition(i) == Position.Horizontal) {
                    graphics.drawImage(horizontalIcon.getImage(), (b.x + a.x) / 2 - 11, a.y - 10, 23, 20, this);
                }
            }
            Point b = polygon.getPoints().get(polygon.getPoints().size() - 1);
            g.setColor(Color.getHSBColor(val[0], val[1], val[2]));
            graphics.drawOval(b.x - 4, b.y - 4, 8, 8);
            graphics.fillOval(b.x - 4, b.y - 4, 8, 8);
            if (polygon.isDone()) {
                Point a = polygon.getPoints().get(0);
                pickAndDraw(graphics, a, b,44,115,85);

                var l = polygon.getX();
                if (polygon.getPosition(polygon.getPoints().size() - 1) == Position.Vertical) {
                    graphics.drawImage(verticalIcon.getImage(), a.x - 10, (b.y + a.y) / 2 - 11, 20, 23, this);
                }
                if (polygon.getPosition(polygon.getPoints().size() - 1) == Position.Horizontal) {
                    graphics.drawImage(horizontalIcon.getImage(), (b.x + a.x) / 2 - 11, a.y - 10, 23, 20, this);
                }
            }
        }
        if (MainPanel.isDrawBox()) {
            int lastIdx = polygons.size() - 1;
            if (lastIdx < 0) return;
            List<Point> ls = polygons.get(lastIdx).getPoints();
            if (!polygons.get(lastIdx).isDone() && !ls.isEmpty()) {
                Point p = ls.get(ls.size() - 1);
                pickAndDraw(graphics, p, new Point(x, y),44,115,85);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                if (!MainPanel.isDrawBox())
                    return;
                int lastIdx = polygons.size() - 1;
                if (lastIdx < 0) {
                    polygons.add(new Polygon());
                    lastIdx++;
                }
                if (polygons.get(lastIdx).isDone()) {
                    polygons.add(new Polygon());
                    lastIdx++;
                }
                polygons.get(lastIdx).add(e.getPoint());

                repaint();
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        prevPosition = e.getPoint();
        if (MainPanel.isDrawBox())
            return;

        if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
            for (var polygon :
                    polygons) {
                List<Point> points = polygon.getPoints();
                for (int i = 0; i < points.size(); i++) {
                    var point = points.get(i);
                    if (Math.abs(point.x - e.getX()) <= 4 &&
                            Math.abs(point.y - e.getY()) <= 4) {
                        currentPolygon = polygon;
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            draggedPointIdx = i;
                        }
                        if (SwingUtilities.isRightMouseButton(e)) {
                            deletePointPos = point;

                            pointMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                        return;
                    }
                }
            }
            for (Polygon polygon : polygons) {
                List<Point> points = polygon.getPoints();
                for (int i = 0; i < points.size() - 1; i++) {
                    if (Calculation.isNextToEdge(points.get(i), points.get(i + 1), e.getPoint())) {
                        currentPolygon = polygon;
                        prevIdx = i;
                        if (SwingUtilities.isLeftMouseButton(e))
                            draggedLineIdx = new int[]{i, i + 1};
                        if (SwingUtilities.isRightMouseButton(e)) {

                            lineMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                        return;
                    }
                }
                if (Calculation.isNextToEdge(points.get(points.size() - 1), points.get(0), e.getPoint())) {
                    currentPolygon = polygon;
                    prevIdx = points.size() - 1;
                    if (SwingUtilities.isLeftMouseButton(e))
                        draggedLineIdx = new int[]{points.size() - 1, 0};
                    if (SwingUtilities.isRightMouseButton(e)) {

                        lineMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                    return;
                }
            }
            for (Polygon polygon : polygons) {
                List<Point> points = polygon.getPoints();
                if (Calculation.isInside(points, new Point2D.Double(e.getPoint().x, e.getPoint().y))) {
                    currentPolygon = polygon;
                    if(SwingUtilities.isLeftMouseButton(e))
                        draggedPolygon = polygon;
                    if(SwingUtilities.isRightMouseButton(e))
                        polygonMenu.show(e.getComponent(), e.getX(), e.getY());
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggedPointIdx = -1;
        draggedLineIdx = null;
        draggedPolygon = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (MainPanel.isDrawBox())
            return;
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (draggedPointIdx != -1) {
                var points = currentPolygon.getPoints();
                points.get(draggedPointIdx).x = e.getX();
                points.get(draggedPointIdx).y = e.getY();
                if (draggedPointIdx > 0 && draggedPointIdx != points.size() - 1) {
                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Vertical) {
                        points.get(draggedPointIdx + 1).x = e.getX();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx - 1) == Position.Vertical) {
                        points.get(draggedPointIdx - 1).x = e.getX();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Horizontal) {
                        points.get(draggedPointIdx + 1).y = e.getY();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx - 1) == Position.Horizontal) {
                        points.get(draggedPointIdx - 1).y = e.getY();
                    }
                }
                if (draggedPointIdx == 0) {
                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Vertical) {
                        points.get(draggedPointIdx + 1).x = e.getX();
                    }
                    if (currentPolygon.getPosition(points.size() - 1) == Position.Vertical) {
                        points.get(points.size() - 1).x = e.getX();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Horizontal) {
                        points.get(draggedPointIdx + 1).y = e.getY();
                    }
                    if (currentPolygon.getPosition(points.size() - 1) == Position.Horizontal) {
                        points.get(points.size() - 1).y = e.getY();
                    }
                }
                if (draggedPointIdx == points.size() - 1) {
                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Vertical) {
                        points.get(0).x = e.getX();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx - 1) == Position.Vertical) {
                        points.get(draggedPointIdx - 1).x = e.getX();
                    }

                    if (currentPolygon.getPosition(draggedPointIdx) == Position.Horizontal) {
                        points.get(0).y = e.getY();
                    }
                    if (currentPolygon.getPosition(draggedPointIdx - 1) == Position.Horizontal) {
                        points.get(draggedPointIdx - 1).y = e.getY();
                    }
                }
                repaint();
            }
            if (draggedLineIdx != null) {
                var points = currentPolygon.getPoints();
                points.get(draggedLineIdx[0]).x += e.getX() - prevPosition.x;
                points.get(draggedLineIdx[0]).y += e.getY() - prevPosition.y;
                points.get(draggedLineIdx[1]).x += e.getX() - prevPosition.x;
                points.get(draggedLineIdx[1]).y += e.getY() - prevPosition.y;
                if (draggedLineIdx[0] != 0 && draggedLineIdx[1] != points.size() - 1) {
                    // finish modified drag line
                    if (currentPolygon.getPosition(draggedLineIdx[0] - 1) == Position.Vertical) {
                        points.get(draggedLineIdx[0] - 1).x = points.get(draggedLineIdx[0]).x;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Vertical) {
                        points.get(draggedLineIdx[1] + 1).x = points.get(draggedLineIdx[1]).x;
                    }

                    if (currentPolygon.getPosition(draggedLineIdx[0] - 1) == Position.Horizontal) {
                        points.get(draggedLineIdx[0] - 1).y = points.get(draggedLineIdx[0]).y;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Horizontal) {
                        points.get(draggedLineIdx[1] + 1).y = points.get(draggedLineIdx[1]).y;
                    }
                }
                if (draggedLineIdx[0] == 0) {
                    if (currentPolygon.getPosition(points.size() - 1) == Position.Vertical) {
                        points.get(points.size() - 1).x = points.get(draggedLineIdx[0]).x;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Vertical) {
                        points.get(draggedLineIdx[1] + 1).x = points.get(draggedLineIdx[1]).x;
                    }
                    if (currentPolygon.getPosition(points.size() - 1) == Position.Horizontal) {
                        points.get(points.size() - 1).y = points.get(draggedLineIdx[0]).y;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Horizontal) {
                        points.get(draggedLineIdx[1] + 1).y = points.get(draggedLineIdx[1]).y;
                    }
                }
                if (draggedLineIdx[1] == points.size() - 1) {
                    if (currentPolygon.getPosition(draggedLineIdx[0] - 1) == Position.Vertical) {
                        points.get(draggedLineIdx[0] - 1).x = points.get(draggedLineIdx[0]).x;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Vertical) {
                        points.get(0).x = points.get(draggedLineIdx[1]).x;
                    }

                    if (currentPolygon.getPosition(draggedLineIdx[0] - 1) == Position.Horizontal) {
                        points.get(draggedLineIdx[0] - 1).y = points.get(draggedLineIdx[0]).y;
                    }
                    if (currentPolygon.getPosition(draggedLineIdx[1]) == Position.Horizontal) {
                        points.get(0).y = points.get(draggedLineIdx[1]).y;
                    }
                }

                repaint();
            }
            if (draggedPolygon != null) {
                for (Point point :
                        draggedPolygon.getPoints()) {
                    point.x += e.getX() - prevPosition.x;
                    point.y += e.getY() - prevPosition.y;
                    repaint();
                }
            }
            prevPosition = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (MainPanel.isDrawBox())
            repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteItem) {
            int idx = currentPolygon.getPoints().indexOf(deletePointPos);
            if(idx == 0) {
                currentPolygon.setDefault(currentPolygon.getPoints().size() - 1);
            } else {
                currentPolygon.setDefault(idx - 1);
            }
            currentPolygon.delete(deletePointPos);
            if (currentPolygon.getPoints().size() <= 2)
                polygons.remove(currentPolygon);
            currentPolygon = null;
            deletePointPos = null;
            repaint();
        }
        if(e.getSource() == deletePolygon) {
            polygons.remove(currentPolygon);
            repaint();
        }
        if (e.getSource() == addMiddlePoint) {
            Point a, b;
            List<Point> points = currentPolygon.getPoints();
            currentPolygon.setDefault(prevIdx);
            if (points.size() == prevIdx + 1) {
                a = points.get(prevIdx);
                b = points.get(0);
                currentPolygon.add(new Point((int) (a.getX() + b.getX()) / 2, (int) (a.getY() + b.getY()) / 2));
            } else {
                a = points.get(prevIdx);
                b = points.get(prevIdx + 1);
                currentPolygon.add(prevIdx + 1, new Point((int) (a.getX() + b.getX()) / 2, (int) (a.getY() + b.getY()) / 2));
            }

            currentPolygon = null;
            prevIdx = -1;
            repaint();
        }
        if (e.getSource() == setVertical) {
            currentPolygon.setVertical(prevIdx);
            repaint();
        }
        if (e.getSource() == setHorizontal) {
            currentPolygon.setHorizontal(prevIdx);
            repaint();
        }
        if (e.getSource() == setDefault) {
            currentPolygon.setDefault(prevIdx);
            repaint();
        }
        if(e.getSource() == MainPanel.sliderBox) {
            MainPanel.slider.setEnabled(MainPanel.sliderBox.isSelected());
            repaint();
        }
        if(e.getSource() == MainPanel.bresenhamBox) {
            MainPanel.wuBox.setSelected(false);
            repaint();
        }
        if(e.getSource() == MainPanel.wuBox) {
            MainPanel.bresenhamBox.setSelected(false);
            repaint();
        }

        if(e.getSource() == MainPanel.saveItem) {
           //int response = fileChooser.showOpenDialog(null);
            XMLEncoder encoder=null;
            try{
                encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(MainPanel.textField.getText()+".xml")));
            }catch(FileNotFoundException fileNotFound){
                System.out.println("ERROR: While Creating or Opening the File .xml");
            }
            encoder.writeObject(polygons);
            encoder.close();
        }

        if(e.getSource() == MainPanel.loadItem) {
            int response = fileChooser.showOpenDialog(null);
            if(response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                XMLDecoder decoder=null;
                try {
                    decoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
                } catch (FileNotFoundException ex) {
                    System.out.println("ERROR: File dvd.xml not found");
                }
                List<Polygon> pls =(List<Polygon>)decoder.readObject();
                polygons = pls;
                repaint();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == MainPanel.slider) {
            repaint();
        }
    }
}
