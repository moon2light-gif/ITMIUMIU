package com.mishka.controller;

import com.mishka.dao.RecordHistory;
import com.mishka.model.ResultRecord;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String r = request.getParameter("r");

        if ("clear".equals(request.getParameter("action"))) {
            RecordHistory.clear(request.getSession(false));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else if (x == null || y == null || r == null) {

            List<ResultRecord> history = RecordHistory.getAll(request.getSession(false));
            request.setAttribute("history", history);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/index.jsp");
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/area-check");
            rd.forward(request, response);
        }

    }
 }
