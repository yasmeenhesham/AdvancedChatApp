package com.example.yasmeen.advancedchatapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class UpdateReqService extends IntentService {
    public static String FROM_ACTIVITY_INGREDIENTS_LIST = "FROM_ACTIVITY_INGREDIENTS_LIST";

    public UpdateReqService(String name) {
        super("UpdateReqService");
    }

    public UpdateReqService() {
        super("UpdateReqService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ArrayList<String> fromActivityIngredientsList = intent.getExtras().getStringArrayList(FROM_ACTIVITY_INGREDIENTS_LIST);
            handleActionUpdateBakingWidgets(fromActivityIngredientsList);
        }

    }

    public static void startBakingService(Context context,  ArrayList<String> fromActivityIngredientsList ) {
        Intent intent = new Intent(context, UpdateReqService.class);
        intent.putExtra(FROM_ACTIVITY_INGREDIENTS_LIST,  fromActivityIngredientsList);
        context.startService(intent);
    }

    private void handleActionUpdateBakingWidgets( ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra(FROM_ACTIVITY_INGREDIENTS_LIST, fromActivityIngredientsList);
        sendBroadcast(intent);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
