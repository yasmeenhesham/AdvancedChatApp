package com.example.yasmeen.advancedchatapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class ListProvider extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ChatRemoteView(this.getApplicationContext(),intent);
    }
}

class ChatRemoteView implements RemoteViewsService.RemoteViewsFactory
{
    Context mContext=null;
    private List<String> mIngredientsArrayList ;


    public ChatRemoteView(Context context,Intent intent)
    {
        this.mContext=context;
//        if(BakingWidgetProvider.mRecipeSelected != null) {
//            mIngredientsArrayList = BakingWidgetProvider.ingredientsList;
//        }
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        //if(BakingWidgetProvider.mRecipeSelected != null) {
        mIngredientsArrayList = ChatWidget.ingredientsList;
        //  }
//        else
//        {
//            mIngredientsArrayList=null;
//        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredientsArrayList==null)
            return 0;
        else
            return mIngredientsArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views= new RemoteViews(mContext.getPackageName(), R.layout.chat_widget_item);
        views.setTextViewText(R.id.widget_grid_view_item,mIngredientsArrayList.get(position));
        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.widget_grid_view_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {



        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
