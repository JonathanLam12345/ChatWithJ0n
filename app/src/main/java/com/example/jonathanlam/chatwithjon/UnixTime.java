

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UnixTime
{
    static String unixTime = "";

    //code
    //0 - Messaging
    //1 - Messaging List
    static String code = "";

    public UnixTime()
    {
        Log.d("Unix Time", "UnixTime()");
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Void>
    {
        String resultStringUnix = "";
        String s = "https://www.epochconverter.com/";
        String find_unix = "<div id=\"ecclock\">";
        /*
        * Find this:
        * <div id="ecclock">
        * <div id=\"ecclock\">
		*/

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d("Unix Time", "doInBackground");

            URL url;
            try
            {
                url = new URL(s);
                Log.d("Unix Time", s);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;

                boolean found_unix = false;

                while ((inputLine = in.readLine()) != null && !found_unix)
                {
                    if (inputLine.contains(find_unix))
                    {
                        int index = inputLine.indexOf(find_unix);
                        int IndexOfResultString = index + find_unix.length();

                        found_unix = true;
                        char c = inputLine.charAt(IndexOfResultString);
                        while (c != '<')
                        {
                            resultStringUnix = resultStringUnix + c;
                            IndexOfResultString = IndexOfResultString + 1;
                            c = inputLine.charAt(IndexOfResultString);
                        }
                        int i = Integer.parseInt(resultStringUnix);

                        resultStringUnix = i + "";
                        Log.d("Unix Time", resultStringUnix);
                    }
                }
                in.close();


            } catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Unix Time", "The website is down.");
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            String s = "It is currently " + resultStringUnix + ".";
            UnixTime.unixTime = resultStringUnix;

            if (UnixTime.code.equals("0"))
            {
                Chat.firebaseStuff();
            }
            else if(UnixTime.code.equals("1"))
            {
               MyMessages.afterGettingCurrent();
            }
                Log.d("Unix Time ", s);
        }
    }

}