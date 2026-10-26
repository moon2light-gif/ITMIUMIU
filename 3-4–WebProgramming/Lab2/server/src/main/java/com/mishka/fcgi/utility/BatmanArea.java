package com.mishka.fcgi.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code BatmanArea} содержит метод проверки попадания точки в область бетмена
 * @author mikhailramar
 */
public final class BatmanArea {

    // Каждую кривую приходится резать на 60 прямых отрезков (можно было больше, но и так многовато)
    private static final int STEPS = 60;
    private static final double[][] POLY = buildPolygon();

    private BatmanArea() {}

    public static boolean contains(double x, double y, double r) {
        double px = x / r;
        double py = y / r;

        boolean inside = false;
        for (int i = 0, j = POLY.length - 1; i < POLY.length; j = i++) {
            double xi = POLY[i][0], yi = POLY[i][1];
            double xj = POLY[j][0], yj = POLY[j][1];

            // Луч вправо от точки пересекает ребро (j -> i)?
            if ((yi > py) != (yj > py)
                    && px < (xj - xi) * (py - yi) / (yj - yi) + xi) {
                inside = !inside;
            }
        }
        return inside;
    }

    private static double[][] buildPolygon() {
        List<double[]> p = new ArrayList<>();
        p.add(new double[]{1 / 16.0, 3 / 8.0});

        // Голова и левое ухо
        line(p, -1 / 16.0, 3 / 8.0);
        line(p, -1 / 8.0, 1 / 2.0);
        line(p, -3 / 16.0, 3 / 16.0);

        // Верхняя левая выемка
        bezier(p, -5 / 16.0, 3 / 32.0, -1 / 2.0, 3 / 16.0, -1 / 2.0, 1 / 2.0);
        // Левое крыло
        bezier(p, -7 / 6.0, 1 / 4.0, -7 / 6.0, -1 / 4.0, -1 / 2.0, -1 / 2.0);
        // Две нижние выемки (левая половина)
        bezier(p, -1 / 2.0, -7 / 32.0, -9 / 32.0, -7 / 32.0, -1 / 4.0, -7 / 16.0);
        bezier(p, -1 / 4.0, -1 / 4.0, -1 / 16.0, -1 / 4.0, 0, -1 / 2.0);

        // Правая половина (зеркальная)
        bezier(p, 1 / 16.0, -1 / 4.0, 1 / 4.0, -1 / 4.0, 1 / 4.0, -7 / 16.0);
        bezier(p, 9 / 32.0, -7 / 32.0, 1 / 2.0, -7 / 32.0, 1 / 2.0, -1 / 2.0);
        // Правое крыло
        bezier(p, 7 / 6.0, -1 / 4.0, 7 / 6.0, 1 / 4.0, 1 / 2.0, 1 / 2.0);
        // Правая верхняя выемка
        bezier(p, 1 / 2.0, 3 / 16.0, 5 / 16.0, 3 / 32.0, 3 / 16.0, 3 / 16.0);

        // Правое ухо
        line(p, 1 / 8.0, 1 / 2.0);
        line(p, 1 / 16.0, 3 / 8.0);

        return p.toArray(new double[0][]);
    }

    private static void line(List<double[]> p, double x, double y) {
        p.add(new double[]{x, y});
    }

    private static void bezier(List<double[]> p,
                               double x1, double y1,
                               double x2, double y2,
                               double x3, double y3) {
        double[] s = p.get(p.size() - 1);
        double x0 = s[0], y0 = s[1];

        for (int i = 1; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            double u = 1 - t;
            double a = u * u * u;
            double b = 3 * u * u * t;
            double c = 3 * u * t * t;
            double d = t * t * t;
            p.add(new double[]{
                    a * x0 + b * x1 + c * x2 + d * x3,
                    a * y0 + b * y1 + c * y2 + d * y3
            });
        }
    }
}