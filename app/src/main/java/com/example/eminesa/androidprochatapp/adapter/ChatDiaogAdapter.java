package com.example.eminesa.androidprochatapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.eminesa.androidprochatapp.Holder.QBUnreadMessageHolder;
import com.example.eminesa.androidprochatapp.R;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by eminesa on 5.06.2018.
 */

public class ChatDiaogAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDiaogAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_dialog_list_item, null);

            TextView messageTextView, titleTextView;
            final ImageView profileImageView, unreadImageView;

            messageTextView = (TextView) view.findViewById(R.id.list_chat_dialog_message);
            titleTextView = (TextView) view.findViewById(R.id.list_chat_dialog_title);
            profileImageView = (ImageView) view.findViewById(R.id.chat_dialog_image);
            unreadImageView = (ImageView) view.findViewById(R.id.unread_dialog_image);

            messageTextView.setText(qbChatDialogs.get(position).getLastMessage());
            titleTextView.setText(qbChatDialogs.get(position).getName());


            ColorGenerator generater = ColorGenerator.MATERIAL;

            int randomColor = generater.getRandomColor();

            if (qbChatDialogs.get(position).getPhoto().equals("null")) {

                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .round();

                //Get first character from chat dialog title for create chat dialog image
                TextDrawable drawable = builder.build(titleTextView.getText().toString().substring(0, 1).toUpperCase(), randomColor);

                profileImageView.setImageDrawable(drawable);
            } else {

                //Download bitmap from server and set for Dialog
                QBContent.getFile(Integer.parseInt(qbChatDialogs.get(position).getPhoto()))
                        .performAsync(new QBEntityCallback<QBFile>() {
                            @Override
                            public void onSuccess(QBFile qbFile, Bundle bundle) {

                                String fileURL = qbFile.getPublicUrl();
                                Picasso.with(context)
                                        .load(fileURL)
                                        .resize(50, 50)
                                        .centerCrop()
                                        .into(profileImageView);
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e("Error image", "" + e.getMessage());
                            }
                        });
            }

            //Set message unread count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            int unreadCount = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if (unreadCount > 0) {
                TextDrawable unreadDrawable = unreadBuilder.build("" + unreadCount, Color.RED);
                unreadImageView.setImageDrawable(unreadDrawable);

            }

        }
        return view;
    }

}
