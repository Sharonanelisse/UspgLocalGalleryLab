package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.dto.UploadResult;
import com.darwinruiz.uspglocalgallerylab.repositories.LocalFileRepository;
import com.darwinruiz.uspglocalgallerylab.services.ImageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;


@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 2 * 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 30L * 1024 * 1024)
public class UploadServlet extends HttpServlet {
    private ImageService service;

    @Override
    public void init() {
        service = new ImageService(LocalFileRepository.createDefault());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Part part = req.getPart("file");

        if (part == null || part.getSize() == 0) {
            resp.sendError(400, "Archivo vacío");
            return;
        }

        String target = req.getParameter("target");
        if (target == null) target = "local";
        String submitted = part.getSubmittedFileName();
        String fileName = java.nio.file.Paths.get(submitted).getFileName().toString();
        String contentType = part.getContentType();

        String prefix = req.getParameter("prefix");
        if (prefix == null) prefix = "";

        try (InputStream in = part.getInputStream()) {
            service.save(fileName, in, prefix);
        }
        resp.sendRedirect(req.getContextPath() + "/upload.jsp?ok=local");

    }
}