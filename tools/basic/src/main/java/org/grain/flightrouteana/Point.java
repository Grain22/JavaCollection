package org.grain.flightrouteana;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * @author laowu
 */
@Data
public class Point extends Graphic {

    int id;
    Double x;
    Double y;
    boolean work = false;

    public Point() {
        super();

    }

    public Point(Double x, Double y) {
        super();

        this.id = 0;
        this.x = x;
        this.y = y;
    }

    public Point(int id, double x, double y) {
        super();

        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Point(int id, double x, double y, boolean work) {
        super();

        this.id = id;
        this.x = x;
        this.y = y;
        this.work = work;
    }

    public Point(String x, String y) {
        super();

        this.id = 0;
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
    }

    public Point(Point point, int i) {
        super();

        this.id = i;
        this.x = point.x;
        this.y = point.y;
    }

    public Point(Point point, boolean b) {
        super();

        this.id = point.id;
        this.x = point.x;
        this.y = point.y;
        this.work = b;
    }

    public boolean equals(Point a) {
        return abs(this.x - a.x) < ERROR_RANGE && abs(this.y - a.getY()) < ERROR_RANGE;
    }

    public double getDistance(Point p) {
        return sqrt(abs((this.x - p.x) * (this.x - p.x) + (this.getY() - p.getY()) * (this.getY() - p.getY())));
    }

    /**
     * 在线上
     * 精确度为 0.000000000001
     *
     * @param line
     * @return
     */
    public boolean isOnLine(Line line) {
        if (line.isStandard()) {
            return Objects.isNull(line.x) ? abs(this.y - line.y) < ERROR_RANGE : abs(this.x - line.x) < ERROR_RANGE;
        } else {
            return abs(line.getSlope() * this.x + line.getOffset() - this.y) <= ERROR_RANGE;
        }
    }

    public boolean isOnSegment(Line line) {
        if (this.isOnLine(line)) {
            return line.getCenter().getDistance(this) <= line.getLong() / 2;
        }
        return false;
    }


    /**
     * 点在圆内
     *
     * @param c
     * @return
     */
    public boolean isOnCircle(Circle c) {
        double distance = LatLngUtil.getDistance(c.o, this);
        return distance < c.radiusWithSafe;
    }

    public boolean isOnCircle(Circle c, Line line) {
        Line lv = line.getVertical(c.o);
        Line l = new Line(lv, lv.getOffset(c.radiusWithSafe), true);
        Line r = new Line(lv, lv.getOffset(c.radiusWithSafe), false);
        Line ll = lv.getVertical(c.o);
        Line t = new Line(ll, ll.getOffset(c.radiusWithSafe), true);
        Line b = new Line(ll, ll.getOffset(c.radiusWithSafe), false);
        List<Line> area = new ArrayList<>();
        area.add(l);
        area.add(r);
        area.add(t);
        area.add(b);
        return this.isOnArea(area, c.o);
    }

    public boolean isOnArea(List<Line> area, Point center) {
        for (Line line : area) {
            Line lv = line.getVertical(center);
            Line lvv = lv.getVertical(this);
            Point p1 = lv.getPoint(line);
            Point p2 = lv.getPoint(lvv);
            if (center.getDistance(p2) > center.getDistance(p1)) {
                return false;
            }
        }
        return true;
    }
}
