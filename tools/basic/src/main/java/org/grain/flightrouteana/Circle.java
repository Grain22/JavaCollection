package org.grain.flightrouteana;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

/**
 * @author laowu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Circle extends Graphic {
    Point o;
    /**
     * 2/27/2019 实际距离半径
     */
    double radius;
    /**
     * 2/27/2019 实际安全距离,含半径
     */
    double radiusWithSafe;
    double distance;

    public Circle() {
        super();

    }

    public Circle(Point point, String radiusStr) {
        super();

        this.o = point;
        this.radius = Double.valueOf(radiusStr);
    }

    public Circle(Point point, double radius) {
        super();

        this.o = point;
        this.radius = radius;
    }

    public Circle(Point point, double radius, double safe) {
        super();

        this.o = point;
        this.radius = radius;
        this.radiusWithSafe = radius + safe;
    }

    public boolean isOnLine(Line line) {
        Line lv = line.getVertical(this.o);
        Line lvv = lv.getVertical(this.o);
        double lvvo = lvv.getOffset(this.radiusWithSafe);
        Point p1 = new Line(lvv, lvvo, true).getPoint(lv);
        Point p2 = line.getPoint(lv);
        return p2.getDistance(this.o) < this.o.getDistance(p1);
    }

    public boolean isOnSegment(Line line) {
        if (!this.isOnLine(line)) {
            return false;
        }
        Line lv = line.getVertical(this.o);
        Point point = lv.getPoint(line);
        double d = line.a.getDistance(line.b);
        double da = line.a.getDistance(point);
        double db = line.b.getDistance(point);
        return abs(da + db - d) < ERROR_RANGE;
    }

    public List<Line> getSquare(Line lineBase) {
        Line lv = lineBase.getVertical(this.o);
        Line lvv = lv.getVertical(this.o);
        Line lva = new Line(lv, lv.getOffset(this.radiusWithSafe), true);
        Line lvs = new Line(lv, lv.getOffset(this.radiusWithSafe), false);
        Line lvva = new Line(lvv, lvv.getOffset(this.radiusWithSafe), true);
        Line lvvs = new Line(lvv, lvv.getOffset(this.radiusWithSafe), false);
        List<Line> res = new ArrayList<>();
        res.add(lva);
        res.add(lvs);
        res.add(lvva);
        res.add(lvvs);
        return res;
    }

    public List<Point> getPoints(Line l) {
        double m = this.o.x;
        double n = this.o.y;
        double r = this.radiusWithSafe;
        double k = l.slope;
        double b = l.offset;
        double aX = 1 + k * k;
        double bX = 2 * k * (b - n) - 2 * m;
        double cX = m * m + (b - n) * (b - n) - r * r;
        List<Double> xArr = Equation.quadraticEquationOfOneUnknown(aX, bX, cX);
        List<Point> collect = xArr.stream().map(x -> new Point(x, k * x + b)).collect(Collectors.toList());
        return collect;
    }
}
