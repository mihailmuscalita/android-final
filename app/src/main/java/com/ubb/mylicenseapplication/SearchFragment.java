package com.ubb.mylicenseapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private Context context;
    private Dialog searchDialog;
    private Dialog failFilterDialog;
    private String currentSelectedFriend;

    private ListView listView;
    private MainActivity mainActivity;
    private SearchNewFriendsAdapter searchAdapter;
    private List<Friend> searchList;

    private TextView friendText;
    private Button closeButton;
    private Button sendRequestButton;
    private Button searchAParticularName;
    private Button closeWarningDialogButton;
    private EditText editTextForFriend;
    private TextView editTextWarning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        this.listView = (ListView) view.findViewById(R.id.listSearch);
        this.searchAParticularName = (Button) view.findViewById(R.id.searchFriendButton);
        this.editTextForFriend = (EditText) view.findViewById(R.id.searchEditText);

        this.listView.setOnItemClickListener(this);
        this.searchAParticularName.setOnClickListener(this);

        this.searchDialog = new Dialog(this.context);
        this.searchDialog.setContentView(R.layout.custompopupsearch);

        this.closeButton = this.searchDialog.findViewById(R.id.closePopupButtonSearch);
        this.sendRequestButton = this.searchDialog.findViewById(R.id.sendButtonPopupRequest);
        this.friendText = this.searchDialog.findViewById(R.id.textPopupSearch);

        this.failFilterDialog = new Dialog(this.context);
        this.failFilterDialog.setContentView(R.layout.custompopupfilter);

        this.closeWarningDialogButton = this.failFilterDialog.findViewById(R.id.closePopupFilter);
        this.editTextWarning = this.failFilterDialog.findViewById(R.id.warningText);

        this.mainActivity =(MainActivity) getActivity();

        new FetchNewFriendsAsync().execute();

        return view;
    }


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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.currentSelectedFriend = this.searchList.get(position).getFriendName();
        chooseAction();
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
            }
        });
        this.sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showProgressBar();
                new SendRequestAsynk(currentSelectedFriend).execute();
            }
        });

        this.searchDialog.show();

    }

    private void chooseAction(){
        this.friendText.setText("Do you want to be friend with " + this.currentSelectedFriend + "  ?");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchFriendButton:
                this.mainActivity.showProgressBar();
                new FilterRequestAsync(this.editTextForFriend.getText().toString()).execute();
                break;
        }
    }

    private void setTextAndShowFriend(){
        this.currentSelectedFriend = this.editTextForFriend.getText().toString();
        chooseAction();
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
            }
        });
        this.sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showProgressBar();
                new SendRequestAsynk(currentSelectedFriend).execute();
            }
        });

        this.searchDialog.show();
    }

    private void setSimpleText(String text){
        this.editTextWarning.setText(text);
        this.closeWarningDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failFilterDialog.dismiss();
            }
        });
        this.failFilterDialog.show();
    }

    private class FetchNewFriendsAsync extends AsyncTask<Void,Void, List<Friend>> {

        @Override
        protected List<Friend> doInBackground(Void... voids) {
            try {
                List<Friend> friends = ApiRequests.getInstance().searchToNewFriends();
                searchList = friends;
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
                searchAdapter = new SearchNewFriendsAdapter(context.getApplicationContext() ,friends);
                listView.setAdapter(searchAdapter);
            }
            mainActivity.closeProgressBar();
        }
    }

    private class SendRequestAsynk extends AsyncTask<Void,Void,Boolean>{

        private String sendTo;

        public SendRequestAsynk(String sendTo) {
            this.sendTo = sendTo;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean requestSent = false;
            try {
                requestSent = ApiRequests.getInstance().sendRequest(this.sendTo);
                return requestSent;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return requestSent;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                searchList.remove(new Friend(currentSelectedFriend));
                searchAdapter.notifyDataSetChanged();
                searchDialog.dismiss();
            }
            else{
                Toast.makeText(context,"There is a problem with server!",Toast.LENGTH_SHORT);
            }
            mainActivity.closeProgressBar();
        }
    }

    private class FilterRequestAsync extends AsyncTask<Void,Void,String>{

        private String filterName;

        public FilterRequestAsync(String filterName) {
            this.filterName = filterName;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String requestFilter = "";
            try {
                requestFilter = ApiRequests.getInstance().filterFriends(this.filterName);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            return requestFilter;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            mainActivity.closeProgressBar();
            if (aString.equals("A request is sending or you are already friends !") || aString.equals("This user does not exist !")) {
                setSimpleText(aString);
            }
            else {
              setTextAndShowFriend();
            }
        }
    }

    private class SearchNewFriendsAdapter extends ArrayAdapter<Friend> {

        private Context contextApplication;
        private List<Friend> friends;

        public SearchNewFriendsAdapter(@NonNull Context context, List<Friend> friends) {
            super(context, R.layout.row_search, R.id.searchText, friends);
            this.friends = friends;
            this.contextApplication = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) contextApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_search,parent,false);
            ImageView imageView = row.findViewById(R.id.imageSearch);
            TextView name = row.findViewById(R.id.searchText);
            ImageView imageViewLayoutInner = row.findViewById(R.id.imageSearchLayout);
            name.setEnabled(false);

            imageView.setImageResource(R.drawable.friends);
            name.setText((CharSequence) this.friends.get(position).getFriendName());
            imageViewLayoutInner.setImageResource(R.drawable.addsearch);

            return row;
        }
    }

}
