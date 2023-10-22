import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Polygon {
    private List<Point> points;

    private List<Position> positions;

    public List<Point> getPoints() {
        return points;
    }

    public List<Point> offsetPoints;

    private List<List<Double>> functions;

    private boolean isDone;

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
            var f2 = functions.get(i+1);
            if(f2.get(0) == Double.POSITIVE_INFINITY) {
                double x = f2.get(1).intValue();
                int y = (int) (f1.get(0) * x + f1.get(1));

                offsetPoints.add(new Point((int) x, y));
                continue;
            }
            if(f1.get(0) == Double.POSITIVE_INFINITY) {
                double x = f1.get(1).intValue();
                int y = (int) (f2.get(0) * x + f2.get(1));
                offsetPoints.add(new Point((int) x, y));
                continue;
            }
            double x = ((f2.get(1) - f1.get(1)) / (f1.get(0) - f2.get(0)));
            int y = (int) (f1.get(0) * x + f1.get(1));
            offsetPoints.add(new Point((int) x,y));
        }


        var f1 = functions.get(functions.size() - 1);
        var f2 = functions.get(0);

        if(f2.get(0) == Double.POSITIVE_INFINITY) {
            double x = f2.get(1).intValue();
            int y = (int) (f1.get(0) * x + f1.get(1));

            offsetPoints.add(new Point((int) x, y));
            return;
        }
        if(f1.get(0) == Double.POSITIVE_INFINITY) {
            double x = f1.get(1).intValue();
            int y = (int) (f2.get(0) * x + f2.get(1));
            offsetPoints.add(new Point((int) x, y));
            return;
        }
        double x = ((f2.get(1) - f1.get(1)) / (f1.get(0) - f2.get(0)));
        int y = (int) (f1.get(0) * x + f1.get(1));
        offsetPoints.add(new Point((int) x,y));
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

    public int[] getX()
    {
        int[] res = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            res[i] = points.get(i).x;
        }
        return res;
    }


    public void setVertical(int idx) {
        if((idx == 0 && (getPosition(points.size()-1) == Position.Vertical || getPosition(idx+1) == Position.Vertical)) ||
                (idx > 0 && idx < points.size() -1 && (getPosition(idx-1) == Position.Vertical || getPosition(idx+1) == Position.Vertical)) ||
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

    public void setHorizontal(int idx) {
        if((idx == 0 && (getPosition(points.size()-1) == Position.Horizontal || getPosition(idx+1) == Position.Horizontal)) ||
                (idx > 0 && idx < points.size() -1 && (getPosition(idx-1) == Position.Horizontal || getPosition(idx+1) == Position.Horizontal)) ||
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

    public void setDefault(int idx) {
        positions.add(idx, Position.Default);
        positions.remove(idx + 1);
    }

    public int[] getY()
    {
        int[] res = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            res[i] = points.get(i).x;
        }
        return res;
    }

    public void delete(Point p) {
        int idx = points.indexOf(p);
        if(idx != -1) {
            positions.remove(idx);
        }
        points.remove(p);
    }
}
