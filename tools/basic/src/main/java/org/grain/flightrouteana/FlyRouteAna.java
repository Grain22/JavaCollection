package org.grain.flightrouteana;

import org.grain.tools.CustomerLogger;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;

/**
 * @author laowu
 */
@SuppressWarnings("unused")
public class FlyRouteAna {
    private static final double UAV_WIDTH = 0.5;
    CustomerLogger c = CustomerLogger.getLogger(this.getClass());
    private List<Point> points;
    private Line lineBase;
    private Line lineLeft;
    private Line lineRight;
    private Line lineTowards;
    private double width;
    private Point pointA;
    private Point uavPosition;
    private boolean flyBeginA = true;
    private List<Point> beginWithStart;
    private List<Point> beginAnother;
    private Point center;
    private List<Point> defaultFlightRoute;
    private List<Point> flightRoute;
    private List<Circle> circles;
    private List<Line> area;
    private boolean leftCanBeEnd = false;
    private boolean rightCanBeEnd = false;
    private double safe;
    private int lineNumber = 0;

    public FlyRouteAna(String pointStr, String upStr, String widthStr, String safeStr, String obstacleStr) {
        /**
         *  @date 2/27/2019 2:44 PM
         *  @author laowu 外边界点
         */
        List<Point> ps = new ArrayList<>();
        String[] split = pointStr.split("-");
        for (int i = 0; i < split.length; i++) {
            String[] pi = split[i].split(",");
            Point p = new Point(i, Double.valueOf(pi[0]), Double.valueOf(pi[1]));
            ps.add(p);
        }
        points = ps;
        String s = String.valueOf(LatLngUtil.getDistance(ps.get(0), ps.get(3)));
        System.out.println(s);
        /**
         *  @date 2/27/2019 2:44 PM
         *  @author laowu 无人机位置
         */
        uavPosition = new Point(ps.get(2).x - 0.001, ps.get(3).y + 0.001);
        /**
         *  @date 2/27/2019 2:49 PM
         *  @author laowu 飞行宽度
         */
        width = Double.valueOf(widthStr);
        /**
         *  @date 2/27/2019 2:47 PM
         *  @author laowu 障碍物
         */
        List<Circle> cs = new ArrayList<>();
        if (obstacleStr.isEmpty()) {
            circles = cs;
        } else {
            Arrays.stream(obstacleStr.split("-")).forEach(a -> cs.add(new Circle(new Point(a.split(",")[0], a.split(",")[1]), Double.valueOf(a.split(",")[2]), Double.valueOf(a.split(",")[3]))));
            circles = cs;
        }
        safe = Double.valueOf(safeStr) < UAV_WIDTH ? Double.valueOf(safeStr) : Double.valueOf(safeStr) + UAV_WIDTH;

    }


    public String getResult() {
        getCenter();
        getLineBase();
        getPeriphery();
        resetLinesAndPoints();
        resetWidth();
        getCenter();
        getLineBase();
        getPeriphery();
        getBeginPoint();
        getPeripheryPoints();
        getDefaultFlyLines();
        if (circles.isEmpty()) {
            flightRoute = defaultFlightRoute;
        } else {
            getFlightGoAroundObstacles();
        }
        if (Objects.isNull(flightRoute)) {
            flightRoute = defaultFlightRoute;
        }
        setWorkStatus();
        String data = data();
        return data;
    }

    private String data() {
        for (int i = 0; i + 6 < flightRoute.size(); i++) {
            double distance = LatLngUtil.getDistance(flightRoute.get(i), flightRoute.get(i + 3));
        }
        flightRoute.add(0, uavPosition);
        if (flightRoute.isEmpty()) {
            throw new RuntimeException("");
        }
        StringJoiner joiner = new StringJoiner("-");
        for (Point a : flightRoute) {
            if (abs(a.getX()) > 150 || abs(a.getY()) > 150) {
                break;
            }
            String s = "" + a.getX() + "," + a.getY() + "," + (a.isWork() ? 1 : 0);
            joiner.add(s);
        }
        String string = joiner.toString();
        return string;
    }

    private void resetWidth() {
        double distance = 0;
        boolean f = false;
        Point p = null;
        Line lv;
        Point lvp = null;
        double scale = lineBase.getScale();
        for (Point point : points) {
            if (lineBase.getDistance(point) != 0) {
                lv = lineBase.getVertical(point);
                lvp = lv.getPoint(lineBase);
                double acD = lvp.getDistance(point) / scale;
                if (acD > distance) {
                    distance = acD;
                    double v2 = acD % width;
                    if (v2 > 0.2) {
                        p = point;
                        f = true;
                    } else {
                        f = false;
                    }
                }
            }
        }
        if (f) {
            lv = lineBase.getVertical(p);
            lvp = lv.getPoint(lineBase);
            distance = lvp.getDistance(p) / scale;
            lineNumber = (int) ceil(distance / width);
            width = distance / lineNumber;
        }
    }

    private void setWorkStatus() {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < defaultFlightRoute.size(); i = i + 2) {
            lines.add(new Line(defaultFlightRoute.get(i), defaultFlightRoute.get(i + 1)));
        }
        List<Point> res = new ArrayList<>();
        flag:
        for (int i = 0; i < flightRoute.size(); i++) {
            c.log(i);
            if ((i + 1) == flightRoute.size()) {
                res.add(new Point(flightRoute.get(i), false));
                break;
            }
            for (Line line : lines) {
                if (flightRoute.get(i).isOnLine(line) && flightRoute.get(i + 1).isOnLine(line)) {
                    res.add(new Point(flightRoute.get(i), true));
                    continue flag;
                }
            }
            res.add(new Point(flightRoute.get(i), false));
        }
        flightRoute = res;
    }

    @SuppressWarnings("all")
    private void getFlightRoute() {
        /*路径点转化飞行线*/
        List<Line> flyRoutes = new ArrayList<>();
        for (int i = 0; i + 1 < defaultFlightRoute.size(); i++) {
            flyRoutes.add(new Line(defaultFlightRoute.get(i), defaultFlightRoute.get(i + 1)));
        }
        /*结果集*/
        List<Point> result = new ArrayList<>();
        for (Line l : flyRoutes) {
            List<Circle> onObstacles = l.isOnObstacles(circles, lineBase);
            if (!onObstacles.isEmpty()) {
                sortCircle(onObstacles, l.a);
                List<Point> nps = l.aroundObstacles(onObstacles, lineBase, area, center);
                if (!result.isEmpty()) {
                    result.remove(result.size() - 1);
                }
                nps.forEach(a -> result.add(a));
            } else {
                if (result.isEmpty()) {
                    result.add(l.a);
                }
                result.add(l.b);
            }
        }

    }

    private void getFlightGoAroundObstacles() {
        /*结果集*/
        List<Point> result = new ArrayList<>();
        try {
            Line l;
            Point a;
            Point b;
            int i = 0;
            theLastPoint:
            while (true) {
                boolean addA;
                boolean addB = true;
                if (i + 1 == defaultFlightRoute.size()) {
                    break;
                }
                if (result.isEmpty()) {
                    addA = true;
                    a = defaultFlightRoute.get(i);
                } else {
                    a = result.get(result.size() - 1);
                    addA = false;
                }
                b = defaultFlightRoute.get(i + 1);
                l = new Line(a, b);
                sortCircle(circles, a);
                for (Circle c : circles) {
                    /*根据顺序验证是否有圆为本条路径上的障碍*/
                    if (c.isOnSegment(l) || b.isOnCircle(c, lineBase)) {
                        /*如果有*/
                        if (a.isOnCircle(c)) {
                            /*起点在障碍上*/
                            Line lv = lineBase.getVertical(c.o);
                            double lvo = lv.getOffset(c.radiusWithSafe + 0.1);
                            Line add = new Line(lv, lvo, true);
                            Line sub = new Line(lv, lvo, false);
                            Line use = add.getPoint(l).getDistance(b) < sub.getPoint(l).getDistance(b) ? add : sub;
                            a = use.getPoint(l);
                            result.add(a);
                            addA = false;
                            l = new Line(a, b);
                        }
                        if (b.isOnCircle(c, lineBase)) {
                            /*终点在障碍上*/
                            Line lv = lineBase.getVertical(c.o);
                            double lvo = lv.getOffset(c.radiusWithSafe);
                            Line lva = new Line(lv, lvo, true);
                            Line lvs = new Line(lv, lvo, false);
                            Line lvu = null;
                            if (i + 2 == defaultFlightRoute.size()) {
                                /*终点是最后一个点,不管了*/
                                lvu = lva.getPoint(l).getDistance(a) < lvs.getPoint(l).getDistance(a) ? lva : lvs;
                                b = lvu.getPoint(l);
                                result.add(b);
                                break theLastPoint;
                            } else {
                                Point i2 = defaultFlightRoute.get(i + 2);
                                Point i3 = defaultFlightRoute.get(i + 3);
                                if (defaultFlightRoute.get(i + 2).isOnCircle(c)) {
                                    /*如果后续点在圆内*/
                                    lvu = lva.getPoint(l).getDistance(a) < lvs.getPoint(l).getDistance(a) ? lva : lvs;
                                    b = lvu.getPoint(l);
                                    result.add(b);
                                    result.add(lvu.getPoint(new Line(i2, i3)));
                                    i += 2;
                                    continue theLastPoint;
                                } else {
                                    Line compare = new Line(b, i2);
                                    if (l.ifLineParallel(lineBase)) {
                                        lvu = lva.getPoint(l).getDistance(a) < lvs.getPoint(l).getDistance(a) ? lva : lvs;
                                        Line lvv = lv.getVertical(c.o);
                                        double lvvo = lvv.getOffset(c.radiusWithSafe);
                                        Line lvva = new Line(lvv, lvvo, true);
                                        Line lvvs = new Line(lvv, lvvo, false);
                                        Line lvvu = lvva.getPoint(lv).getDistance(i2) < lvvs.getPoint(lv).getDistance(i2) ? lvva : lvvs;
                                        result.add(lvu.getPoint(l));
                                        if (lvvu.getPoint(lvu).isOnArea(area, center)) {
                                            result.add(lvvu.getPoint(lvu));
                                        }
                                        if (lvvu.getPoint(compare).isOnSegment(compare)) {
                                            result.add(lvvu.getPoint(compare));
                                        }
                                        i += 1;
                                        continue theLastPoint;
                                    } else {
                                        Line lvv = lv.getVertical(c.o);
                                        double lvvo = lvv.getOffset(c.radiusWithSafe);
                                        Line lvva = new Line(lvv, lvvo, true);
                                        Line lvvs = new Line(lvv, lvvo, false);
                                        Line lvvu = lvva.getPoint(l).getDistance(a) < lvvs.getPoint(l).getDistance(a) ? lvva : lvvs;
                                        Point lvap = lva.getPoint(lvvu);
                                        Point lvsp = lvs.getPoint(lvvu);
                                        boolean aa = lva.getPoint(lvva).isOnArea(area, center);
                                        boolean as = lva.getPoint(lvvs).isOnArea(area, center);
                                        boolean sa = lvs.getPoint(lvva).isOnArea(area, center);
                                        boolean ss = lvs.getPoint(lvvs).isOnArea(area, center);
                                        lvu = lvap.getDistance(center) < lvsp.getDistance(center) ? lva : lvs;
                                        if (aa && as && sa && ss) {
                                        } else if (aa && sa) {
                                            lvvu = lvva;
                                        } else {
                                            lvvu = lvvs;
                                        }
                                        if (lvvu.getPoint(l).isOnSegment(l)) {
                                            result.add(lvvu.getPoint(l));
                                            result.add(lvvu.getPoint(lvu));
                                            result.add(lvu.getPoint(compare));
                                        } else {
                                            result.add(lva.getDistance(a) < lvs.getDistance(a) ? lva.getPoint(l) : lvs.getPoint(l));
                                            if (aa && as && sa && ss) {

                                            } else if (aa && sa) {
                                                lvvu = lvva;
                                            } else {
                                                lvvu = lvvs;
                                            }
                                            result.add(lva.getDistance(a) < lvs.getDistance(a) ? lva.getPoint(lvvu) : lvs.getPoint(lvvu));
                                            result.add(lva.getDistance(a) < lvs.getDistance(a) ? lvs.getPoint(lvvu) : lva.getPoint(lvvu));
                                            result.add(lva.getDistance(a) < lvs.getDistance(a) ? lvs.getPoint(compare) : lva.getPoint(compare));
                                        }
                                        i += 1;
                                        continue theLastPoint;
                                    }
                                }
                            }
                        }
                        if (c.isOnLine(l)) {
                            /*障碍在中间*/
                            if (addA) {
                                result.add(a);
                                addA = false;
                            }
                            Line lv = lineBase.getVertical(c.o);
                            double lvo = lv.getOffset(c.radiusWithSafe);
                            Line add = new Line(lv, lvo, true);
                            Line sub = new Line(lv, lvo, false);
                            Line lvv = lv.getVertical(c.o);
                            double lvvo = lvv.getOffset(c.radiusWithSafe);
                            Line top = new Line(lvv, lvvo, true);
                            Line bot = new Line(lvv, lvvo, false);
                            Line ul1 = null;
                            Line ul2 = null;
                            if (add.getPoint(l).getDistance(a) < sub.getPoint(l).getDistance(a)) {
                                ul1 = add;
                                ul2 = sub;
                            } else {
                                ul1 = sub;
                                ul2 = add;
                            }
                            Line lu = null;
                            Point t1 = top.getPoint(ul1);
                            Point t2 = bot.getPoint(ul1);
                            if (t1.isOnArea(area, center) && t2.isOnArea(area, center)) {
                                lu = l.getDistance(t1) < l.getDistance(t2) ? top : bot;
                            } else {
                                lu = t1.getDistance(center) < t2.getDistance(center) ? top : bot;
                            }
                            result.add(ul1.getPoint(l));
                            result.add(ul1.getPoint(lu));
                            result.add(ul2.getPoint(lu));
                            result.add(ul2.getPoint(l));
                            a = ul2.getPoint(l);
                            l = new Line(a, b);
                        }
                    }
                }
                if (addA) {
                    result.add(a);
                }
                if (addB) {
                    result.add(b);
                }
                i++;
            }
            flightRoute = result;
        } catch (Exception e) {
            flightRoute = result;
        }
    }

    private void sortCircle(List<Circle> circles, Point a) {
        circles.forEach(c -> c.setDistance(LatLngUtil.getDistance(c.getO(), a)));
        circles.sort((o1, o2) -> o1.getDistance() <= o2.getDistance() ? -1 : 1);
    }

    private void getDefaultFlyLines() {
        int start = beginWithStart.size();
        int another = beginAnother.size();
        if (start != another) {
            if (leftCanBeEnd || rightCanBeEnd) {
                if (start > another) {
                    beginWithStart.remove(start - 1);
                } else {
                    beginAnother.remove(another - 1);
                }
            }
        }
        List<Point> line = new ArrayList<>();
        for (int i = 0; i < start; i++) {
            c.log(i);
            if (i % 2 == 0) {
                line.add(beginWithStart.get(i));
                line.add(beginAnother.get(i));
            } else {
                line.add(beginAnother.get(i));
                line.add(beginWithStart.get(i));
            }

        }
        defaultFlightRoute = line;
    }

    private void getPeripheryPoints() {
        boolean ifLeftAdd = lineBase.getVector(lineLeft);
        boolean ifRightAdd = lineBase.getVector(lineRight);
        int leftPointNum = getNumberCanPoint(lineBase, lineLeft, ifLeftAdd);
        int rightPointNum = getNumberCanPoint(lineBase, lineRight, ifLeftAdd);
        if (lineNumber != 0) {
            if (leftPointNum > lineNumber) {
                leftPointNum = lineNumber;
            }
            if (rightPointNum > lineNumber) {
                rightPointNum = lineNumber;
            }
        }
        boolean towards = false;
        if (abs(leftPointNum - rightPointNum) == 1) {
            if (leftCanBeEnd && rightCanBeEnd) {
            } else {
                towards = true;
            }
        } else if (abs(leftPointNum - rightPointNum) > 1) {
            towards = true;
        }
        List<Point> left = new ArrayList<>();
        List<Point> right = new ArrayList<>();
        for (int i = 0; i < leftPointNum; i++) {
            c.log(i);
            left.add(lineLeft.getPoint(new Line(lineBase, lineBase.getOffset(width) * (i + 0.5), ifLeftAdd)));
        }
        for (int i = 0; i < rightPointNum; i++) {
            c.log(i);
            right.add(lineRight.getPoint(new Line(lineBase, lineBase.getOffset(width) * (i + 0.5), ifRightAdd)));
        }
        if (towards) {
            if (leftPointNum < rightPointNum) {
                for (int i = leftPointNum; i < rightPointNum; i++) {
                    left.add(lineTowards.getPoint(new Line(lineBase, lineBase.getOffset(width) * (i + 0.5), ifLeftAdd)));
                }
            } else {
                for (int i = rightPointNum; i < leftPointNum; i++) {
                    right.add(lineTowards.getPoint(new Line(lineBase, lineBase.getOffset(width) * (i + 0.5), ifLeftAdd)));
                }
            }
        }
        if (flyBeginA) {
            beginWithStart = left;
            beginAnother = right;
        } else {
            beginWithStart = right;
            beginAnother = left;
        }

    }

    private int getNumberCanPoint(Line line, Line neighbour, boolean ifAdd) {
        double neighbourLong = neighbour.getA().getDistance(neighbour.getB());
        double adjacentDistance;
        Point intersectionPoint = neighbour.getPoint(new Line(line, line.getOffset(width), ifAdd));
        if (line.getA().equals(neighbour.getA()) || line.getB().equals(neighbour.getA())) {
            adjacentDistance = neighbour.getA().getDistance(intersectionPoint);
            if (neighbourLong / adjacentDistance == 0) {
                if (line.getA().equals(neighbour.getA())) {
                    leftCanBeEnd = true;
                } else {
                    rightCanBeEnd = true;
                }
            }
            return (int) Math.round(neighbourLong / adjacentDistance);
        } else if (line.getA().equals(neighbour.getB()) || line.getB().equals(neighbour.getB())) {
            adjacentDistance = neighbour.getB().getDistance(intersectionPoint);
            if (neighbourLong / adjacentDistance == 0) {
                if (line.getA().equals(neighbour.getB())) {
                    leftCanBeEnd = true;
                } else {
                    rightCanBeEnd = true;
                }
            }
            return (int) Math.round(neighbourLong / adjacentDistance);
        } else {
            throw new RuntimeException("两条线并不相邻");
        }
    }

    private void getBeginPoint() {
        flyBeginA = LatLngUtil.getDistance(lineBase.getA(), uavPosition) < LatLngUtil.getDistance(lineBase.getB(), uavPosition);
    }

    private void resetLinesAndPoints() {
        Line lba = new Line(lineBase, lineBase.getOffset(safe), true);
        Line lbs = new Line(lineBase, lineBase.getOffset(safe), false);
        Line lbo = lineBase;
        lineBase = lba.getDistance(center) < lbs.getDistance(center) ? lba : lbs;
        Line lla = new Line(lineLeft, lineLeft.getOffset(safe), true);
        Line lls = new Line(lineLeft, lineLeft.getOffset(safe), false);
        Line llo = lineLeft;
        lineLeft = lla.getDistance(center) < lls.getDistance(center) ? lla : lls;
        Line lra = new Line(lineRight, lineRight.getOffset(safe), true);
        Line lrs = new Line(lineRight, lineRight.getOffset(safe), false);
        Line lro = lineRight;
        lineRight = lra.getDistance(center) < lrs.getDistance(center) ? lra : lrs;
        Line lta = new Line(lineTowards, lineTowards.getOffset(safe), true);
        Line lts = new Line(lineTowards, lineTowards.getOffset(safe), false);
        Line lto = lineTowards;
        lineTowards = lta.getDistance(center) < lts.getDistance(center) ? lta : lts;
        Point[] res = new Point[4];
        for (int i = 0; i < points.size(); i++) {
            Point tem = points.get(i);
            if (lbo.getA().equals(llo.getA())) {
                if (lbo.getA().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineLeft), i);
                }
            }
            if (lbo.getA().equals(llo.getB())) {
                if (lbo.getA().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineLeft), i);
                }
            }
            if (lbo.getB().equals(llo.getA())) {
                if (lbo.getB().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineLeft), i);
                }
            }
            if (lbo.getB().equals(llo.getB())) {
                if (lbo.getB().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineLeft), i);
                }
            }
            if (lbo.getA().equals(lro.getA())) {
                if (lbo.getA().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineRight), i);
                }
            }
            if (lbo.getA().equals(lro.getB())) {
                if (lbo.getA().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineRight), i);
                }
            }
            if (lbo.getB().equals(lro.getA())) {
                if (lbo.getB().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineRight), i);
                }
            }
            if (lbo.getB().equals(lro.getB())) {
                if (lbo.getB().equals(tem)) {
                    res[i] = new Point(lineBase.getPoint(lineRight), i);
                }
            }
            if (lto.getA().equals(llo.getA())) {
                if (lto.getA().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineLeft), i);
                }
            }
            if (lto.getA().equals(llo.getB())) {
                if (lto.getA().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineLeft), i);
                }
            }
            if (lto.getB().equals(llo.getA())) {
                if (lto.getB().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineLeft), i);
                }
            }
            if (lto.getB().equals(llo.getB())) {
                if (lto.getB().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineLeft), i);
                }
            }
            if (lto.getA().equals(lro.getA())) {
                if (lto.getA().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineRight), i);
                }
            }
            if (lto.getA().equals(lro.getB())) {
                if (lto.getA().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineRight), i);
                }
            }
            if (lto.getB().equals(lro.getA())) {
                if (lto.getB().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineRight), i);
                }
            }
            if (lto.getB().equals(lro.getB())) {
                if (lto.getB().equals(tem)) {
                    res[i] = new Point(lineTowards.getPoint(lineRight), i);
                }
            }
        }
        points = Arrays.asList(res);
    }

    private void getPeriphery() {
        int bid;
        if (lineBase.a.equals(pointA)) {
            bid = lineBase.b.getId();
        } else {
            bid = lineBase.a.id;
        }
        if (pointA.id == 0 && bid == 3) {
            lineLeft = new Line(points.get(1), points.get(0));
            lineRight = new Line(points.get(3), points.get(2));
            lineTowards = new Line(points.get(1), points.get(2));
        } else if (pointA.id == 0 && bid == 1) {
            lineLeft = new Line(points.get(0), points.get(3));
            lineRight = new Line(points.get(1), points.get(2));
            lineTowards = new Line(points.get(3), points.get(2));
        } else if (pointA.id == 3 && bid == 0) {
            lineLeft = new Line(points.get(3), points.get(2));
            lineRight = new Line(points.get(0), points.get(1));
            lineTowards = new Line(points.get(1), points.get(2));
        } else if (pointA.id == 3 && bid == 2) {
            lineLeft = new Line(points.get(3), points.get(0));
            lineRight = new Line(points.get(2), points.get(1));
            lineTowards = new Line(points.get(1), points.get(0));
        } else if (pointA.id == 1 && bid == 0) {
            lineLeft = new Line(points.get(1), points.get(2));
            lineRight = new Line(points.get(0), points.get(3));
            lineTowards = new Line(points.get(3), points.get(2));
        } else if (pointA.id == 1 && bid == 2) {
            lineLeft = new Line(points.get(1), points.get(0));
            lineRight = new Line(points.get(2), points.get(3));
            lineTowards = new Line(points.get(0), points.get(3));
        } else if (pointA.id == 2 && bid == 1) {
            lineLeft = new Line(points.get(2), points.get(3));
            lineRight = new Line(points.get(0), points.get(1));
            lineTowards = new Line(points.get(3), points.get(0));
        } else if (pointA.id == 2 && bid == 3) {
            lineLeft = new Line(points.get(2), points.get(1));
            lineRight = new Line(points.get(3), points.get(0));
            lineTowards = new Line(points.get(1), points.get(0));
        } else {
            throw new RuntimeException();
        }
        List<Line> areaLine = new ArrayList<>();
        areaLine.add(lineBase);
        areaLine.add(lineLeft);
        areaLine.add(lineRight);
        areaLine.add(lineTowards);
        area = areaLine;
    }

    private void getLineBase() {
        double distance = 999999999;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getDistance(uavPosition) < distance) {
                distance = points.get(i).getDistance(uavPosition);
                pointA = points.get(i);
            }
        }
        switch (pointA.getId()) {
            case 0:
                lineBase = LatLngUtil.getDistance(points.get(0), points.get(3)) > LatLngUtil.getDistance(points.get(0), points.get(1)) ? new Line(points.get(0), points.get(3)) : new Line(points.get(0), points.get(1));
                break;
            case 1:
                lineBase = LatLngUtil.getDistance(points.get(1), points.get(0)) > LatLngUtil.getDistance(points.get(1), points.get(2)) ? new Line(points.get(1), points.get(0)) : new Line(points.get(1), points.get(2));
                break;
            case 2:
                lineBase = LatLngUtil.getDistance(points.get(2), points.get(1)) > LatLngUtil.getDistance(points.get(2), points.get(3)) ? new Line(points.get(2), points.get(1)) : new Line(points.get(2), points.get(3));
                break;
            case 3:
                lineBase = LatLngUtil.getDistance(points.get(3), points.get(2)) > LatLngUtil.getDistance(points.get(3), points.get(0)) ? new Line(points.get(3), points.get(2)) : new Line(points.get(3), points.get(0));
                break;
            default:
                throw new RuntimeException("");
        }
    }

    private void getCenter() {
        center = new Point(points.stream().mapToDouble(Point::getX).sum() / 4, points.stream().mapToDouble(Point::getY).sum() / 4);
    }
}
