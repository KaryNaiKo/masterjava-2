package ru.javaops.masterjava.controllers;

import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.Collection;

@WebServlet("/")
@MultipartConfig(location = "/tmp")
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/WEB-INF/views/fileUpload.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Collection<Part> parts = null;
        try {
            parts = req.getParts();
            Part part = parts.iterator().next();
            if(part.getSubmittedFileName().endsWith(".xml")) {
                StaxStreamProcessor processor = new StaxStreamProcessor(part.getInputStream());
                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    String email = processor.getAttribute("email");
                    String groupRefs = processor.getAttribute("groupRefs");
                    String name = processor.getText();

                    System.out.println("name=" + name + " groupRefs=" + groupRefs + " email=" + email);
                }
            }
            res.sendRedirect("/");
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
