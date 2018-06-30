<!--JSP nowej aktywnosci-->
<%@page import="java.util.List"%>
<%@page import="com.aneta.logowanie.Connector"%>
<%@page import="com.aneta.logowanie.model.Activity"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" session="true" content="text/html; charset=UTF-8">
        <title>Mój Kalendarz</title>
    </head>
    <body>
        
        <%
            response.setHeader("Cache-Control","no-cache"); //Wymuszenie na przegladarce, by pobrala nowa kopie strony z serwera.
            response.setHeader("Cache-Control","no-store"); //Wymuszenie na przegladarce, by nie zapisywala w cache zadnej strony.
            response.setDateHeader("Expires", 0); //Wymuszenie odswiezania strony.
            response.setHeader("Pragma","no-cache"); //Kompatybilnosc wstecz dla HTML 1.0
            
            if(session.getAttribute("loggedLogin") == null)
            {
                request.setAttribute("responseMessage", "Proszę się zalogować!");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else 
            {
                Connector connector = new Connector();
                if(!connector.testConnection())
                {
                    session.invalidate();
                    request.setAttribute("responseMessage", "Brak połączenia z bazą danych! Następuje wylogowanie.");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
            String login = (String)session.getAttribute("loggedLogin");
        %>
        
        <br /><br />
        <h1><center>Dodaj Aktywność</center></h1><br /><br />
    
         <form method="post" action="NewActivity">    
            <table align="center">
                <tr>
                    <td>Data:</td>
                    <td><input type="text" name="activityDate" /></td>
                </tr>
                <tr>
                    <td>Opis:</td>
                    <td><input type="text" name="activityText" /></td>    
                </tr>
                <tr>
                    <input type="hidden" name="userLogin" value="<%= login %>">
                    <td><center><input type=submit value="Dodaj nową aktywność"/></center></td> 
                </tr>
            </table>
        </form>
    
        <h2><center><div style="color: #FF0000;">${errorMessage}</div></center></h2>
        <br />
    
        <form action="mycalendar.jsp">
            <table align="center">
                <tr>
                    <td><input type="submit" value="Powrót" /></td>
                </tr>                
            </table>
        </form>
        
    </body>
</html>
