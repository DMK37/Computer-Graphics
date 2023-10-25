import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Polygon {
    public List<Point> points;

    public List<Position> positions;

    public List<Point> getPoints() {
        return points;
    }

    public List<Point> offsetPoints;

    private List<List<Double>> functions;

    public boolean isDone;

    //private boolean isOffset;

    public boolean isDone() {
        return isDone;
    }

    public Polygon() {
        isDone = false;
        points = new ArrayList<>();
        positions = new ArrayList<>();
        functions = new ArrayList<>();
        offsetPoints = new ArrayList<>();
    }

    public void createOffset(int offset) {
        functions = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            addFunction(Calculation.findOffsetLine(points, points.get(i), points.get(i + 1), offset));
        }
        addFunction(Calculation.findOffsetLine(points, points.get(points.size() - 1), points.get(0), offset));
    }

    public void addFunction(List<Double> func) {
        functions.add(func);
    }


    public void findOffset() {
        offsetPoints = new ArrayList<>();
        for (int i = 0; i < functions.size() - 1; i++) {
            var f1 = functions.get(i);
            var f2 = functions.get(i + 1);
            if (f2.get(0) == Double.POSITIVE_INFINITY) {
                double x = f2.get(1).intValue();
                int y = (int) (f1.get(0) * x + f1.get(1));

                offsetPoints.add(new Point((int) x, y));
                continue;
            }
            if (f1.get(0) == Double.POSITIVE_INFINITY) {
                double x = f1.get(1).intValue();
                int y = (int) (f2.get(0) * x + f2.get(1));
                offsetPoints.add(new Point((int) x, y));
                continue;
            }
            double x = ((f2.get(1) - f1.get(1)) / (f1.get(0) - f2.get(0)));
            int y = (int) (f1.get(0) * x + f1.get(1));
            offsetPoints.add(new Point((int) x, y));
        }


        var f1 = functions.get(functions.size() - 1);
        var f2 = functions.get(0);

        if (f2.get(0) == Double.POSITIVE_INFINITY) {
            double x = f2.get(1).intValue();
            int y = (int) (f1.get(0) * x + f1.get(1));

            offsetPoints.add(new Point((int) x, y));
            return;
        }
        if (f1.get(0) == Double.POSITIVE_INFINITY) {
            double x = f1.get(1).intValue();
            int y = (int) (f2.get(0) * x + f2.get(1));
            offsetPoints.add(new Point((int) x, y));
            return;
        }
        double x = ((f2.get(1) - f1.get(1)) / (f1.get(0) - f2.get(0)));
        int y = (int) (f1.get(0) * x + f1.get(1));
        offsetPoints.add(new Point((int) x, y));
        //polishOffset();
    }

    public void add(Point p) {
        if (points.size() > 2 && Math.abs(points.get(0).x - p.x) <= 4
                && Math.abs(points.get(0).y - p.y) <= 4) {
            MainPanel.setDrawBox(false);
            isDone = true;
        } else {
            if (points.size() != 1 || Math.abs(points.get(0).x - p.x) > 4
                    || Math.abs(points.get(0).y - p.y) > 4) {
                points.add(p);
                positions.add(Position.Default);
            }
        }
    }

    public void add(int idx, Point p) {
        points.add(idx, p);
        positions.add(idx, Position.Default);
    }

    public Position getPosition(int idx) {
        return positions.get(idx);
    }

    public int[] getX() {
        int[] res = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            res[i] = points.get(i).x;
        }
        return res;
    }

    // sets edge by given index vertically
    public void setVertical(int idx) {
        if ((idx == 0 && (getPosition(points.size() - 1) == Position.Vertical || getPosition(idx + 1) == Position.Vertical)) ||
                (idx > 0 && idx < points.size() - 1 && (getPosition(idx - 1) == Position.Vertical || getPosition(idx + 1) == Position.Vertical)) ||
                (idx == points.size() - 1 && (getPosition(idx - 1) == Position.Vertical || getPosition(0) == Position.Vertical)))
            return;

        positions.add(idx, Position.Vertical);
        positions.remove(idx + 1);
        if (idx + 1 != points.size()) {
            points.get(idx + 1).x = points.get(idx).x;
        } else {
            points.get(0).x = points.get(idx).x;
        }
    }

    // sets edge by given index horizontally
    public void setHorizontal(int idx) {
        if ((idx == 0 && (getPosition(points.size() - 1) == Position.Horizontal || getPosition(idx + 1) == Position.Horizontal)) ||
                (idx > 0 && idx < points.size() - 1 && (getPosition(idx - 1) == Position.Horizontal || getPosition(idx + 1) == Position.Horizontal)) ||
                (idx == points.size() - 1 && (getPosition(idx - 1) == Position.Horizontal || getPosition(0) == Position.Horizontal)))
            return;

        positions.add(idx, Position.Horizontal);
        positions.remove(idx + 1);
        if (idx + 1 != points.size()) {
            points.get(idx + 1).y = points.get(idx).y;
        } else {
            points.get(0).y = points.get(idx).y;
        }
    }

    // sets edge by given index to default
    public void setDefault(int idx) {
        positions.add(idx, Position.Default);
        positions.remove(idx + 1);
    }

    public int[] getY() {
        int[] res = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            res[i] = points.get(i).x;
        }
        return res;
    }

    public void delete(Point p) {
        int idx = points.indexOf(p);
        if (idx != -1) {
            positions.remove(idx);
        }
        points.remove(p);
    }


    // finds offset with self intersections of offset
    public HashMap<Integer, List<Point>> polishOffset() {
        List<List<Point>> rectangles = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                List<Point> temp = new ArrayList<>();
                temp.add(points.get(i));
                temp.add(points.get(i + 1));
                temp.add(offsetPoints.get(i));
                temp.add(offsetPoints.get(offsetPoints.size() - 1));
                rectangles.add(temp);
            } else {
                if (i == points.size() - 1) {
                    List<Point> temp = new ArrayList<>();
                    temp.add(points.get(i));
                    temp.add(points.get(0));
                    temp.add(offsetPoints.get(i));
                    temp.add(offsetPoints.get(i - 1));
                    rectangles.add(temp);
                } else {
                    List<Point> temp = new ArrayList<>();
                    temp.add(points.get(i));
                    temp.add(points.get(i + 1));
                    temp.add(offsetPoints.get(i));
                    temp.add(offsetPoints.get(i - 1));
                    rectangles.add(temp);
                }
            }
        }

        HashMap<Integer, List<Point>> pts = new HashMap<>();
        for (int i = 0; i < offsetPoints.size(); i++) {
            List<Point> tmp = new ArrayList<>();
            if (i == 0) {

                tmp.add(new Point(offsetPoints.get(i + 1)));
                tmp.add(new Point(offsetPoints.get(offsetPoints.size() - 1)));
            }
            if (i == offsetPoints.size() - 1) {
                tmp.add(new Point(offsetPoints.get(0)));
                tmp.add(new Point(offsetPoints.get(i - 1)));
            }
            if (i != 0 && i != offsetPoints.size() - 1) {
                tmp.add(new Point(offsetPoints.get(i + 1)));
                tmp.add(new Point(offsetPoints.get(i - 1)));
            }
            tmp.add(new Point(offsetPoints.get(i)));
            pts.put(i, tmp);
        }

        for (int i = 0; i < offsetPoints.size(); i++) {
            for (int j = 0; j < offsetPoints.size(); j++) {
                if (i == j || i == j + 1 || i == j - 1 || (i == 0 && j == offsetPoints.size() - 1) || (j == 0 && i == offsetPoints.size() - 1))
                    continue;
                int iPlus = i == offsetPoints.size() - 1 ? 0 : i + 1;
                int jPlus = j == offsetPoints.size() - 1 ? 0 : j + 1;

                if (Calculation.intersects(toDPoint(offsetPoints.get(i)), toDPoint(offsetPoints.get(iPlus)), toDPoint(offsetPoints.get(j)), toDPoint(offsetPoints.get(jPlus)))) {
                    var t = Calculation.intersectionPoint(toDPoint(offsetPoints.get(i)), toDPoint(offsetPoints.get(iPlus)), toDPoint(offsetPoints.get(j)), toDPoint(offsetPoints.get(jPlus)));
                    Point p = new Point((int) t.x, (int) t.y);
                    //add next for current
                    if (Calculation.distTo(offsetPoints.get(i).x, offsetPoints.get(i).y, p.x, p.y) < Calculation.distTo(offsetPoints.get(i).x, offsetPoints.get(i).y, pts.get(i).get(0).x, pts.get(i).get(0).y)) {
                        var s = pts.get(i);
                        pts.get(i).get(0).x = p.x;
                        pts.get(i).get(0).y = p.y;
                    }


                    //remove prev from i + 1 or add prev
                    var tmp = new ArrayList<>(offsetPoints);
                    tmp.remove(offsetPoints.get(iPlus));
                    boolean isInside = false;
                    for (int k = 0; k < rectangles.size(); k++) {
                        if (Calculation.isInside(rectangles.get(k), toDPoint(offsetPoints.get(iPlus)))
                                && !rectangles.get(k).contains(offsetPoints.get(iPlus))) {
                            isInside = true;
                            break;
                        }
                    }

                    if (isInside || Calculation.isInside(points, toDPoint(offsetPoints.get(iPlus)))) {
                        pts.get(iPlus).remove(1);
                        pts.get(iPlus).add(1, null);
                    } else {
                        var ls = pts.get(iPlus);
                        if (pts.get(iPlus).get(1) != null && Calculation.distTo(offsetPoints.get(iPlus).x, offsetPoints.get(iPlus).y, p.x, p.y) < Calculation.distTo(offsetPoints.get(iPlus).x, offsetPoints.get(iPlus).y, pts.get(iPlus).get(1).x, pts.get(iPlus).get(1).y)) {
                            pts.get(iPlus).get(1).x = p.x;
                            pts.get(iPlus).get(1).y = p.y;
                        }
                    }


                    //update next and prev for j line
                    if (Calculation.distTo(offsetPoints.get(j).x, offsetPoints.get(j).y, p.x, p.y) < Calculation.distTo(offsetPoints.get(j).x, offsetPoints.get(j).y, pts.get(j).get(0).x, pts.get(j).get(0).y)) {
                        pts.get(j).get(0).x = p.x;
                        pts.get(j).get(0).y = p.y;
                    }

                    if (pts.get(jPlus).get(1) != null && Calculation.distTo(offsetPoints.get(jPlus).x, offsetPoints.get(jPlus).y, p.x, p.y) < Calculation.distTo(offsetPoints.get(jPlus).x, offsetPoints.get(jPlus).y, pts.get(jPlus).get(1).x, pts.get(jPlus).get(1).y)) {
                        pts.get(jPlus).get(1).x = p.x;
                        pts.get(jPlus).get(1).y = p.y;
                    }

                } else {
                    var tmp = new ArrayList<>(offsetPoints);
                    tmp.remove(offsetPoints.get(i));
                    boolean isInside = false;
                    for (int k = 0; k < rectangles.size(); k++) {
                        if (Calculation.isInside(rectangles.get(k), toDPoint(offsetPoints.get(i)))
                                && !rectangles.get(k).contains(offsetPoints.get(i))) {
                            isInside = true;
                            break;
                        }
                    }
                    var tmp2 = new ArrayList<>(offsetPoints);
                    tmp2.remove(offsetPoints.get(iPlus));
                    boolean isInside2 = false;
                    for (int k = 0; k < rectangles.size(); k++) {
                        if (Calculation.isInside(rectangles.get(k), toDPoint(offsetPoints.get(iPlus)))
                                && !rectangles.get(k).contains(offsetPoints.get(iPlus))) {
                            isInside2 = true;
                            break;
                        }
                    }

                    if (isInside && isInside2 && Calculation.isInside(points, toDPoint(offsetPoints.get(iPlus))) && Calculation.isInside(points, toDPoint(offsetPoints.get(i)))) {
                        pts.get(iPlus).remove(1);
                        pts.get(iPlus).add(1, null);
                        pts.get(i).remove(1);
                        pts.get(i).add(1, null);
                        pts.get(iPlus).remove(0);
                        pts.get(iPlus).add(0, null);
                        pts.get(i).remove(0);
                        pts.get(i).add(0, null);
                    }
                }
            }
        }
        return pts;
    }

    public Point2D.Double toDPoint(Point p) {
        return new Point2D.Double(p.x, p.y);
    }
}
