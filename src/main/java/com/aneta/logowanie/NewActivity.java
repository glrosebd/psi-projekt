/*
    Serwlet obslugujacy dodawanie nowej aktywnosci.
 */
package com.aneta.logowanie;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dreknar
 */
public class NewActivity extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/html;charset=UTF-8");
        String activityDate = request.getParameter("activityDate");
        String activityText = request.getParameter("activityText");
        String userLogin = request.getParameter("userLogin");
        System.out.println("user login XDDDDDDDDDDDDDDDD: " + userLogin);
        try 
        {
            String errorMessage = ""; //Pusty string, do ktorego beda dodawane poszczegolne errory.
            connectionValidation = connector.testConnection();
            if(connectionValidation == false)
            {
                errorMessage += "Brak połączenia z bazą danych!" + "\n";
            }
            else
            {
                System.out.println("DB Connected!");
                
                if(activityDate == null || activityDate.length() == 0)
                {
                    errorMessage += "Proszę podać datę!" + "\n";
                }
                else
                { 
                    Pattern validatedDate = Pattern.compile("((?:(?:\\d{1}\\d{1}))[-:\\/.](?:[0]?[1-9]|[1][012])[-:\\/.](?:(?:[0-2]?\\d{1})|(?:[3][01]{1})))(?![\\d])", Pattern.CASE_INSENSITIVE); //Wzorzec daty.
                    Matcher matchDate = validatedDate.matcher(activityDate); //Matcher pobiera podanego maila i porownuje, czy zgadza sie ze wzorcem.
                    Boolean dateValidated = matchDate.matches(); //Logika dla matchera od adresu email. 
                    if (!(dateValidated))
                    {
                        errorMessage += "Niepoprawa format daty! (musi byc YY/MM/DD)!" + "\n";
                    }
                }
                
                if(activityText == null || activityText.length() == 0)
                {
                    errorMessage += "Proszę podać adres mailowy!" + "\n";
                }
            }
                
            //Gdy liczba bledow jest rowna zero:
            if (errorMessage.length() == 0) 
            {
                connector.addActivity(activityDate, activityText, userLogin);
                request.setAttribute("responseMessage", "Dodano nową aktywność.");
                request.getRequestDispatcher("/mycalendar.jsp").forward(request, response);
            } 

            //Gdy liczba bledow jest rozna od zera:
            else 
            {
                System.out.println(errorMessage);
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/addactivity.jsp").forward(request, response);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}