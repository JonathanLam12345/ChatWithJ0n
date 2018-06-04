package com.example.jonathanlam.chatwithjon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.socero.soceroapp.Database.MessagingDatabase;
import com.team.socero.soceroapp.Database.UserInfoDatabase;
import com.team.socero.soceroapp.Discovery.Bot.POMBot;
import com.team.socero.soceroapp.R;
import com.team.socero.soceroapp.Retrofit.BaroServiceProvider;
import com.team.socero.soceroapp.Retrofit.DB;
import com.team.socero.soceroapp.Retrofit.Profile;
import com.team.socero.soceroapp.Retrofit.SoceroAPI;
import com.team.socero.soceroapp.Startup.Application;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


//One to One messaging!!!

public class Chat extends Fragment
{
    @Inject
    public BaroServiceProvider baroServiceProvider;

    @Inject
    public SoceroAPI soceroAPI;

    Context context;
    static UserInfoDatabase userInfoDatabase;
    TextView textView_chat;
    static EditText editText_input_chat;
    ListView listView_list_chat;
    ImageView imageView_send_button_chat;
    MyCustomAdapter_Message mAdapter;

    static MessagingDatabase messagingDatabase;


    public static boolean isUserOnChat = false;

    //To read or write data from the database, you need an instance of DatabaseReference:
    private static DatabaseReference mDatabase;

    Profile p = new Profile();
    //Storing the other user's info from API.
    static String otherUserID;
    static String otherEmail;
    static String otherName;
    static String otherImage;


    private static ArrayList<RowItem_Message> arrayList;
    static String userID1_userID2;
    static MessagingList messages_list_other = new MessagingList();

    /*
    This callback is called when the activity becomes visible to the user.  [[application screen]]
*/
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("Chat", "Chat: onStart()");
        Chat.isUserOnChat = true;
        //Don't change chat status here. Do it after getting other user info. Actually we could do it here since we know the otherUserID...
    }

    //Set current user's chatting status. We need this because when another sent the current user a message, the database needs to know if the message has been read or not.
    public void ChattingStatus(Boolean b)
    {
        ChatStatus chatStatus = new ChatStatus();
        chatStatus.setChatting(b);
        chatStatus.setWhoYouCurrentlyChattingWith(otherUserID);
        mDatabase.child("chat_status").child(userInfoDatabase.getUser_ID()).setValue(chatStatus);
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
        Log.d("Chat", "Chat: onStop()");
        Chat.isUserOnChat = false;
        ChattingStatus(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("chat :", "onCreateView");
        Application.inject(this);
        View view = inflater.inflate(R.layout.chat, container, false);

        this.context = getContext();
        userInfoDatabase = new UserInfoDatabase(context);
        messagingDatabase = new MessagingDatabase(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Log.d("chat :", "onViewCreated");
        this.context = getContext();
        super.onViewCreated(view, savedInstanceState);

        textView_chat = (TextView) view.findViewById(R.id.textView_chat);
        editText_input_chat = (EditText) view.findViewById(R.id.editText_input_chat);
        listView_list_chat = (ListView) view.findViewById(R.id.list_chat);
        listView_list_chat.setClickable(true);
        imageView_send_button_chat = (ImageView) view.findViewById(R.id.send_button_chat);

        textView_chat.setText("");

        arrayList = new ArrayList<RowItem_Message>();

        mAdapter = new MyCustomAdapter_Message(context, arrayList);
        listView_list_chat.setAdapter(mAdapter);

        //MAKE SURE TO GET THE OTHER USER ID FIRST!!
        MessagingDatabase messagingDatabase = new MessagingDatabase(context);

        //Other user ID is passed from Profile Fragment!!!
        otherUserID = messagingDatabase.getUserID();
        Log.d("Chat: ", "Get info from other user ID: " + messagingDatabase.getUserID());

        //Get other user info.
        getUserInfo(messagingDatabase.getUserID());

        Log.d("Chat: Current User:: ", userInfoDatabase.getUser_ID() + "\n" + userInfoDatabase.getEmail() + "\n" + userInfoDatabase.getFirst_name()
                + "\n" + userInfoDatabase.getProfileImageURL());

////////////////////////////////////////Messaging///////////////////////////////////////////////////
        //User click on the SEND button
        imageView_send_button_chat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!editText_input_chat.getText().toString().equals(""))
                {
                    POMBot.clickableList = false;
                    Log.d("chat :", "imageView clicked");

                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    UnixTime.code = "0";
                    new UnixTime();

                    //Generate the Messaging ID.
                }
            }
        });
    }

    //This method is executed after unix time has been retrieved.
    public static void firebaseStuff()
    {
        UnixTime.code = "";
        //Get the message that the current user had posted.
        String message = editText_input_chat.getText().toString();

        //Initialize the privateMessaging object. (Current user is the sender)
        Messaging messaging = new Messaging();
        messaging.setUserID(userInfoDatabase.getUser_ID());
        messaging.setMessage(message);
        editText_input_chat.setText("");

        Log.d("chat: gimme unix plz", UnixTime.unixTime + "");

        int randomNum = 1 + (int) (Math.random() * 99);   // 1-99
        String recordKey = UnixTime.unixTime + "-" + randomNum;
        UnixTime.unixTime = "";

        Log.d("Chat: ", "insert into database: " + userID1_userID2 + " " + recordKey);
        //Insert the message into the database.
        mDatabase.child("chat").child(userID1_userID2).child(recordKey).setValue(messaging);

//////////////////////////////////Update the messages list/////////////////////////////////////////////////

/////////////////////////Updating my messaging list:
        //Always show other user's profile image!!!
        //In this case, current user's message is the most updated message.
        MessagingList messages_list = new MessagingList();
        messages_list.setUserID(messagingDatabase.getUserID());
        messages_list.setMessage(messaging.getMessage());  //current user's message.
        messages_list.setRead(true);  //true. Since the current user already reading it.
        messages_list.setFullName(messagingDatabase.getFullname());
        messages_list.setImage(otherImage);

/*
                   dataSnapshot.getkey:         2a96b350-7b7c-11e7-9311-f36c5d2071e   (Child node)
                   dataSnapshot.getRef():       https://socero-ba0e6.firebaseio.com/chat/2a96b350-7b7c-11e7-9311-f36c5d2071e8
                   dataSnapshot.getValue()      {1504111804581-76={userID=67c36030-5907-11e7-afb3-4f1b741efa4e, fullName=Jimmy Tang, message=lol, image=/9j/4AAQSkZJRgABAQA.............
                   snapshot.getKey()            1504111804581-76
 */
        //Find current user in the database. Then update that messageList object.
        mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).orderByChild("userID").equalTo(otherUserID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Chat: ", "Finding: " + otherUserID);

                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        MessagingList messagingList = dataSnapshot.getValue(MessagingList.class);

                        //update!!! (delete + add to database)
                        dataSnapshot.getRef().child(snapshot.getKey()).setValue(null);
                        mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).child(recordKey).setValue(messages_list);
                    }
                }
                else
                {
                    Log.d("Chat", " dataSnapshot" + dataSnapshot.toString() + " doesn't exist. Therefore create a fresh record.");
                    mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).child(recordKey).setValue(messages_list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("Chat ERROR:", databaseError.toString());
            }
        });

//////////////////////Updating OTHER user messaging list:
        //Always show current user's profile image!!!
        //In this case, current user's message is the most updated message.

        messages_list_other.setUserID(userInfoDatabase.getUser_ID());  //my user ID
        messages_list_other.setMessage(messaging.getMessage());  //my message.
        //  messages_list_other.setRead(false);  //tricky...
        messages_list_other.setFullName(userInfoDatabase.getFirst_name() + " " + userInfoDatabase.getLast_name());   //my full name
        messages_list_other.setImage(userInfoDatabase.getProfileImageURL());  //my picture

        //Find other user in the database. Then update that "messageList object".
        mDatabase.child("chat").child(otherUserID).orderByChild("userID").equalTo(userInfoDatabase.getUser_ID()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Chat: ", "Finding2: " + otherUserID);
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        MessagingList chattingStatus = dataSnapshot.getValue(MessagingList.class);
                        checkIfOtherUserOnlineAndChattingWithYou(snapshot.getKey(), recordKey);
                    }
                }
                else
                {
                    messages_list_other.setRead(false);  //most likely false. if user is getting the message for the first time.
                    //Create a messageList for other user.
                    Log.d("Chat", " dataSnapshot" + dataSnapshot.toString() + " doesn't exist. Therefore create a fresh record.");
                    mDatabase.child("chat").child(otherUserID).child(recordKey).setValue(messages_list_other);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("Chat ERROR:", databaseError.toString());
            }
        });
    }

    //determine if the user is online/offline so I can tell if they have read the message.
    public static void checkIfOtherUserOnlineAndChattingWithYou(String oldSnapshotKey, String UnixAndKey)
    {
        mDatabase.child("chat_status").child(otherUserID).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Log.d("Chat: ", "checkIfOtherUserOnline: " + otherUserID);
                        if (dataSnapshot.exists())
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                ChatStatus chattingStatus = dataSnapshot.getValue(ChatStatus.class);

                                //check if the other user is online and if user2 is chatting with user1
                                if ((chattingStatus.getChatting()) && (chattingStatus.getWhoYouCurrentlyChattingWith().equals(userInfoDatabase.getUser_ID())))
                                {
                                    //I know the other person is chatting with me.
                                    messages_list_other.setRead(true);
                                }
                                else
                                {
                                    //other user didn't read your messages yet.
                                    messages_list_other.setRead(false);
                                }

                                //updating other's message list with the correct read status.
                                mDatabase.child("chat").child(otherUserID).child(oldSnapshotKey).setValue(null);
                                mDatabase.child("chat").child(otherUserID).child(UnixAndKey).setValue(messages_list_other);
                            }
                        }
                        else
                        {
                            //if the user status doesn't exist. it means the other users has never used the messaging before.
                            //Therefore other user is NOT online.

                            //updating other's message list with the correct read status.
                            messages_list_other.setRead(false);
                            mDatabase.child("chat").child(otherUserID).child(oldSnapshotKey).setValue(null);
                            mDatabase.child("chat").child(otherUserID).child(UnixAndKey).setValue(messages_list_other);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }

                });
    }

    //current user just read other user's message.
    public void read()
    {
        mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).orderByChild("userID").equalTo(otherUserID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Chat: ", "Finding: " + otherUserID);
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        MessagingList messagingList = snapshot.getValue(MessagingList.class);

                        //snapshot.getKey():::     1504748863-34
                        Log.d("Chat: Read Value1: ", messagingList.getFullName() + " " + messagingList.getRead() + " " + snapshot.getKey());

                        if (messagingList.getRead().equals(false))  //The user has read the message!!!
                        {
                            Log.d("Chat: Read Value2: ", messagingList.getFullName() + " " + messagingList.getRead() + " " + snapshot.getKey());
                            mDatabase.child("chat").child(userInfoDatabase.getUser_ID()).child(snapshot.getKey()).child("read").setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("Chat ERROR:", databaseError.toString());
            }
        });
    }

    //Retrieve the one to one messaging and populating the list view with the chat messages.
    public void user1VSuser2()////////////////////////////////////////////////////////////////////////////////////////////////////////
    {
        mDatabase.child("chat").child(userID1_userID2).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                arrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Messaging privateMessaging = snapshot.getValue(Messaging.class);//////////////////

                    //public RowItem_MessageList(String message, String userID, Boolean read, String timeStamp)
                    arrayList.add(new RowItem_Message(privateMessaging.getMessage(), privateMessaging.getUserID()));
                    mAdapter.notifyDataSetChanged();
                }
                //wait until your messagelist gets updated by the other user, then current user mark it as read.
                //Actually, now you don't have to.
                //  new Handler().postDelayed(new Runnable()
                //  {
                //   @Override
                // public void run()
                //  {
                read();
                //   }
                // }, 500);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    /*
    String otherUserID;
    String otherEmail;
    String otherName;
    String otherImage;
    */
    //Get the info of the other user!!!!
    //https://aujhfd1x3c.execute-api.us-east-1.amazonaws.com/staging/users/{id}
    //2a96b350-7b7c-11e7-9311-f36c5d2071e8 (Team Socero's ID)
    //my id: 31f34350-5919-11e7-9681-a535bb41d7f3
    private void getUserInfo(String otherUserID)
    {
        Log.d("Chat: ", "getUserInfo");

        Retrofit retrofit_getUserInfo = new Retrofit.Builder()
                .baseUrl("https://aujhfd1x3c.execute-api.us-east-1.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        SoceroAPI api_getUserInfo = retrofit_getUserInfo.create(SoceroAPI.class);

        soceroAPI.getDetailedProfileObservable(baroServiceProvider.getAuthToken().getToken(), otherUserID)
                .flatMap(new Func1<DB, Observable<Profile>>()
                {
                    @Override
                    public Observable<Profile> call(DB db)
                    {
                        return Observable.from(db.getProfile());
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Profile>()
        {
            @Override
            public void onCompleted()
            {
                Log.d("Chat: ", "Give me user id.." + p.getUserId() + "");
                Log.d("Chat: ", "Give me email.." + p.getEmail() + "");
                Log.d("Chat: ", "Give me first name.." + p.getFirstName() + "");
                Log.d("Chat: ", "Give me last name.." + p.getLastName() + "");
                Log.d("Chat: ", "Give me image url.." + p.getUserImage());
                Log.d("Chat: ", "Give me Socero ID.." + p.getSoceroId());

                // otherUserID = p.getUserId();
                otherEmail = p.getEmail();
                otherName = p.getFirstName() + " " + p.getLastName();
                otherImage = p.getUserImage();
                //  Log.d("Chat: Other User:: ", "From getUserInfo: " + otherUserID + "\n" + otherEmail + "\n" + otherName
                //        + "\n" + otherImage);

                // messagingDatabase.setUserID(otherUserID);
                messagingDatabase.setFullname(otherName);
                messagingDatabase.setEmail(otherEmail);
                messagingDatabase.setImage(otherImage);

                textView_chat.setText(otherName);

                // Generating the child node ID.
                int compare = userInfoDatabase.getEmail().compareTo(otherEmail);
                if (compare < 0) //current is smaller
                {
                    userID1_userID2 = userInfoDatabase.getUser_ID() + "()" + otherUserID;
                }
                else // current is larger!
                {
                    userID1_userID2 = otherUserID + "()" + userInfoDatabase.getUser_ID();
                }
                //Log.d("Chat: ", " userID1_userID2: " + userID1_userID2);
                ChattingStatus(true);
                user1VSuser2();
            }

            @Override
            public void onError(Throwable throwable)
            {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(com.team.socero.soceroapp.Retrofit.Profile profile)
            {
                p = profile;
            }
        });
    }
}