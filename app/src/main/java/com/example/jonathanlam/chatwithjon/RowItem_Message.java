package com.example.jonathanlam.chatwithjon;


/**
 * Created by Jonathan Lam on 2017-08-29.
 */

public class RowItem_Message
{
    private String message;
    private String userID;

    public RowItem_Message(String message, String userID)
    {
        this.message = message;
        this.userID = userID;
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