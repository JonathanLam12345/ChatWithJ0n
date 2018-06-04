
package com.example.jonathanlam.chatwithjon;

public class RowItem_MessageList
{
    private String message;
    private String userID;
    private Boolean read;
    private String timeStamp;        //unix time - 99
    private String image;
    private String fullName;


    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }


    public RowItem_MessageList(String message, String userID, String fullName, Boolean read, String timeStamp, String image)
    {
        this.userID = userID;
        this.read = read;
        this.fullName = fullName;
        this.timeStamp = timeStamp;
        this.message = message;
        this.image = image;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
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