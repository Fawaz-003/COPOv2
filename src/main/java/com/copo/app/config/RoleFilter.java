package com.copo.app.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login?error=sessionExpired");
            return false;
        }

        String role = (String) session.getAttribute("role");
        String uri = request.getRequestURI();

     // Do NOT include /student-marks here anymore
        if (uri.startsWith("/faculty") || uri.startsWith("/faculty-marks") ||
            uri.startsWith("/batches") || 
            uri.startsWith("/departments") || uri.startsWith("/copo") || uri.startsWith("/copo/**") ) {
            
        	System.out.println("PreHandle faculy");
            if (!"faculty".equals(role)) {
            	System.out.println("PreHandle student !faculty");
                response.sendRedirect("/login?error=unauthorized");
                return false;
            }
            
        }
        
       


        if (uri.startsWith("/student-marks") ) {
        	
        	System.out.println("PreHandle student");
            if (!"student".equals(role)) {
            	System.out.println("PreHandle student !student");
                response.sendRedirect("/login?error=unauthorized");
                return false;
            }
        }
        
        
     // Shared URIs (accessible by both roles)
        if ((uri.startsWith("/subjects") || uri.startsWith("/questions") || uri.startsWith("/subjects/by-dept-sem") || uri.startsWith("/students")) &&
            !(role.equals("faculty") || role.equals("student"))) {

        	System.out.println("Shared uri ");
 
            response.sendRedirect("/login?error=unauthorized");
            return false;
        }
        

        return true;
    }
    
    
    
}
