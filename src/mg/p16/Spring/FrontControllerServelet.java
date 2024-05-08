package mg.p16.Spring;
import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class FrontControllerServelet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println(request.getRequestURL());
        // String requestedPage = request.getPathInfo();
        
        // request.getRequestDispatcher("/web/page" + requestedPage).forward(request, response);
        // out.println("/WEB-INF/views/jsp" + requestedPage);
        // out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "FrontControllerServelet";
    }
}
