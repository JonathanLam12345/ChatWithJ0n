package com.example.jonathanlam.chatwithjon;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.socero.soceroapp.Database.MessagingDatabase;
import com.team.socero.soceroapp.Database.UserInfoDatabase;
import com.team.socero.soceroapp.R;

import java.util.List;


public class MyCustomAdapter_MessageList extends BaseAdapter
{
    private List<RowItem_MessageList> arrayList;
    private LayoutInflater mLayoutInflater;
    UserInfoDatabase userInfoDatabase;
    MessagingDatabase messagingDatabase;
    Context context;

    public MyCustomAdapter_MessageList(Context context, List<RowItem_MessageList> arrayList)
    {
        this.arrayList = arrayList;
        this.context = context;
        // get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        // getCount() represents how many items are in the list
        return arrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.arrayList.get(position);
    }

    @Override
    // get the position id of the item from the list
    public long getItemId(int i)
    {
        return 0;
    }

    /*private view holder class*/
    private class ViewHolder
    {
        ImageView imageView_displayPic;  //POM image, or Current user image.
        TextView textView_fullName;
        TextView textView_message;
        TextView textView_timeStamp;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        ViewHolder holder = null;
        userInfoDatabase = new UserInfoDatabase(this.context);
        messagingDatabase = new MessagingDatabase(this.context);

        // check to see if the reused view is null or not, if is not null then
        // reuse it
        if (view == null)
        {
            view = mLayoutInflater.inflate(R.layout.message_list_item, null);
            holder = new ViewHolder();
            holder.textView_fullName = (TextView) view.findViewById(R.id.textView_fullName);
            holder.textView_message = (TextView) view.findViewById(R.id.textView_message);
            holder.imageView_displayPic = (ImageView) view.findViewById(R.id.imageView_displayPic);
            holder.textView_timeStamp = (TextView) view.findViewById(R.id.textView_timeStamp);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        RowItem_MessageList rowItem_messageList = (RowItem_MessageList) getItem(position);

        //change font size
        //  https://stackoverflow.com/a/25913782

        //String s = rowItem_messageList.getFullName() + "<br />" + "<font color=#A2A2A2>" + rowItem_messageList.getMessage() + "</font>";
        //holder.textView_message.setText(Html.fromHtml(s));

        if (rowItem_messageList.getRead()==false)  //unread message.
        {
          //  Log.d("Chat", "read false?: "+ rowItem_messageList.getRead());

            holder.textView_fullName.setText(rowItem_messageList.getFullName());
            holder.textView_fullName.setTypeface(null, Typeface.BOLD);

            holder.textView_message.setText(rowItem_messageList.getMessage());
            holder.textView_message.setTypeface(null, Typeface.BOLD);

            holder.textView_timeStamp.setTypeface(null, Typeface.BOLD);
        }
        else   //message have been read before
        {
           // Log.d("Chat", "read true?: "+ rowItem_messageList.getRead());
            holder.textView_fullName.setText(rowItem_messageList.getFullName());
            holder.textView_message.setText(rowItem_messageList.getMessage());
        }

       // Log.d("Chat", "message sent:" + rowItem_messageList.getTimeStamp());
       // Log.d("Chat", "current time:" + MyMessages.currentUnixTime);

        //get time ago timestamp.
        TimeAgo timeAgo = new TimeAgo();
        holder.textView_timeStamp.setText(timeAgo.getText(Long.parseLong(rowItem_messageList.getTimeStamp()), Long.parseLong(MyMessages.currentUnixTime)));

        UnixTime.unixTime = "";
        //Displaying the profile pictures.

        //If you like talking to yourself...
        if (rowItem_messageList.getUserID().equals(userInfoDatabase.getUser_ID()))
        {
         //   Log.d("Chat:", "Displaying current user image: " + userInfoDatabase.getProfileImageURL());
            if (userInfoDatabase.getProfileImageURL() == null || userInfoDatabase.getProfileImageURL().equals(""))
            {
                Glide.with(view.getContext() /* context */)
                        .load(R.mipmap.default_image_meetup)
                        .into(holder.imageView_displayPic);
            }
            else
            {
                if (userInfoDatabase.getProfileImageURL().contains("."))
                {
                    Glide.with(view.getContext() /* context */)
                            .load(userInfoDatabase.getProfileImageURL())
                            .into(holder.imageView_displayPic);
                }
                else
                {
                    String image64;
                    image64 = userInfoDatabase.getProfileImageURL();
                    byte[] decodedString = Base64.decode(image64, Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imageView_displayPic.setImageBitmap(decodedByte); //display the image.
                }
            }
        }
        else
        {
           // Log.d("Chat:", "Displaying other user image: " + rowItem_messageList.getImage());
            if (rowItem_messageList.getImage() == null || rowItem_messageList.getImage().equals(""))
            {
                Glide.with(view.getContext() /* context */)
                        .load(R.mipmap.default_image_meetup)
                        .into(holder.imageView_displayPic);
            }
            else
            {
                if (rowItem_messageList.getImage().contains("."))
                {
                    Glide.with(view.getContext() /* context */)
                            .load(rowItem_messageList.getImage())
                            .into(holder.imageView_displayPic);
                }
                else
                {
                    byte[] decodedString = Base64.decode(rowItem_messageList.getImage(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imageView_displayPic.setImageBitmap(decodedByte); //display the image.
                }
            }
        }

        // this method must return the view corresponding to the data at the
        // specified position.
        return view;
    }
}