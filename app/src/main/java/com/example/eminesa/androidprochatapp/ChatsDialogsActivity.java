package com.example.eminesa.androidprochatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eminesa.androidprochatapp.Common.Common;
import com.example.eminesa.androidprochatapp.Holder.QBChatDialogHolder;
import com.example.eminesa.androidprochatapp.Holder.QBUnreadMessageHolder;
import com.example.eminesa.androidprochatapp.Holder.QBUsersHolder;
import com.example.eminesa.androidprochatapp.adapter.ChatDiaogAdapter;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatsDialogsActivity extends AppCompatActivity implements QBSystemMessageListener, QBChatDialogMessageListener {

    FloatingActionButton floatingActionButton;
    ListView listChatDialog;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.context_deleted_dialog:
                deletedDialog(info.position);
                break;

        }

        return true;
    }

    private void deletedDialog(int index) {
        final QBChatDialog chatDialog = (QBChatDialog) listChatDialog.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(), false)
                .performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBChatDialogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                        ChatDiaogAdapter adapter = new ChatDiaogAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                        listChatDialog.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadChatDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chat_dialog_menu_user:
                showUserProfile();
                break;
            default:
                break;
        }
        return true;
    }

    private void showUserProfile() {

        Intent intent = new Intent(ChatsDialogsActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_dialogs);

        // Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_dialog_toolbar);
        toolbar.setTitle("Android Pro Chat");
        setSupportActionBar(toolbar);

        CreateSessionForChat();

        LoadChatDialog();

        listChatDialog = (ListView) findViewById(R.id.chats_dialog_list_view);

        registerForContextMenu(listChatDialog);

        listChatDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //mesajlaşma sayfaası açılacak.
                QBChatDialog qbChatDialog = (QBChatDialog) listChatDialog.getAdapter().getItem(position);
                Intent intent = new Intent(ChatsDialogsActivity.this, ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                startActivity(intent);
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatsDialogsActivity.this, ListUsersActivity.class);
                startActivity(intent);
            }
        });

    }

    private void LoadChatDialog() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);


        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                //put all dialog to cache

                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                //Unread Setting
                Set<String> setId = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs)
                    setId.add(chatDialog.getDialogId());

                //get message unread
                QBRestChatService.getTotalUnreadMessagesCount(setId, QBUnreadMessageHolder.getInstance().getBundle())
                        .performAsync(new QBEntityCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer ınteger, Bundle bundle) {

                                //Save to cache
                                QBUnreadMessageHolder.getInstance().setBundle(bundle);

                                //Refresh List Dialogs
                                ChatDiaogAdapter adapter = new ChatDiaogAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                                listChatDialog.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("ERROR", e.getMessage());
            }
        });
    }

    private void CreateSessionForChat() {
        final ProgressDialog mDialog = new ProgressDialog(ChatsDialogsActivity.this);

        mDialog.setMessage("Please Waiting");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String user, password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        //Load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUsers = new QBUser(user, password);

        // Burda veri doğru veya yanlış çalıştığında esaj yada işlemlerini listeleyeiblirsiniz.
        QBAuth.createSession(qbUsers).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                qbUsers.setId(qbSession.getUserId());
                try {
                    qbUsers.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUsers, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {

                        mDialog.dismiss();

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ChatsDialogsActivity.this);


                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatsDialogsActivity.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                        Log.e("ERROR", "" + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processMessage(final QBChatMessage qbChatMessage) {

        //put dialog to cache
        //Because we send system message with content is DialogId
        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                //put to cache
                QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllChatDialogs();
                ChatDiaogAdapter adapter = new ChatDiaogAdapter(getBaseContext(), adapterSource);
                listChatDialog.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {

        Log.e("ERROR", "" + e.getMessage());
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer ınteger) {
        LoadChatDialog();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer ınteger) {

    }
}
