package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SelectPagerAdapter extends FragmentPagerAdapter{
    Context context;
    public SelectPagerAdapter(FragmentManager fm, Context context) {

        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
                FriendRequestsFragment mFriendRequestsFragment= new FriendRequestsFragment();
                return mFriendRequestsFragment;
            case 1:
                ChatsFragment mChatsFragment = new ChatsFragment();
                return mChatsFragment;
            case 2:
                FriendsFragment mFriendsFragment = new FriendsFragment();
                return mFriendsFragment;
            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return context.getString(R.string.requests);
            case 1:
                return context.getString(R.string.chat);
            case 2:
                return context.getString(R.string.friends);
            default:
                return null;
        }
    }
}
