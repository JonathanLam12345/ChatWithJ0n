package com.example.jonathanlam.chatwithjon;

/**
 * Created by Jonathan Lam on 2017-09-26.
 */

public class ChatStatus
{
    private Boolean isChatting;
    private String whoYouCurrentlyChattingWith;

    public Boolean getChatting()
    {
        return isChatting;
    }

    public void setChatting(Boolean chatting)
    {
        isChatting = chatting;
    }

    public String getWhoYouCurrentlyChattingWith()
    {
        return whoYouCurrentlyChattingWith;
    }

    public void setWhoYouCurrentlyChattingWith(String whoYouCurrentlyChattingWith)
    {
        this.whoYouCurrentlyChattingWith = whoYouCurrentlyChattingWith;
    }
}