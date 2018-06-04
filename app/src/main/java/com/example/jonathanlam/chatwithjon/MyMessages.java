package com.example.jonathanlam.chatwithjon;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.socero.soceroapp.Analytics.Analytics;
import com.team.socero.soceroapp.Database.MessagingDatabase;
import com.team.socero.soceroapp.Database.UserInfoDatabase;
import com.team.socero.soceroapp.Home.MainActivity;
import com.team.socero.soceroapp.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jonathan Lam on 2017-08-22.
 */

public class MyMessages extends Fragment
{
    static Context context;
    static UserInfoDatabase userInfoDatabase;

    static ListView list_messages;
    static String currentUnixTime = "";
    static int firstLoad = 0; //the first time the fragment load. The app won't notify the user.

    //To read or write data from the database, you need an instance of DatabaseReference:
    public static DatabaseReference mDatabase;
    public static MessagingDatabase messagingDatabase;

    public static boolean isUserOnMessagingList = false;
    public static boolean MessagingListenerActivated = false;   //When true: MainActivity no longer need to listen for new messages.

    private static ArrayList<RowItem_MessageList> arrayList;
    static int readCounter = 0;

    /*
        This callback is called when the activity becomes visible to the user.  [[application screen]]
    */
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("Chat", "MyMessages: onStart()");
        MyMessages.isUserOnMessagingList = true;
    }

    /*
      This callback is called when the activity is no longer visible.
      [[when user is using another app]]
      [[Your app is no longer visible]]
      [[note that onPause() will always execute before onStop()]]
      [[switching to ActivityA to ActivityB]]
     */
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("Chat", "MyMessages: onStop()");
        MyMessages.isUserOnMessagingList = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("messages :", "onCreateView");
        firstLoad = 0;
        View view = inflater.inflate(R.layout.my_messages, container, false);

        context = getContext();
        userInfoDatabase = new UserInfoDatabase(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        messagingDatabase = new MessagingDatabase(getContext());

        new Analytics(context, "Viewing their Messages");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Log.d("messages :", "onViewCreated");
        this.context = getContext();
        super.onViewCreated(view, savedInstanceState);

        list_messages = (ListView) view.findViewById(R.id.list_messages);
        list_messages.setClickable(true);

        UnixTime.code = "1";
        new UnixTime();

        list_messages.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("Chat: Messages plz", arrayList.get(position) + "");

                MessagingDatabase messagingDatabase = new MessagingDatabase(getContext());

                Log.d("Chat: ", "Passing other user ID to the Chat class: " + arrayList.get(position).getUserID());
                messagingDatabase.setUserID(arrayList.get(position).getUserID());

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.frame, new Chat()).addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });
    }

    public static void notifyUser()
    {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("menuFragment", "messageListFragment");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.white_pom)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Socero")
                .setContentText("You have a new message.")
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setVibrate(new long[]{1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void afterGettingCurrent()
    {
        UnixTime.code = "";
        MyMessages.currentUnixTime = UnixTime.unixTime;
        MyCustomAdapter_MessageList mAdapter;
        //clear unix time in MyCustomAdapter_MessageList.
        arrayList = new ArrayList<>();

        mAdapter = new MyCustomAdapter_MessageList(context, arrayList);
        list_messages.setAdapter(mAdapter);

        mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).addValueEventListener(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        arrayList.clear();
                        readCounter = 0;
                        //Log.d("Chat: onChildAdded:", " dataSnapshot.getkey: " + dataSnapshot.getKey() +
                        //"dataSnapshot.getRef(): " + dataSnapshot.getRef() + "dataSnapshot.getValue(): " + dataSnapshot.getValue());
                        MessagingList messagingList = new MessagingList();
                        ChatStatus chatStatus = new ChatStatus();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            messagingList = snapshot.getValue(MessagingList.class);//////////////////

                            if (messagingList.getRead() == false)
                            {
                                readCounter++;
                            }

                            //public RowItem_MessageList(String message, String userID, String fullName, Boolean read, String timeStamp, String image)
                            arrayList.add(new RowItem_MessageList(messagingList.getMessage(), messagingList.getUserID(), messagingList.getFullName(), messagingList.getRead(), snapshot.getKey().split("-")[0], messagingList.getImage()));
                        }

                        if (readCounter > 0)
                        {
                           MainActivity.redDot = true;//////////////////////////////////
                            MyMessages.readCounter = 0; //reset
                        }
                        else
                        {
                            MainActivity.redDot = false;/////////////////////////////////
                            MyMessages.readCounter = 0; //reset
                        }

                        Collections.reverse(arrayList);

                        mAdapter.notifyDataSetChanged();

//Check the last child node (Top messaging List)
//Note: You don't have to check if messageList's read is read/unread.
//Note: the top messageList might not be the person you want to talk to.
//No notification:
//messagingDatabase's userID equals Top messaging List's userID+ user is current on the Chat.   (i'm currently chatting with the person)

//Send notification:
//messagingDatabase's userID doesn't equal to Top messaging List's userID + user is currently on the chat  (currently chatting but other user messaged you) (messagingDatabase's userID can't be null)

//Send notification:
//(user is not current on the Chat.) (note that we don't have to check if messageList's read is read/unread.)




                        MyMessages.MessagingListenerActivated = true;   //MainActivity no longer need to listen for new messages.
                        if (firstLoad == 0)
                        {
                            //do nothing.
                            Log.d("Chat", " firstLoad++" + firstLoad);
                            firstLoad++;
                        }
                        else if ((messagingList.getUserID() != null))
                        {
                            if (Chat.isUserOnChat && (messagingList.getUserID().equals(MyMessages.messagingDatabase.getUserID())))//ok...
                            {
                                //no notifications.
                                Log.d("Chat", "MessagingList: (I'm currently chatting with the person)");

                            } //Send notification!!!
                            else if (Chat.isUserOnChat  && !(messagingList.getUserID().equals(MyMessages.messagingDatabase.getUserID())))  //(currently chatting but other user messaged you)
                            {
                                Log.d("Chat", "MessagingList: (currently chatting but other user messaged you)" + Chat.isUserOnChat + " " + messagingList.getUserID() + " " + MyMessages.messagingDatabase.getUserID());
                                notifyUser();
                            }
                            else if (Chat.isUserOnChat == false)
                            {
                                Log.d("Chat", "MessagingList: (received a message when user isn't chatting.)");
                                notifyUser();
                            }
                            else
                            {
                                Log.d("Chat", "There's another case that I'm missing...!!!" + "Chat.isUserOnChat: " + Chat.isUserOnChat
                                        + "messagingList.getUserID(): " + messagingList.getUserID() + "MyMessages.messagingDatabase.getUserID(): " + MyMessages.messagingDatabase.getUserID());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Log.d("Chat", "onCancelled");
                    }
                }
        );
    }
}