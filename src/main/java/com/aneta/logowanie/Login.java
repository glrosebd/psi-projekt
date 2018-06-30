/*
    Serwlet obslugujacy logowanie.
 */
package com.aneta.logowanie;

import java.beans.PropertyVetoException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.*;
import javax.servlet.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dreknar
 */
public class Login extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        int accountValidation = 0;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        try 
        {
            connectionValidation = connector.testConnection();
            if(connectionValidation == true)
            {
                System.out.println("DB Connected!");
                if(login == "" && login.length() == 0) //nie podano loginu
                {
                    request.setAttribute("responseMessage", "Proszę podać login!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                if(password == "" && password.length() == 0) //nie podano hasla
                {
                    request.setAttribute("responseMessage", "Proszę podać hasło!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else
                {
                    accountValidation = connector.accountValidation(login, password);
                    if(accountValidation == 0) //konto nie istnieje.
                    {
                        request.setAttribute("responseMessage", "Konto nie istnieje!");
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                    }
                    else
                    {
                        if(accountValidation == 1) //bledne dane.
                        {
                            request.setAttribute("responseMessage", "Niepoprawne dane...");
                            request.getRequestDispatcher("/index.jsp").forward(request, response);
                        }
                        else
                        {
                            if(accountValidation == 2) //dobre logowanie.
                            {
                                //Stworzenie sesji, przypisanie do niej maila i imienia usera do dalszej manipulacji danymi.
                                HttpSession session = request.getSession();
                                session.setAttribute("loggedLogin", login);
                                response.sendRedirect("welcome.jsp");
                            }
                            else
                            {
                                if(accountValidation == 4) //zablokowanie konta
                                {
                                    request.setAttribute("responseMessage", "Przekroczono limit błędnych prób logowania! Następuje zablokowanie konta.");
                                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                                }
                                else //konto zablokowane.
                                {
                                    request.setAttribute("responseMessage", "Konto jest zablokowane!");
                                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                                }
                            }
                        }
                    }
                }       
            }
            else
            {
                out.println("DB connection could not been established!");
            }
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
