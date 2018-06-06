package com.example.yasmeen.advancedchatapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class UpdateBakingService extends IntentService {
    public static volatile boolean shouldContinue = true;
    public static String FROM_ACTIVITY_INGREDIENTS_LIST ="FROM_ACTIVITY_INGREDIENTS_LIST";

    public UpdateBakingService(){
        super("UpdateBakingService");

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateBakingService(String name) {
        super("UpdateBakingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doStuff();
        if (intent != null) {
            List<String> fromActivityIngredientsList = (List<String>) intent.getExtras().get(FROM_ACTIVITY_INGREDIENTS_LIST);
            handleActionUpdateBakingWidgets(fromActivityIngredientsList);

        }
    }
    public static void startBakingService(Context context, List<String> fromActivityIngredientsList) {
        Intent intent = new Intent(context, UpdateBakingService.class);
        intent.putExtra(FROM_ACTIVITY_INGREDIENTS_LIST, (Serializable) fromActivityIngredientsList);
        context.startService(intent);
    }
    private void handleActionUpdateBakingWidgets(List<String> fromActivityIngredientsList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra(FROM_ACTIVITY_INGREDIENTS_LIST, (Serializable) fromActivityIngredientsList);
        sendBroadcast(intent);
    }
    private void doStuff() {
        // do something

        // check the condition
        if (shouldContinue == false) {
            stopSelf();
            return;
        }

        // continue doing something

        // check the condition
        if (shouldContinue == false) {
            stopSelf();
            return;
        }

        // put those checks wherever you need
    }



}
