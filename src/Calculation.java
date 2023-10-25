import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;

public class Calculation {
    private Calculation() {
    }

    // distance from line a-b to point c
    public static double distance(Point a, Point b, Point c) {
        return Math.abs((b.x - a.x) * (a.y - c.y) - (a.x - c.x) * (b.y - a.y)) /
                Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    // distance between two points
    static double distTo(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    // checks if point c next to line a-b
    public static boolean isNextToEdge(Point a, Point b, Point c) {
        if (distance(a, b, c) > 4)
            return false;

        double m = (double) (b.y - a.y) / (b.x - a.x);
        if (b.y - a.y == 0) {
            return Math.min(a.x, b.x) <= c.getX() && Math.max(a.x, b.x) >= c.getX();
        }
        if (b.x - a.x == 0) {
            return Math.min(a.y, b.y) <= c.getY() && Math.max(a.y, b.y) >= c.getY();
        }

        if(a.x > b.x) {
            var t = b;
            b = a;
            a = t;
        }


        double mPer = -1 / m;
        double cMain = a.y - m * a.x;



        double cPerA = (m - mPer) * a.x + cMain;
        double cPerB = (m - mPer) * b.x + cMain;
        int step = 40;
        Point a1 = new Point(a.x + step, (int) (mPer * (a.x + step) + cPerA));
        Point b1 = new Point(b.x + step, (int) (mPer * (b.x + step) + cPerB));
            if((isLeft(a, a1, c) && !isLeft(b, b1, c))) {
                return true;
            }
            a1 = new Point(a.x - step, (int) (mPer * (a.x - step) + cPerA));
            b1 = new Point(b.x - step, (int) (mPer * (b.x - step) + cPerB));
            return isLeft(a, a1, c) && !isLeft(b, b1, c);
    }

    public static boolean isLeft(Point a, Point b, Point c) {
        return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0;
    }


    // finds equation of offset line for line a-b
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


    // checks if given point is inside given polygon
    public static boolean isInside(List<Point> points, Point2D.Double point) {
        int count = 0;
        Point2D.Double endless = new Point2D.Double(point.x, 9999999);
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D.Double a = new Point2D.Double(points.get(i).x, points.get(i).y);
            Point2D.Double b = new Point2D.Double(points.get(i + 1).x, points.get(i + 1).y);
            if (intersects(a, b, point, endless))
                count++;
        }
        Point2D.Double a = new Point2D.Double(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
        Point2D.Double b = new Point2D.Double(points.get(0).x, points.get(0).y);
        if (intersects(a, b, point, endless))
            count++;
        return count % 2 == 1;
    }

    private static double crossProduct(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }

    public static boolean intersects(Point2D.Double start1, Point2D.Double end1, Point2D.Double start2, Point2D.Double end2) {
        double d1 = crossProduct(start1, end1, start2);
        double d2 = crossProduct(start1, end1, end2);
        double d3 = crossProduct(start2, end2, start1);
        double d4 = crossProduct(start2, end2, end1);

        return (d1 * d2 < 0 && d3 * d4 < 0);
    }

    public static void bresenham(Graphics2D graphics, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int error = dx + dy;
        while (true) {
            graphics.drawRect(x1, y1, 1, 1);
            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * error;
            if (e2 >= dy) {
                if (x1 == x2) {
                    break;
                }
                error += dy;
                x1 += sx;
            }
            if (e2 <= dx) {
                if (y1 == y2) {
                    break;
                }
                error += dx;
                y1 += sy;
            }
        }
    }

    static int  ipart(double x) {
        return (int) x;
    }

    static double fpart(double x) {
        return x - Math.floor(x);
    }

    static double rfpart(double x) {
        return 1.0 - fpart(x);
    }

    static void plot(Graphics2D g, double x, double y, double c, int r, int gr, int b) {
        g.setColor(new Color(r/256f, gr/256f, b/256f, (float)c));
        g.fillOval((int) x, (int) y, 2, 2);
    }

    public static Point2D.Double intersectionPoint(Point2D.Double a1, Point2D.Double b1, Point2D.Double a2, Point2D.Double b2) {
        double m1 = (b1.y - a1.y) / (b1.x - a1.x);
        double c1 = a1.y - m1 * a1.x;

        double m2 = (b2.y - a2.y) / (b2.x - a2.x);
        double c2 = a2.y - m2 * a2.x;

        double x = (c2 - c1) / (m1 - m2);
        double y = m1 * x + c1;
        return new Point2D.Double(x,y);
    }

    public static void WuAlgo(Graphics2D graphics, double x0, double y0, double x1, double y1,int r, int g, int b) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep)
        {
            double t = x0;
            x0 = y0;
            y0 = t;
            t = x1;
            x1 = y1;
            y1 = t;
        }
        if(x0 > x1) {
            double t = x0;
            x0 = x1;
            x1 = t;
            t = y0;
            y0 = y1;
            y1 = t;
        }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;

        double xend = Math.round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = rfpart(x0 + 0.5);
        double xpxl1 = xend;
        double ypxl1 = ipart(yend);

        if (steep) {
            plot(graphics, ypxl1, xpxl1, rfpart(yend) * xgap, r, g, b);
            plot(graphics, ypxl1 + 1, xpxl1, fpart(yend) * xgap, r, g, b);
        } else {
            plot(graphics, xpxl1, ypxl1, rfpart(yend) * xgap, r, g, b);
            plot(graphics, xpxl1, ypxl1 + 1, fpart(yend) * xgap, r, g, b);
        }

        double intery = yend + gradient;
        xend = Math.round(x1);
        yend = y1 + gradient * (xend - x1);
        xgap = fpart(x1 + 0.5);
        double xpxl2 = xend;
        double ypxl2 = ipart(yend);

        if (steep) {
            plot(graphics, ypxl2, xpxl2, rfpart(yend) * xgap, r, g, b);
            plot(graphics, ypxl2 + 1, xpxl2, fpart(yend) * xgap, r, g, b);
        } else {
            plot(graphics, xpxl2, ypxl2, rfpart(yend) * xgap, r, g, b);
            plot(graphics, xpxl2, ypxl2 + 1, fpart(yend) * xgap, r, g, b);
        }
        for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(graphics, ipart(intery), x, rfpart(intery), r, g, b);
                plot(graphics, ipart(intery) + 1, x, fpart(intery), r, g, b);
            } else {
                plot(graphics, x, ipart(intery), rfpart(intery), r, g, b);
                plot(graphics, x, ipart(intery) + 1, fpart(intery), r, g, b);
            }
            intery = intery + gradient;
        }
    }
}
