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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.model.Friend;

import java.util.List;

public class RequestsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    private List<Friend> requestsList;

    private TextView requestText;
    private Button closeButton;
    private Button acceptButton;
    private Button deleteButton;

    private Context context;
    private Dialog requestDialog;
    private String currentSelectedFriend;

    private RequestsAdapter requestsAdapter;
    private FriendsReceiver friendsReceiver;


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
        View view = inflater.inflate(R.layout.requests_fragment, container, false);

        this.listView = (ListView) view.findViewById(R.id.listRequests);

        this.listView.setOnItemClickListener(this);

        this.requestDialog = new Dialog(this.context);
        this.requestDialog.setContentView(R.layout.custompopuprequest);

        this.closeButton = this.requestDialog.findViewById(R.id.closePopupRequestButton);
        this.acceptButton = this.requestDialog.findViewById(R.id.acceptRequest);
        this.deleteButton = this.requestDialog.findViewById(R.id.deleteRequest);
        this.requestText = this.requestDialog.findViewById(R.id.textPopupRequest);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.currentSelectedFriend = this.requestsList.get(position).getFriendName();
        chooseButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchRequestsAsink().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        friendsReceiver = new FriendsReceiver();
        this.context.registerReceiver(friendsReceiver, new IntentFilter("GET_LEVEL_REQUEST"));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.context.unregisterReceiver(friendsReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void chooseButton(){

        this.requestText.setText(this.currentSelectedFriend + " want to be your friend !");

        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
            }
        });
        this.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AcceptRequestAynk(currentSelectedFriend).execute();
            }
        });
        this.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteRequestAsynk(currentSelectedFriend).execute();
            }
        });
        this.requestDialog.show();
    }


    private class FetchRequestsAsink extends AsyncTask<Void,Void,List<Friend>> {

        @Override
        protected List<Friend> doInBackground(Void... voids) {
            try {
                List<Friend> friends = ApiRequests.getInstance().getRequestsForCurrentUser();
                requestsList = friends;
                return friends;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            super.onPostExecute(friends);
            System.out.println("Se afiseaza prietenii=");
            if (friends != null){
                requestsAdapter = new RequestsAdapter(context, requestsList);
                listView.setAdapter(requestsAdapter);
            }
        }
    }

    private class AcceptRequestAynk extends AsyncTask<Void,Void,Boolean>{

        private String currentRequest;

        public AcceptRequestAynk(String currentRequest){
            this.currentRequest = currentRequest;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean accepted = false;
            try {
                accepted = ApiRequests.getInstance().acceptRequest(currentRequest);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return accepted;
        }

        @Override
        protected void onPostExecute(Boolean boolValue) {
            super.onPostExecute(boolValue);
            if (boolValue){
                requestsList.remove(new Friend(currentSelectedFriend));
                System.out.println("Aici s-a sters din lista" + currentRequest);
                requestsAdapter.notifyDataSetChanged();
                requestDialog.dismiss();
            }
            else{
                Toast.makeText(context,"There is a problem with server!",Toast.LENGTH_SHORT);
            }
        }
    }


    private class DeleteRequestAsynk extends AsyncTask<Void,Void,Boolean>{

        private String currentRequest;

        public DeleteRequestAsynk(String currentRequest){
            this.currentRequest = currentRequest;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean accepted = false;
            try {
                accepted = ApiRequests.getInstance().cancelRequest(currentRequest);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return accepted;
        }

        @Override
        protected void onPostExecute(Boolean boolValue) {
            super.onPostExecute(boolValue);
            if (boolValue){
                requestsList.remove(new Friend(currentSelectedFriend));
                System.out.println("Aici s-a sters din lista" + currentRequest);
                requestsAdapter.notifyDataSetChanged();
                requestDialog.dismiss();
            }
            else{
                Toast.makeText(context,"There is a problem with server!",Toast.LENGTH_SHORT);
            }
        }
    }

    private class RequestsAdapter extends ArrayAdapter<Friend> {

        private Context contextApplication;
        private List<Friend> friends;

        public RequestsAdapter(@NonNull Context context, List<Friend> friends) {
            super(context, R.layout.row_request, R.id.friendRequest, friends);
            this.friends = friends;
            this.contextApplication = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) contextApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request,parent,false);
            ImageView imageView = row.findViewById(R.id.imageRequest);
            TextView name = row.findViewById(R.id.friendRequest);
            ImageView imageViewLayoutInner = row.findViewById(R.id.imageRequestLayout);
            name.setEnabled(false);

            imageView.setImageResource(R.drawable.tell_a_friends);
            name.setText((CharSequence) this.friends.get(position).getFriendName());
            imageViewLayoutInner.setImageResource(R.drawable.addicon);

            return row;
        }
    }

    class FriendsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("GET_LEVEL_REQUEST"))
            {
                System.out.println("Se incearca handle !");
                String sendBy = intent.getStringExtra("LEVEL_SEND");
                requestsList.add(new Friend(sendBy));
                if (requestsList.size() == 1){
                    requestsAdapter.notifyDataSetChanged();
                }
                else{
                    requestsAdapter = new RequestsAdapter(context.getApplicationContext() ,requestsList);
                    listView.setAdapter(requestsAdapter);
                }
            }
        }

    }


}
