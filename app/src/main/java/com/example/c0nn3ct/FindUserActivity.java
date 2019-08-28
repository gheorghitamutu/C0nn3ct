package com.example.c0nn3ct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.Objects;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        contactList = new ArrayList<>();
        userList = new ArrayList<>();

        initializeRecyclerView();
        getContactList();
    }

    private void initializeRecyclerView() {
        mUserList = findViewById(R.id.user_list);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);

        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);

        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }

    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if (phones != null) {
            while(phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phone = phone.replace(" ", "");
                phone = phone.replace("-", "");
                phone = phone.replace("(", "");
                phone = phone.replace(")", "");

                if (!String.valueOf(phone.charAt(0)).equals("+")) {
                    phone = getCountryISO() + phone;
                }

                UserObject contact = new UserObject(name, phone);
                contactList.add(contact);

                mUserListAdapter.notifyDataSetChanged();

                getUserDetails(contact);
            }

            phones.close();
        }
    }

    private void getUserDetails(UserObject contact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(contact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String phone = "", name = "";

                    if(dataSnapshot.getChildrenCount() > 0) {
                        DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();

                        if(childSnapshot.child("phone").getValue() != null) {
                            phone = Objects.requireNonNull(childSnapshot.child("phone").getValue()).toString();
                        }

                        if(childSnapshot.child("phone").getValue() != null) {
                            name = Objects.requireNonNull(childSnapshot.child("name").getValue()).toString();
                        }

                        UserObject user = new UserObject(name, phone);

                        if (name.equals(phone)) {
                            for (UserObject contact : contactList) {
                                if(contact.getPhone().equals(user.getPhone())) {
                                    user.setPhone(contact.getPhone());
                                    break;
                                }
                            }
                        }

                        userList.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryISO() {
        String iso = "";

        getApplicationContext();
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null) {
            iso = telephonyManager.getNetworkCountryIso();
        }

        return String.valueOf(PhoneNumberUtil.getInstance().getCountryCodeForRegion(iso.toUpperCase()));
    }
}
