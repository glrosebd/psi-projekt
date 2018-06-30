/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aneta.logowanie;

import com.aneta.logowanie.model.Activity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Uzytkownik
 */
public class Connector 
{
    //String przechowujacy dane niezbedne do dostepu do bazy danych.
    private static String connectionDataString = "jdbc:oracle:thin:@localhost:1521:XE";  //Adres, gdzie baza sie znajduje.
    private static String connectionUserName = "mgr_psi";
    private static String connectionUserPassword = "12345";
    
    //Metoda odpowiedzialna za sprawdzenie istnienia maila w DB (zabezpieczenie przed posiadaniem dwoch kont zarejestrowanych na tego samego maila).
    public boolean testConnection() throws SQLException
    {
        try 
        {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("Brak sterownika JDBC!");
            e.printStackTrace();
            return false;

        }
        
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            System.out.println("DB Connection established!");
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            return false;
        }     
        try 
        {
            connection.close(); //Zamykanie polaczenia.
            System.out.println("DB Connection closed!");
        } 
        catch (SQLException e) 
        {
            return false;
        }
        return true;
    }
    
    public boolean isLoginExists(String login) throws SQLException, ClassNotFoundException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String isExists = null;
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select Email from PSI_USER where Login = '" + login + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                isExists = result.getString(1); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
            }    
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        if(isExists != null) //Jesli nie ma wyniku (czyli nie istnieje w DB podany mail) zwracana jest falsz (czyli brak loginu).
        {
            System.out.println("Login jest juz uzywany. Wynik: " + isExists);
            return true;
        }  
        else
        {
            System.out.println("Login nie jest uzywany. Wynik: " + isExists);
            return false;
        }
    }
    
    public boolean isEmailExists(String email) throws SQLException, ClassNotFoundException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String isExists = null;
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select Email from PSI_USER where Email = '" + email + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                isExists = result.getString(1); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
            }    
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        if(isExists != null) //Jesli nie ma wyniku (czyli nie istnieje w DB podany mail) zwracana jest falsz (czyli brak maila).
        {
            System.out.println("Email jest juz uzywany. Wynik: " + isExists);
            return true;
        }  
        else
        {
            System.out.println("Email nie jest uzywany. Wynik: " + isExists);
            return false;
        }
    }
    
    //Rejestracja wczesniej zweryfikowanych danych i zapis do DB.
    public static void addAccount(String login, String email, String password) throws SQLException, ClassNotFoundException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            String encryptedPassword = sha1Encryptor(password);
            //zapytanie SQLowskie
            SQLStatement = "INSERT INTO PSI_USER (ID, Login, Email, Password, Acc_Block) VALUES (USER_SEQUENCE.nextval, '" + login + "','" + email + "','" + encryptedPassword + "','N')";
            System.out.println(SQLStatement);
            statement.execute(SQLStatement); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    //Metoda odpowiedzialna za sprawdzenie istnienia maila w DB (zabezpieczenie przed posiadaniem dwoch kont zarejestrowanych na tego samego maila).
    public int accountValidation(String login, String password) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        String SQLStatement = "";
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String userLogin = null;
        String userPassword = null;
        String accBlockade = null;
        int loginFailureCount = 0;
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            SQLStatement = "select Login, Password, Login_Fail, Acc_Block from PSI_USER where Login = '" + login + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                userLogin = result.getString(1); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
                userPassword = result.getString(2);
                loginFailureCount = result.getInt(3);   
                accBlockade = result.getString(4);
            }
            System.out.println("Wynik: " + userLogin);
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                statement.close();
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        if(userLogin==null) //Jesli nie ma wyniku (czyli nie istnieje w DB podany mail) zwracana jest prawda (czyli brak maila).
        {
            return 0; 
        }  
        else
        {
            String encryptedPassword = sha1Encryptor(password); //hashowanie hasla
             if(accBlockade.equals("Y")) //jesli accBlock jest Y, to zwracamy odpowiedz nr 3 - konto zablokowane.
            {
                return 3;
            }
            else
            {
                if(!(userPassword.equals(encryptedPassword))) //Jesli haslo jest bledne.
                {
                    loginFailureCount++;
                    try
                    {
                        connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
                        statement = connection.createStatement(); //proba polaczenia sie z DB.
                        System.out.println("DB Connected again!");
                        if(loginFailureCount >= 5) //jesli mamy juz 5 nieudana probe, to blokujemy konto.
                        {
                            SQLStatement = "UPDATE PSI_USER SET Login_Fail = " + loginFailureCount + ", Last_Login_Type = SYSDATE, Acc_Block = 'Y' WHERE Login = '" + userLogin + "'";
                        }
                        else
                        {
                            SQLStatement = "UPDATE PSI_USER SET Login_Fail = " + loginFailureCount + ", Last_Login_Type = SYSDATE WHERE Login = '" + userLogin + "'";
                        }

                        System.out.println(SQLStatement);
                        statement.execute(SQLStatement); //Execute Query
                    }
                    //W przypadku braku polaczenia wyrzuci SQLException
                    catch (SQLException e) 
                    {
                        System.out.println("DB Connection could not been established!");
                        e.printStackTrace();
                    }        
                    finally 
                    {
                        try 
                        {
                            statement.close();
                            connection.close(); //Zamykanie polaczenia.
                            System.out.println("DB Connection closed!");
                        } 
                        catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
                    }  
                    if(SQLStatement.contains("Acc_Block"))
                    {
                        return 4;
                    }
                    return 1;
                }
                else //Jesli wszystko jest w porzadku.
                {
                    return 2;
                }
            }
        }
    }
    
    public List<String> pullAccountCredentials(String login) 
    {
        List credentialsList = new ArrayList<>();
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select Login, Email, Login_Fail, Last_Login_Type from PSI_USER where Login = '" + login + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                credentialsList.add(result.getString(1)); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
                credentialsList.add(result.getString(2));
                credentialsList.add(result.getString(3));
                credentialsList.add(result.getString(4));
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return credentialsList;
    }
    
    //pul ID uzytkownika.
    public static int pullUserID(String userLogin)
    {
        int userID = 0;
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select ID from PSI_USER where Login = '" + userLogin + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next())
            {
                userID = result.getInt(1); //Argument w getInt mowi, z ktorej kolumny czytamy informacje.
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return userID;
    }
    
    //Pull danych zdarzen uzytkownika.
    public List<Activity> pullUserActivities(String userLogin)
    {
        List activitiesList = new ArrayList<Activity>();
        
        int userID = pullUserID(userLogin); //pobranie ID uzytkownika.
        System.out.println("UserID: " + userID);
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select ID, USR_DATE, USR_TXT from PSI_TEXT where UserID = '" + userID + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next()) //Wypisanie wszystkich wynikow.
            {
                Activity newActivity = new Activity();
                newActivity.setActivityID(result.getString(1));
                newActivity.setDate(result.getString(2));
                newActivity.setText(result.getString(3));
                activitiesList.add(newActivity);
            }
            if(activitiesList.isEmpty())
            {
                Activity newActivity = new Activity();
                newActivity.setActivityID("Brak aktywności");
                newActivity.setDate("");
                newActivity.setText("");
                activitiesList.add(newActivity);
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return activitiesList;
    }
    
    //Rejestracja wczesniej zweryfikowanych danych i zapis do DB.
    public static void addActivity(String activityDate, String activityText, String userLogin)
    {
        int userID = pullUserID(userLogin); //pobranie ID uzytkownika.
        System.out.println("UserID: " + userID);
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            //zapytanie SQLowskie
            SQLStatement = "INSERT INTO PSI_TEXT (ID, USERID, USR_DATE, USR_TXT) VALUES (USER_SEQUENCE.nextval, '" + userID + "','" + activityDate + "','" + activityText + "')";
            System.out.println(SQLStatement);
            statement.execute(SQLStatement); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    //Usuwanie aktywnosci.
    public static void deleteActivity(int activityID)
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            //zapytanie SQLowskie
            SQLStatement = "DELETE FROM PSI_TEXT WHERE ID = '" + activityID + "'";
            System.out.println(SQLStatement);
            statement.execute(SQLStatement); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    //Szyfrowanie hasła przy pomocy SHA-1
    public static String sha1Encryptor(String password) throws NoSuchAlgorithmException 
    {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        byte[] result = messageDigest.digest(password.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.length; i++) 
        {
            stringBuilder.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }         
        return stringBuilder.toString();
    }
}
