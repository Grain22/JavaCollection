package org.grain.flightrouteana;

import java.util.ArrayList;
import java.util.List;

/**
 * @author laowu
 */
public class Equation {
    public static List<Double> quadraticEquationOfOneUnknown(double a, double b, double c) {
        List<Double> res = new ArrayList<>();
        double v = Math.pow(b, 2) - (4 * a * c);
        if (v > 0) {
            res.add(0 - b + Math.sqrt(v) / (2 * a));
            res.add((0 - b - Math.sqrt(v)) / (2 * a));
        } else if (v == 0) {
            res.add((0 - b) / (2 * a));
        }
        return res;
    }
}
