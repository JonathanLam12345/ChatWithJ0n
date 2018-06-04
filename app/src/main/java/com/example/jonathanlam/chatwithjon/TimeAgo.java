package com.example.jonathanlam.chatwithjon;

/**
 * Created by Jonathan Lam on 2017-08-29.
 */

public class TimeAgo
{
    // https://stackoverflow.com/a/13018647
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public String getText(long past, long now)
    {
        if (past < 1000000000000L)
        {
           // Log.d("Chat: Time Ago: ", "timestamp given in seconds, convert to millis");
            // if timestamp given in seconds, convert to millis
            past *= 1000;
            now *= 1000;
        }

        if (past > now || past <= 0)
        {
           // Log.d("Chat: Time Ago: ", "NULL: PAST: " + past + "NOW: " + now);

            return "just now";
        }

        final long diff = now - past;
        if (diff < MINUTE_MILLIS)
        {
            return "just now";
        }
        else if (diff < 2 * MINUTE_MILLIS)
        {
            return "a minute ago";
        }
        else if (diff < 50 * MINUTE_MILLIS)
        {
            return diff / MINUTE_MILLIS + " minutes ago";
        }
        else if (diff < 90 * MINUTE_MILLIS)
        {
            return "an hour ago";
        }
        else if (diff < 24 * HOUR_MILLIS)
        {
            return diff / HOUR_MILLIS + " hours ago";
        }
        else if (diff < 48 * HOUR_MILLIS)
        {
            return "yesterday";
        }
        else
        {
            return diff / DAY_MILLIS + " days ago";
        }

//        final long diff = now - past;
//        Log.d("Chat: Time Ago: ", diff + "");
//        if (diff < MINUTE_MILLIS)
//        {
//            return "just now";
//        }
//        else if (diff < (2 * MINUTE_MILLIS))
//        {
//            return "a minute ago";
//        }
//        else if (diff < (50 * MINUTE_MILLIS))
//        {
//            return diff / MINUTE_MILLIS + " minutes ago";
//        }
//        else if (diff < (90 * MINUTE_MILLIS))
//        {
//            return "an hour ago";
//        }
//        else if (diff < (24 * HOUR_MILLIS))
//        {
//            return diff / HOUR_MILLIS + " hours ago";
//        }
//        else if (diff < (48 * HOUR_MILLIS))
//        {
//            return "yesterday";
//        }
//        else
//        {
//            return diff / DAY_MILLIS + " days ago";
//        }

    }


}