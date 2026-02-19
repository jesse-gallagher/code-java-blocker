package frostillicus.xsp.codejavablocker;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.designer.runtime.domino.adapter.util.PageNotFoundException;

public class NullServlet extends HttpServlet {
    public static final Servlet INSTANCE = new NullServlet();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new PageNotFoundException(MessageFormat.format("No resource found at path {0}", req.getServletPath()));
    }
}
