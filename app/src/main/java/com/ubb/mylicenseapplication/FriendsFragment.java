package com.ubb.mylicenseapplication;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.model.Friend;

import java.util.List;

public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private List<Friend> friendsList;
    private Dialog friendDialog;
    private Context context;
    private MainActivity mainActivity;
    private FriendsAdapter friendsAdapter;
    private AcceptRecevier acceptRecevier;

    private Button buttonClose;
    private TextView friendText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);

        this.listView = (ListView) view.findViewById(R.id.listFriends);

        this.listView.setOnItemClickListener(this);

        this.friendDialog = new Dialog(this.context);

        this.friendDialog.setContentView(R.layout.custompopup);

        this.buttonClose = (Button) this.friendDialog.findViewById(R.id.closePopupButton);
        this.friendText  = (TextView) this.friendDialog.findViewById(R.id.textPopup);
        this.friendText.setEnabled(false);

        this.mainActivity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchFriendsAsync().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        acceptRecevier = new AcceptRecevier();
        this.context.registerReceiver(acceptRecevier, new IntentFilter("GET_LEVEL_ACCEPT"));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.context.unregisterReceiver(acceptRecevier);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(position);
        this.mainActivity.showProgressBar();
        new GetStepsForMyFriendAsync(this.friendsList.get(position).getFriendName()).execute();
    }

    private void ShowPopup(String userName, Integer steps){
        if ( steps == -1 ){
            this.friendText.setText(userName + " is not registered in competition !");
        }
        else {
            this.friendText.setText(userName + " has " + steps + " ! Take more steps !");
        }
        this.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendDialog.dismiss();
            }
        });
        this.friendDialog.show();
    }


    private class FetchFriendsAsync extends AsyncTask<Void,Void, List<Friend>> {

        @Override
        protected List<Friend> doInBackground(Void... voids) {
            try {
                List<Friend> friends = ApiRequests.getInstance().getFriendsForCurrentUser();
                friendsList = friends;
                return friends;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainActivity.showProgressBar();
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            super.onPostExecute(friends);
            if (friends != null){
                friendsAdapter = new FriendsAdapter(context.getApplicationContext() ,friends);
                listView.setAdapter(friendsAdapter);
            }
            mainActivity.closeProgressBar();
        }
    }

    private class GetStepsForMyFriendAsync extends AsyncTask<Void,Void,Integer>{

        private String friendName;

        public GetStepsForMyFriendAsync(String friendName){
            this.friendName = friendName;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            try {
                Integer stepsForMyFriend =  ApiRequests.getInstance().getStepsForMyFriend(this.friendName);
                return stepsForMyFriend;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            ShowPopup(this.friendName,integer);
            mainActivity.closeProgressBar();
        }
    }

    private class FriendsAdapter extends ArrayAdapter<Friend>{

        private Context contextApplication;
        private List<Friend> friends;

        public FriendsAdapter(@NonNull Context context, List<Friend> friends) {
            super(context, R.layout.row, R.id.friendName, friends);
            this.friends = friends;
            this.contextApplication = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) contextApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row,parent,false);
            ImageView imageView = row.findViewById(R.id.imageFriends);
            TextView name = row.findViewById(R.id.friendName);
            name.setEnabled(false);

            imageView.setImageResource(R.drawable.frd);
            name.setText((CharSequence) this.friends.get(position).getFriendName());

            return row;
        }
    }

    class AcceptRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("GET_LEVEL_ACCEPT"))
            {
                System.out.println("Se incearca handle !");
                String acceptedBy = intent.getStringExtra("LEVEL_ACCEPT");
                friendsList.add(new Friend(acceptedBy));
                if (friendsList.size() == 1){
                    friendsAdapter.notifyDataSetChanged();
                }
                else{
                    friendsAdapter = new FriendsAdapter(context.getApplicationContext() ,friendsList);
                    listView.setAdapter(friendsAdapter);
                }
            }
        }

    }

}
