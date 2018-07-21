package com.example.eminesa.androidprochatapp.Holder;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eminesa on 5.06.2018.
 */

public class QBUsersHolder {
    private static QBUsersHolder instance;

    private SparseArray<QBUser> qbUsersParseArray;

    public static synchronized QBUsersHolder getInstance() {

        if (instance == null)

            instance = new QBUsersHolder();
        return instance;
    }

    private QBUsersHolder() {

        qbUsersParseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> users) {

        for (QBUser user : users) {

            putUser(user);
        }
    }

    public void putUser(QBUser user) {
        qbUsersParseArray.put(user.getId(), user);
    }

    public QBUser getUserById(int id) {
        return qbUsersParseArray.get(id);
    }

    public List<QBUser> getUserByIds(List<Integer> ids) {

        List<QBUser> qbUser = new ArrayList<>();
        for (Integer id : ids) {

            QBUser user = getUserById(id);
            if (user != null)
                qbUser.add(user);
        }
        return qbUser;
    }

    public ArrayList<QBUser> getAllUsers() {
        ArrayList<QBUser> result = new ArrayList<>();
        for (int i = 0; i < qbUsersParseArray.size(); i++)
            result.add(qbUsersParseArray.valueAt(i));
        return result;
    }
}
