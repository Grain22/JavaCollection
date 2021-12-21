package org.grain.flightrouteana;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;

/**
 * @author laowu
 */
@Data
public class Line extends Graphic {

    Point a;
    Point b;
    boolean standard = false;
    Double slope;
    Double offset;
    Double x;
    Double y;

    public Line() {
        super();
    }

    public Line(Point p1, Point p2) {
        super();
        a = p1;
        b = p2;
        if (p1.x.equals(p2.x)) {
            standard = true;
            x = p1.x;
        } else if (p1.y.equals(p2.y)) {
            standard = true;
            y = p1.y;
        } else {
            standard = false;
            slope = (p1.y - p2.y) / (p1.x - p2.x);
            offset = p1.y - (slope * p1.x);
        }
    }

    public Line(Line line, Double lineOffset, boolean addOrSub) {
        super();
        if (Objects.isNull(lineOffset)) {
            lineOffset = 0.00005;
        }
        this.standard = line.isStandard();
        if (this.isStandard()) {
            if (!Objects.isNull(line.x)) {
                this.x = addOrSub ? line.x + lineOffset : line.x - lineOffset;
                this.a = new Point(this.x, line.a.y);
                this.b = new Point(this.x, line.b.y);
            } else {
                this.y = addOrSub ? line.y + lineOffset : line.y - lineOffset;
                this.a = new Point(line.a.x, this.y);
                this.b = new Point(line.b.x, this.y);
            }
        } else {
            this.slope = line.getSlope();
            this.offset = addOrSub ? line.offset + lineOffset : line.offset - lineOffset;
            this.a = this.getPoint(line.a);
            this.b = this.getPoint(line.b);
        }
    }

    public Line(Line line, Double lineOffset, int multiple, boolean ifAdd) {
        this(line, lineOffset * multiple, ifAdd);
    }

    public Line getVertical(Point p) {
        Line res = new Line();
        res.a = p;
        res.standard = this.standard;
        if (this.isStandard()) {
            if (Objects.isNull(this.x)) {
                res.setX(p.x);
                res.setB(new Point(p.x, p.y + 0.00001));
            } else {
                res.setY(p.y);
                res.setB(new Point(p.x + 0.0001, p.y));
            }
        } else {
            res.setSlope((-1) / this.getSlope());
            res.setOffset(p.y - res.getSlope() * p.x);
            res.setB(res.getPoint(p.x + 0.00001, p.y + 0.00001));
        }
        return res;
    }


    public Point getPoint(Point p) {
        return getPoint(p.x, p.y);
    }

    public Point getPoint(Double x, Double y) {
        if (this.isStandard()) {
            if (Objects.isNull(this.x) && Objects.isNull(this.y)) {
                throw new RuntimeException("线有问题");
            }
            if (Objects.isNull(this.x)) {
                return new Point(0, x, this.y);
            } else {
                return new Point(0, this.x, y);
            }
        } else {
            return !Objects.isNull(x) ? new Point(0, x, this.getSlope() * x + this.offset) : new Point(0, (y - this.offset) / this.getSlope(), y);
        }
    }

    public Point getPoint(Line l) {
        if (this.ifLineParallel(l)) {
            throw new RuntimeException();
        }
        if (this.isStandard() && l.isStandard()) {
            if (this.x == null) {
                return new Point(l.x, this.y);
            } else {
                return new Point(this.x, l.y);
            }
        } else if (this.isStandard()) {
            if (this.x == null) {
                return new Point((this.y - l.offset) / l.getSlope(), this.y);
            } else {
                return new Point(this.x, l.getSlope() * this.x + l.offset);
            }
        } else if (l.isStandard()) {
            if (l.x == null) {
                return new Point((l.y - this.offset) / this.getSlope(), l.y);
            } else {
                return new Point(l.x, this.getSlope() * l.x + this.offset);
            }
        } else {
            double x = (l.offset - this.offset) / (this.getSlope() - l.getSlope());
            double y = (this.getSlope() * l.offset - this.offset * l.getSlope()) / (this.getSlope() - l.getSlope());
            return new Point(x, y);
        }
    }


    public double getDistance(Point o) {
        if (this.isStandard()) {
            if (Objects.isNull(this.x)) {
                return abs(o.y - this.y);
            } else {
                return abs(o.x - this.x);
            }
        } else {
            return abs((this.slope * o.x - o.y + this.offset) / sqrt(this.slope * this.slope + 1));
        }
    }

    public boolean getVector(Line adjacent) {
        Point add = adjacent.getPoint(new Line(this, null, true));
        Point sub = adjacent.getPoint(new Line(this, null, false));
        Point end;
        if (this.a.equals(adjacent.a) || this.b.equals(adjacent.a)) {
            end = adjacent.b;
        } else if (this.a.equals(adjacent.b) || this.b.equals(adjacent.b)) {
            end = adjacent.a;
        } else {
            throw new RuntimeException("两条线并不相邻");
        }
        return add.getDistance(end) < sub.getDistance(end);
    }

    public List<Point> getPoints(Point p, double d) {
        List<Point> points = new ArrayList<>();
        if (this.isStandard()) {
            if (Objects.isNull(this.x)) {
                points.add(new Point(p.x + this.getOffset(d), this.y));
                points.add(new Point(p.x - this.getOffset(d), this.y));
            } else {
                points.add(new Point(this.x, p.y + this.getOffset(d)));
                points.add(new Point(this.x, p.y - this.getOffset(d)));
            }
        } else {
            Line vertical = this.getVertical(p);
            Line add = new Line(vertical, vertical.getOffset(d), true);
            Line sub = new Line(vertical, vertical.getOffset(d), false);
            points.add(this.getPoint(add));
            points.add(this.getPoint(sub));
        }
        return points;
    }

    public boolean ifLineParallel(Line b) {
        if (this.isStandard() && b.isStandard()) {
            boolean allX = Objects.isNull(this.x) && Objects.isNull(b.x);
            boolean allY = Objects.isNull(this.y) && Objects.isNull(b.y);
            if (allX || allY) {
                return true;
            }
        }
        if (!this.isStandard() && !b.isStandard()) {
            double v = abs(this.slope - b.slope);
            if (v < ERROR_RANGE_TEN) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取比例尺
     *
     * @return
     */
    public double getScale() {
        Point center = this.getCenter();
        Line vertical = this.getVertical(center);
        Point pt1 = center;
        Point pt2 = vertical.getPoint(center.getX() + 0.0001, center.getY() + 0.0001);
        double xyD = pt1.getDistance(pt2);
        double acD = LatLngUtil.getDistance(pt1, pt2);
        return xyD / acD;
    }

    /**
     * 平行线偏移量
     *
     * @param width
     * @return
     */
    public double getOffset(double width) {
        double v = this.getScale() * width;
        if (this.isStandard()) {
            /*标准线,横纵坐标距离即为线距离*/
            return v;
        } else {
            /*任意线,平行线距离计算公式倒推*/
            return abs((sqrt(this.getSlope() * this.getSlope() + 1)) * v);
        }
    }

    public Point getCenter() {
        return new Point((this.a.x + this.b.x) / 2, (this.a.y + this.b.y) / 2);
    }

    /**
     * 可能是计算一个点的某一个点相同距离的两个点
     * 不考虑拉伸扭曲情况
     *
     * @param d
     */
    private void unknown(double d) {
        Point p = this.getCenter();
        double a = pow(this.getSlope(), 2) + 1;
        double b = 2 * this.getSlope() * this.offset - 2 * this.getSlope() * p.y - 2 * p.x;
        double c = pow(p.x, 2) + pow(p.y, 2) + pow(this.offset, 2) - pow(this.getOffset(d), 2) - 2 * this.offset * p.y;
        List<Double> doubles = Equation.quadraticEquationOfOneUnknown(a, b, c);
    }

    public List<Circle> isOnObstacles(List<Circle> circles, Line lineBase) {
        List<Circle> res = new ArrayList<>();
        for (Circle circle : circles) {
            List<Line> square = circle.getSquare(lineBase);
            if (this.a.isOnArea(square, circle.o)) {
                res.add(circle);
                continue;
            }
            if (this.b.isOnArea(square, circle.o)) {
                res.add(circle);
                continue;
            }
            if (circle.isOnSegment(this)) {
                res.add(circle);
                continue;
            }
        }
        return res;
    }

    public List<Point> aroundObstacles(List<Circle> cs, Line lb, List<Line> area, Point center) {
        List<Point> res = new ArrayList<>();
        boolean addA = true;
        for (Circle c : cs) {
            if (this.a.isOnCircle(c, lb)) {
                Line lv = this.getVertical(c.o);
                double lvo = lv.getOffset(c.radiusWithSafe);
                Line lva = new Line(lv, lvo, true);
                Line lvs = new Line(lv, lvo, false);
                Point ap = this.getPoint(lva);
                Point as = this.getPoint(lvs);
                Point p = b.getDistance(ap) < b.getDistance(as) ? ap : as;
                res.add(p);
                addA = false;
                this.a = p;
            }
            if (c.isOnSegment(this)) {
                Line lv = lb.getVertical(c.o);
                double offset = lv.getOffset(c.radiusWithSafe);


            }
        }
        return res;
    }

    public List<Point> getPoints(Circle o) {
        List<Point> res = new ArrayList<>();
        if (this.isStandard()) {

        } else {
            double a = 1 + pow(this.slope, 2);
            double b = 2 * this.slope * this.offset - 2 * this.slope * o.o.y - 2 * o.o.x;
            double c = pow(o.o.x, 2) + pow((this.offset - o.o.y), 2) - pow(o.radiusWithSafe, 2);
            List<Double> xs = Equation.quadraticEquationOfOneUnknown(a, b, c);
            for (double x : xs) {
                double oa = 1;
                double ob = 0 - 2 * o.o.y;
                double oc = pow(o.o.y, 2) - pow(o.radiusWithSafe, 2) + pow(x - o.o.x, 2);
                List<Double> ys = Equation.quadraticEquationOfOneUnknown(oa, ob, oc);
                for (double y : ys) {
                    res.add(new Point(x, y));
                }
            }
        }
        return res;
    }

    public double getLong() {
        return this.a.getDistance(this.b);
    }
}