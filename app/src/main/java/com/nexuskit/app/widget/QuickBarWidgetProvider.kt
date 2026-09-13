package com.nexuskit.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nexuskit.app.MainActivity
import com.nexuskit.app.R
import com.nexuskit.app.navigation.NavRoutes

/**
 * Ultra-lightweight Home Screen Widget: 4x1 Quick Utilities Bar.
 *
 * Performance Optimizations:
 * • Minimal RAM footprint (< 40KB).
 * • Instant tool launching without background service overhead.
 */
class QuickBarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_quick_bar)

        views.setOnClickPendingIntent(
            R.id.btn_bar_search,
            createToolIntent(context, NavRoutes.HOME, 200)
        )
        views.setOnClickPendingIntent(
            R.id.btn_bar_calc,
            createToolIntent(context, NavRoutes.TOOL_CALCULATOR, 201)
        )
        views.setOnClickPendingIntent(
            R.id.btn_bar_flashlight,
            createToolIntent(context, NavRoutes.TOOL_SCREEN_LIGHT, 202)
        )
        views.setOnClickPendingIntent(
            R.id.btn_bar_stopwatch,
            createToolIntent(context, NavRoutes.TOOL_STOPWATCH, 203)
        )
        views.setOnClickPendingIntent(
            R.id.btn_bar_notes,
            createToolIntent(context, NavRoutes.TOOL_TEXT_EDITOR, 204)
        )

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun createToolIntent(context: Context, route: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_TOOL_ROUTE, route)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
