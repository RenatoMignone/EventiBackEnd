package it.Security.Utente;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //questo metodo è un filtro che ci permette di vedere nel Log del Server cosa accade
public class FilterCustom implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println("Logging Request  {"+req.getMethod()+"} : {"+req.getRequestURI()+"}");


        chain.doFilter(request, response);
        System.out.println("Logging Response  {"+res.getStatus()+"} : {"+res.getContentType()+"}");
    }
}