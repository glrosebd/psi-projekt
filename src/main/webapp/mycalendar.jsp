<!--JSP listy aktywnosci-->
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
        %>
        
        <br /><br />
        <h1><center>Mój Kalendarz</center></h1><br /><br />
    
         
        <table align="center" border ="1" style="border-collapse: collapse; padding: 10px; width: 25%">
        <tr>
            <td>ID</td>
            <td>Data</td>
            <td>Zadanie</td>
        </tr>
        <%
            Connector connector = new Connector();
            System.out.println("Logged login " + (String)session.getAttribute("loggedLogin"));
            List<Activity> activitiesList = connector.pullUserActivities((String)session.getAttribute("loggedLogin"));
            for(Activity a : activitiesList)
            {
        %>
                <tr>
                    <td><%= a.getActivityID() %></td>
                    <td><%= a.getDate() %></td>
                    <td><%= a.getText() %></td>
                    <%if(!a.getActivityID().equals("Brak aktywności"))
                    {
                        %> 
                        <td>
                            <form method="post" action="DeleteActivity"> 
                                <input type="hidden" name="activityID" value="<%= a.getActivityID() %>">
                                <input type="submit" align="middle" value="Usuń aktywność"/>
                            </form>

                        </td> 
                        <%
                    } 
                    %>
                </tr>
        <%      
            }
        %>
        
        <br /><br />
        
        <h2><center><div style="color: #FF0000;">${responseMessage}</div></center></h2>
        
        <br /><br />
        
        <form method="post" action="addactivity.jsp">   
            <table align="center">
                <td><input type="submit" value="Dodaj aktywność"/></td>
            </table>
        </form>
        
        <br /><br />
        
        <form method="post" action="welcome.jsp">   
            <table align="center">
                <td><input type="submit" value="Powrót"/></td>
            </table>
        </form>
        
    </body>
</html>

