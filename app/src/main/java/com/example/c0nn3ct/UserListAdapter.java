package com.example.c0nn3ct;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private final ArrayList<UserObject> userList;

    UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new UserListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mPhone;

        UserListViewHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
        }
    }
}
