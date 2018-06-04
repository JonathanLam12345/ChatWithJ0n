
import android.content.Context;
import android.content.SharedPreferences;
/*
Note that it only stores Strings.
Guide:
InAppDatabase inAppDatabase_userInfo;
inAppDatabase_userInfo = new InAppDatabase(this.getActivity().getApplicationContext(), "UserInfo");
Store data:
inAppDatabase_userInfo.storeData("user_ID", user_id);
Get data:
inAppDatabase_userInfo.getData("user_ID")
*/
public class InAppDatabase
{
    SharedPreferences sharedPreferences;


    public InAppDatabase(Context context, String name)     //name of the file.
    {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    //Store value in key inside the name file.
    public void storeData(String key, String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //assuming that all data stored is a string.
    public String getData(String key)
    {
        String value = sharedPreferences.getString(key, "Socero");  //the second parameter is the value assigned to the key if the key doesn't initially exist.
        return value;
    }
}