package com.example.eminesa.androidprochatapp.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eminesa on 9.06.2018.
 */

public class QBChatMessagesHolder {

    private static QBChatMessagesHolder instance;
    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessagesHolder getInstance() {

        QBChatMessagesHolder qbMessagesHolder;
        synchronized (QBChatMessagesHolder.class) {

            if (instance == null)

                instance = new QBChatMessagesHolder();
            qbMessagesHolder = instance;

        }
        return qbMessagesHolder;
    }

    private QBChatMessagesHolder() {

        this.qbChatMessageArray = new HashMap<>();

    }

    public void putMessages(String dialogId, ArrayList<QBChatMessage> qbChatMessages) {

        this.qbChatMessageArray.put(dialogId, qbChatMessages);
    }

    public void putMessage(String dialogId, QBChatMessage qbChatMessage) {

        List<QBChatMessage> listResult = (List) this.qbChatMessageArray.get(dialogId);
        listResult.add(qbChatMessage);
        ArrayList<QBChatMessage> listAdded = new ArrayList(listResult.size());
        listAdded.addAll(listResult);
        putMessages(dialogId, listAdded);
    }

    public ArrayList<QBChatMessage> getChatMessagesByDialogId(String dialogId) {

        return (ArrayList<QBChatMessage>) this.qbChatMessageArray.get(dialogId);
    }

}
