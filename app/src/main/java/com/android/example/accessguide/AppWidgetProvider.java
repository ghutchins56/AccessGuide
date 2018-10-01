package com.android.example.accessguide;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    private String PlaceName = "";
    private String Access;
    private static final String EXTRA_PLACE_NAME = "placeName";
    private static final String EXTRA_ACCESS = "access";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(EXTRA_PLACE_NAME)) {
            PlaceName = intent.getStringExtra(EXTRA_PLACE_NAME);
        }
        if (intent.hasExtra(EXTRA_ACCESS)) {
            Access = intent.getStringExtra(EXTRA_ACCESS);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.app_widget_access_guide);
            if (PlaceName.equals("")) {
                views.setViewVisibility(R.id.widget_image_view, View.VISIBLE);
                views.setViewVisibility(R.id.widget_text_view_name, View.GONE);
                views.setViewVisibility(R.id.widget_text_view_access, View.GONE);
            } else {
                views.setViewVisibility(R.id.widget_image_view, View.GONE);
                views.setTextViewText(R.id.widget_text_view_name, PlaceName);
                views.setViewVisibility(R.id.widget_text_view_name, View.VISIBLE);
                views.setTextViewText(R.id.widget_text_view_access, Access);
                views.setViewVisibility(R.id.widget_text_view_access, View.VISIBLE);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

