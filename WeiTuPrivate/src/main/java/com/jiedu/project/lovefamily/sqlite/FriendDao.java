package com.jiedu.project.lovefamily.sqlite;

import com.jiedu.project.lovefamily.mode.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naruto on 2016/5/31.
 */
public interface FriendDao {

    public void addAll(List<User> list);

    public boolean deleteAll();

    public ArrayList<User> getAll();


}
