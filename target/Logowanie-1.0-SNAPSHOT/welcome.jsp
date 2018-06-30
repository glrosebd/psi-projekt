<!--JSP zalogowana-->
<%@page import="com.aneta.logowanie.Connector"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" session="true" content="text/html; charset=UTF-8">
        <title>Zalogowano!</title>
    </head>
    <body>
        
        <%
            response.setHeader("Cache-Control","no-cache"); //Wymuszenie na przegladarce, by pobrala nowa kopie strony z serwera.
            response.setHeader("Cache-Control","no-store"); //Wymuszenie na przegladarce, by nie zapisywala w cache zadnej strony.
            response.setDateHeader("Expires", 0); //Wymuszenie odswiezania strony.
            response.setHeader("Pragma","no-cache"); //Kompatybilnosc wstecz dla HTML 1.0
               
            Connector connector = new Connector();
            
            if(session.getAttribute("loggedLogin") == null)
            {
                request.setAttribute("responseMessage", "Proszę się zalogować!");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else 
            {
                if(!connector.testConnection())
                {
                    session.invalidate();
                    request.setAttribute("responseMessage", "Brak połączenia z bazą danych!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
            
            List credentials = connector.pullAccountCredentials((String)session.getAttribute("loggedLogin"));
            System.out.println(credentials);
        %>
        
        <br /><br /><br />
        <h1><center>Witaj ${loggedLogin}!</center></h1><br /><br /><br />
        <h1><center>Twoje dane: </center></h1>
        <h2><center>Login: ${loggedLogin}</center></h2> 
        <h2><center>Email: <%= credentials.get(1) %></center></h2> 
        <h2><center>Ilość błędnych logowań: <%= credentials.get(2) %></center></h2> 
        <h2><center>Ostatnie błędne logowanie: <%= credentials.get(3) %></center></h2> 
    
        <br />
        <h2><div style="color: #FF0000;">${responseMessage}</div></h2>
    
        <br />
        
        <form method="post" action="mycalendar.jsp">    
            <table align="center">
                <td><input type="submit" value="Mój Kalendarz"/></td>
            </table>
        </form>
        
        <form method="post" action="Logout">    
            <table align="center">
                <td><input type="submit" value="Wylogowanie"/></td>
            </table>
        </form>
    </body>
</html>