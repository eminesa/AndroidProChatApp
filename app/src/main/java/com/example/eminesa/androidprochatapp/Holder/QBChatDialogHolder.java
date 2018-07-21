package com.example.eminesa.androidprochatapp.Holder;

import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eminesa on 10.06.2018.
 */

public class QBChatDialogHolder {

    private static QBChatDialogHolder instance;
    private HashMap<String, QBChatDialog> qbChatDialogHashMap;


    public static synchronized QBChatDialogHolder getInstance() {

        QBChatDialogHolder qbChatDialogHolder;
        synchronized (QBChatDialogHolder.class) {
            if (instance == null)
                instance = new QBChatDialogHolder();

        }
        qbChatDialogHolder = instance;
        return qbChatDialogHolder;
    }

    public QBChatDialogHolder() {
        this.qbChatDialogHashMap = new HashMap<>();

    }

    public void putDialogs(List<QBChatDialog> dialogs) {

        for (QBChatDialog qbChatDialog : dialogs)
            putDialog(qbChatDialog);
    }

    public void putDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialogHashMap.put(qbChatDialog.getDialogId(), qbChatDialog);
    }

    public QBChatDialog getChatDialogById(String dialogId) {

        return (QBChatDialog) qbChatDialogHashMap.get(dialogId);
    }

    public List<QBChatDialog> getChatDialogByIds(List<String> dialogIds) {

        List<QBChatDialog> chatDialogs = new ArrayList<>();
        for (String id : dialogIds) {

            QBChatDialog chatDialog = getChatDialogById(id);
            if (chatDialog != null)

                chatDialogs.add(chatDialog);
        }
        return chatDialogs;
    }

    public ArrayList<QBChatDialog> getAllChatDialogs() {
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for (String key : qbChatDialogHashMap.keySet())
            qbChat.add(qbChatDialogHashMap.get(key));
        return qbChat;
    }

    public void removeDialog(String id) {
        qbChatDialogHashMap.remove(id);

    }
}
