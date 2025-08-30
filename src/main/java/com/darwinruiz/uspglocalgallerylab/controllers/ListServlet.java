package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.repositories.LocalFileRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/list")
public class ListServlet extends HttpServlet {
    private LocalFileRepository repo;

    @Override
    public void init() {
        repo = LocalFileRepository.createDefault();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        List<String> all = repo.listByExtensionsRecursive("imagenes", ".png", ".jpg", ".jpeg", ".gif", ".webp");



        // TODO-4: leer page y size, calcular fromIndex/toIndex y sublista
        int page = 1, size = 12; // defaults
        // int total = all.size(); int totalPages = ...

        if (page < 1) page = 1;
        if (size < 1) size = 12;

        try {
            if (req.getParameter("page") != null) {
                page = Integer.parseInt(req.getParameter("page"));
            }
            if (req.getParameter("size") != null) {
                size = Integer.parseInt(req.getParameter("size"));
            }
        } catch (NumberFormatException e) {

        }

        int total = all.size();
        int totalPages = (int) Math.ceil((double) total / size);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<String> pageItems = all.subList(fromIndex, toIndex); // TODO: reemplazar con sublista calculada

        req.setAttribute("localImages", pageItems);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("total", all.size());
        // req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("/gallery.jsp").forward(req, resp);
    }
}
