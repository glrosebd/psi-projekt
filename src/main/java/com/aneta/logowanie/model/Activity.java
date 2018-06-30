//Model jednego zdarzenia w kalendarzu

package com.aneta.logowanie.model;

public class Activity 
{
    public String activityID;
    public String date;
    public String text;
    
    //Konstruktor
    public Activity() 
    {
        this(null, null, null);
    }

    public Activity(String activityID, String date, String text) 
    {
        this.activityID = activityID;
        this.date = date;
        this.text = text;
    }

    public String getActivityID() 
    {
        return activityID;
    }

    public void setActivityID(String activityID) 
    {
        this.activityID = activityID;
    }
    
    public String getDate() 
    {
        return date;
    }

    public void setDate(String date) 
    {
        this.date = date;
    }
    
    public String getText() 
    {
        return text;
    }

    public void setText(String text) 
    {
        this.text = text;
    }
}