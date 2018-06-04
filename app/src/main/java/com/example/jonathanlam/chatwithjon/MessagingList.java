package com.example.jonathanlam.chatwithjon;

/**
 * Created by Jonathan Lam on 2017-08-18.
 */

public class MessagingList
{
    private String userID;
    private String message;
    private String image;
    private Boolean read;
    private String fullName;
//    private Boolean isChatting;   //I need to know if the currently user is chatting or not. Indicates whether the user is reading the messages or not.
//    private String whoYouChattingWith; //user ID of the person you can chatting with. This only valid if "isChatting" is true.

//    public String getWhoYouChattingWith()
//    {
//        return whoYouChattingWith;
//    }
//
//    public void setWhoYouChattingWith(String whoYouChattingWith)
//    {
//        this.whoYouChattingWith = whoYouChattingWith;
//    }
//
//    public Boolean getChatting()
//    {
//        return isChatting;
//    }
//
//    public void setChatting(Boolean chatting)
//    {
//        isChatting = chatting;
//    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }


    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }

    // Default constructor required for calls to DataSnapshot.getValue(PrivateMesssaging.class)
    public MessagingList()
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