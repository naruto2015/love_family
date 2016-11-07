package com.jiedu.project.lovefamily.sqlite;

import com.jiedu.project.lovefamily.mode.User;

/**
 * Created by naruto on 2016/5/24.
 */
public interface UserDao {

    public void insertUserInfo(User user);

    public User selectUserInfo();

    public void updateUserInfo(User user);

    public void deleteAllUserInfo();

    public int selectUserInfoFrequency();

}
