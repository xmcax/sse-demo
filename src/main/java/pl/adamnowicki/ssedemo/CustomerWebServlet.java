package pl.adamnowicki.ssedemo;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(urlPatterns = "/web")
public class CustomerWebServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("static/index.html");

        try (InputStream inputStream = new ByteArrayInputStream(resourceAsStream.readAllBytes());
             ServletOutputStream outputStream = resp.getOutputStream()) {
            inputStream.transferTo(outputStream);
        }

        resp.setStatus(200);
    }
}
