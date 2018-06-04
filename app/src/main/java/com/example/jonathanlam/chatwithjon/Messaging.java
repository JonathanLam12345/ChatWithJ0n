package com.example.jonathanlam.chatwithjon;

/**
 * Created by Jonathan Lam on 2017-08-18.
 */

public class Messaging
{
    String userID;
    String message;



    // Default constructor required for calls to DataSnapshot.getValue(PrivateMesssaging.class)
    public Messaging()
    {
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}