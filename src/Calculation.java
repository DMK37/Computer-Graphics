import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Calculation {
    private Calculation() {
    }

    public static double distance(Point a, Point b, Point c) {
        return Math.abs((b.x - a.x) * (a.y - c.y) - (a.x - c.x) * (b.y - a.y)) /
                Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    static double distTo(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static boolean isNextToEdge(Point a, Point b, Point c) {
        return distance(a, b, c) <= 4; //&&
//                Math.min(a.x, b.x) <= c.getX() &&
//                Math.max(a.x, b.x) >= c.getX() &&
//                Math.min(a.y, b.y) <= c.getY() &&
//                Math.max(a.y, b.y) >= c.getY();
    }

    public static List<Double> findOffsetLine(List<Point> points, Point a, Point b, int offset) {
        double m = (double) (b.y - a.y) / (b.x - a.x);
        if (b.y - a.y == 0) {
            var ls = new ArrayList<Double>();
            ls.add((double) 0);
            if (!isInside(points, new Point2D.Double((double) (a.x + b.x) / 2, b.y + 2))) {
                ls.add((double) b.y + offset);
            } else {
                ls.add((double) b.y - offset);
            }
            return ls;
        }
        if (b.x - a.x == 0) {
            var ls = new ArrayList<Double>();
            ls.add(Double.POSITIVE_INFINITY);
            if (!isInside(points, new Point2D.Double(a.x  + 2, (double) (b.y + a.y) / 2))) {
                ls.add((double) b.x + offset);
            } else {
                ls.add((double) b.x - offset);
            }
            return ls;
        }

        boolean isX = false;
        boolean isPlus = false;


        double mPer = -1 / m;
        double cMain = a.y - m * a.x;

        double cPerMid = (m - mPer) * (a.x + b.x) / 2 + cMain;

        double xMid = (double) (a.x + b.x) / 2;
        double yMid = mPer * xMid + cPerMid;

        if (Math.abs(mPer) <= 1) {
            isX = true;
            double xMidT = xMid + 2;
            double yMidT = mPer * xMidT + cPerMid;
            if (!isInside(points, new Point2D.Double(xMidT, yMidT))) {
                isPlus = true;
            }
        } else {
            double yMidT = yMid + 2;
            double xMidT = ((yMidT - cPerMid) / mPer);
            if (!isInside(points, new Point2D.Double(xMidT, yMid + 2))) {
                isPlus = true;
            }
        }

        int move = 200;
        if (!isPlus)
            move = -move;
        double x1;
        double y1;
        if (isX) {

            double distX = xMid + move;
            double distY = (mPer * distX + cPerMid);
            x1 = (xMid - (offset * (xMid - distX)) / distTo(xMid, yMid, distX, distY));
        } else {
            double distY = yMid + move;
            double distX = (distY - cPerMid) / mPer;
            x1 = (xMid - (offset * (xMid - distX)) / distTo(xMid, yMid, distX, distY));
        }
        y1 = (mPer * x1 + cPerMid);

        double cParMid = y1 - m * x1;
        var ls = new ArrayList<Double>();
        ls.add(m);
        ls.add(cParMid);
        return ls;
    }

    public static boolean isInside(List<Point> points, Point2D.Double point) {
        int count = 0;
        Point2D.Double endless = new Point2D.Double(point.x, 9999999);
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D.Double a = new Point2D.Double(points.get(i).x, points.get(i).y);
            Point2D.Double b = new Point2D.Double(points.get(i + 1).x, points.get(i + 1).y);
            if (Intersects(a, b, point, endless))
                count++;
        }
        Point2D.Double a = new Point2D.Double(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
        Point2D.Double b = new Point2D.Double(points.get(0).x, points.get(0).y);
        if (Intersects(a, b, point, endless))
            count++;
        return count % 2 == 1;
    }

    private static double CrossProduct(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }

    public static boolean Intersects(Point2D.Double start1, Point2D.Double end1, Point2D.Double start2, Point2D.Double end2) {
        double d1 = CrossProduct(start1, end1, start2);
        double d2 = CrossProduct(start1, end1, end2);
        double d3 = CrossProduct(start2, end2, start1);
        double d4 = CrossProduct(start2, end2, end1);

        return (d1 * d2 < 0 && d3 * d4 < 0);
    }

    public static void bresenham(Graphics2D graphics, int x1, int y1, int x2, int y2)
    {
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int error = dx + dy;
         while(true) {
             graphics.drawRect(x1,y1,1,1);
             if(x1 == x2 && y1 == y2) {
                 break;
             }

             int e2 = 2 * error;
             if(e2 >= dy) {
                 if(x1 == x2) {
                     break;
                 }
                 error += dy;
                 x1 += sx;
             }
             if(e2 <= dx) {
                 if(y1 == y2) {
                     break;
                 }
                 error += dx;
                 y1 += sy;
             }
         }

    }
}
