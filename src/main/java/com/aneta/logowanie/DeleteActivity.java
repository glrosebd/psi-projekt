/*
    Serwlet obslugujacy usuwanie aktywnosci.
 */
package com.aneta.logowanie;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.servlet.*;

/**
*
* @author Dreknar
*/
public class DeleteActivity extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/html;charset=UTF-8");
        int activityID = Integer.parseInt(request.getParameter("activityID"));
        System.out.println("ID:" + activityID);
        try 
        {
            String errorMessage = ""; //Pusty string, do ktorego beda dodawane poszczegolne errory.
            connectionValidation = connector.testConnection();
            if(connectionValidation == false)
            {
                errorMessage += "Brak połączenia z bazą danych!" + "\n";
            }
                
            //Gdy liczba bledow jest rowna zero:
            if (errorMessage.length() == 0) 
            {
                connector.deleteActivity(activityID);
                request.setAttribute("responseMessage", "Usunięto aktywność o ID: " + activityID + "!");
                request.getRequestDispatcher("/mycalendar.jsp").forward(request, response);
            } 

            //Gdy liczba bledow jest rozna od zera:
            else 
            {
                System.out.println(errorMessage);
                request.setAttribute("responseMessage", errorMessage);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DeleteActivity.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}