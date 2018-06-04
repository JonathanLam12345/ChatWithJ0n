
import android.content.Context;

/**
 * Created by Jonathan Lam on 2017-08-22.
 */

public class MessagingDatabase
{
    Context context;
    InAppDatabase inAppDatabase_otherUserInfo;

   /*
    private String userID;
    private String fullname;
    private String email;
    private String image;
    */

    public MessagingDatabase(Context c)
    {
        context = c;
        inAppDatabase_otherUserInfo = new InAppDatabase(context, "CbLIhBdfjlKxVcZFiVqX");
    }
    public String getUserID()
    {
        return inAppDatabase_otherUserInfo.getData("user_ID");
    }
    public void setUserID(String userID)
    {
        inAppDatabase_otherUserInfo.storeData("user_ID", userID);
    }

    public String getFullname()
    {
        return inAppDatabase_otherUserInfo.getData("full_name");
    }

    public void setFullname(String fullname)
    {
        inAppDatabase_otherUserInfo.storeData("full_name", fullname);
    }

    public String getEmail()
    {
        return inAppDatabase_otherUserInfo.getData("email");
    }

    public void setEmail(String email)
    {
        inAppDatabase_otherUserInfo.storeData("email", email);
    }

    public String getImage()
    {
        return inAppDatabase_otherUserInfo.getData("image");
    }

    public void setImage(String image)
    {
        inAppDatabase_otherUserInfo.storeData("image", image);
    }
}