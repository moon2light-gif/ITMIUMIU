package com.mishka.controller;

import com.mishka.dao.RecordHistory;
import com.mishka.model.ResultRecord;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/area-check")
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Если пользователь пытается попасть в этот сервлет самостоятельно
        if (request.getDispatcherType() != DispatcherType.FORWARD) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long currentTime = System.currentTimeMillis();
        long startTime = System.nanoTime();

        double x, y, r;

        try {
            x = Double.parseDouble(request.getParameter("x"));
            y = Double.parseDouble(request.getParameter("y"));
            r = Double.parseDouble(request.getParameter("r"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Проверка диапазона, если обошли js
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(r)
                || x < -5 || x > 3
                || y < -5 || y > 5
                || !(r == 1 || r == 1.5 || r == 2 || r == 2.5 || r == 3)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isHit = checkHit(x, y, r);

        double executionTime = (System.nanoTime() - startTime) / 1000000.0;

        ResultRecord result = new ResultRecord(
                x,
                y,
                r,
                isHit,
                currentTime,
                executionTime
        );

        HttpSession httpSession = request.getSession();
        RecordHistory.save(httpSession, result);

        // Отдельная обработка для формы, которая отправляется при нажатии на Canvas (чтобы сразу видеть нарисованную точку)
        if ("true".equals(request.getParameter("ajax"))) {
            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().print(
                    "{"
                            + "\"x\":" + result.x() + ","
                            + "\"y\":" + result.y() + ","
                            + "\"r\":" + result.r() + ","
                            + "\"hit\":" + result.isHit() + ","
                            + "\"timestamp\":" + result.currentTime() + ","
                            + "\"executionTime\":" + result.executionTime()
                            + "}"
            );

            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"ru\">");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>Результат проверки</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Результат проверки</h1>");

        out.println("<table border=\"1\">");
        out.println("<tr><th>Параметр</th><th>Значение</th></tr>");
        out.println("<tr><td>X</td><td>" + x + "</td></tr>");
        out.println("<tr><td>Y</td><td>" + y + "</td></tr>");
        out.println("<tr><td>R</td><td>" + r + "</td></tr>");
        out.println("</table>");

        if (isHit) {
            out.println("<h2>Точка попала в область!</h2>");
        } else {
            out.println("<h2>Точка не попала в область.</h2>");
        }

        out.println("<p><a href=\"" + request.getContextPath()
                + "/\">Вернуться к форме</a></p>");

        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Функция проверки попадания по области
     * @param x - координата X
     * @param y - координата Y
     * @param r - параметр R
     * @return true в случае попадания, иначе - false
     */
    protected boolean checkHit(double x, double y, double r) {
        boolean triangle = x <= 0 && y >= 0 && y <= x + r;

        boolean circle = x >= 0 && y >= 0
                && x * x + y * y <= (r * r) / 4;

        boolean rectangle = x >= 0 && x <= r
                && y <= 0 && y >= -r;

        return triangle || circle || rectangle;
    }
}
