/*
    Serwlet obslugujacy rejestracje.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dreknar
 */
public class Register extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/html;charset=UTF-8");
        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
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
                
                if(login == null || login.length() == 0)
                {
                    errorMessage += "Proszę podać login!" + "\n";
                }
                else
                { 
                    
                    if (login.length() < 5)
                    {
                        errorMessage += "Login musi składać się z minimum 5 znaków!" + "\n";
                    }

                    if(connector.isLoginExists(login))
                    {
                        errorMessage += "Login jest już w użyciu!" + "\n";
                    }
                }
                
                if(email == null || email.length() == 0)
                {
                    errorMessage += "Proszę podać adres mailowy!" + "\n";
                }
                else
                {
                    Pattern validatedEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); //Wzorzec adresu email.
                    Matcher matchEmail = validatedEmail.matcher(email); //Matcher pobiera podanego maila i porownuje, czy zgadza sie ze wzorcem.
                    Boolean emailValidated = matchEmail.matches(); //Logika dla matchera od adresu email. 
                    
                    if (!(emailValidated))
                    {
                        errorMessage += "Niepoprawny adres mailowy!" + "\n";
                    }

                    if(connector.isEmailExists(email))
                    {
                        errorMessage += "Email jest już w użyciu!" + "\n";
                    }
                }
                
                if(password == null || password.length() == 0)
                {
                    errorMessage += "Proszę podać hasło!" + "\n";
                }
                else
                {
                    if (password.length() < 6) 
                    {
                        errorMessage += "Proszę podać minimum 6 znakowe hasło!" + "\n"; 
                    }
                }
                
                if(confirmPassword == null || confirmPassword.length() == 0)
                {
                    errorMessage += "Proszę ponownie podać hasło!" + "\n";
                }
                else
                {
                    if(password.length() != 0)
                    {
                        if(!password.equals(confirmPassword))
                        {
                            errorMessage += "Podane hasła są niezgodne ze sobą!" + "\n";
                        }
                    }
                }
            }
                
            //Gdy liczba bledow jest rowna zero:
            if (errorMessage.length() == 0) 
            {
                connector.addAccount(login, email, password);
                request.setAttribute("responseMessage", "Konto zostalo utworzone! Możesz się teraz zalogowac.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } 

            //Gdy liczba bledow jest rozna od zera:
            else 
            {
                System.out.println(errorMessage);
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } 
        catch (SQLException  | ClassNotFoundException ex) 
        {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
