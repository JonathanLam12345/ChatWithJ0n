package com.example.jonathanlam.chatwithjon;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

//import com.voice.controller.client.R;

public class MyCustomAdapter_Message extends BaseAdapter
{
    private List<RowItem_Message> arrayList;
    private LayoutInflater mLayoutInflater;
    UserInfoDatabase userInfoDatabase;
    MessagingDatabase messagingDatabase;
    Context context;

    public MyCustomAdapter_Message(Context context, List<RowItem_Message> arrayList)
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
        TextView textView_message;
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
            view = mLayoutInflater.inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.textView_message = (TextView) view.findViewById(R.id.textView_message);
            holder.imageView_displayPic = (ImageView) view.findViewById(R.id.imageView_displayPic);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        RowItem_Message rowItem_message = (RowItem_Message) getItem(position);

        holder.textView_message.setText(rowItem_message.getMessage());


        //Displaying the profile pictures.
        if (rowItem_message.getUserID().equals(userInfoDatabase.getUser_ID()))
        {
          //  Log.d("Chat:", "Displaying current user image: " + userInfoDatabase.getProfileImageURL());
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
          //  Log.d("Chat:", "Displaying other user image: " + messagingDatabase.getImage());
            if (messagingDatabase.getImage() == null || messagingDatabase.getImage().equals(""))
            {
                Glide.with(view.getContext() /* context */)
                        .load(R.mipmap.default_image_meetup)
                        .into(holder.imageView_displayPic);
            }
            else
            {
                if (messagingDatabase.getImage().contains("."))
                {
                    Glide.with(view.getContext() /* context */)
                            .load(messagingDatabase.getImage())
                            .into(holder.imageView_displayPic);
                }
                else
                {
                    String image64;
                    image64 = messagingDatabase.getImage();
                    byte[] decodedString = Base64.decode(image64, Base64.NO_WRAP);
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
