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
 * Ultra-lightweight Home Screen Widget: 4x2 / 4x1 Quick Tools Hub.
 *
 * Performance Optimizations:
 * • Uses standard Android RemoteViews with 0% background battery drain.
 * • No long-running background tasks or services.
 * • Static PendingIntents for instant 1-tap navigation into any tool.
 */
class QuickToolsWidgetProvider : AppWidgetProvider() {

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
        val views = RemoteViews(context.packageName, R.layout.widget_quick_tools)

        // Set click listeners for all 8 tools + Search
        views.setOnClickPendingIntent(
            R.id.btn_widget_search,
            createToolIntent(context, NavRoutes.HOME, 100)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_calc,
            createToolIntent(context, NavRoutes.TOOL_CALCULATOR, 101)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_flashlight,
            createToolIntent(context, NavRoutes.TOOL_SCREEN_LIGHT, 102)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_unit,
            createToolIntent(context, NavRoutes.TOOL_UNIT_CONVERTER, 103)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_notes,
            createToolIntent(context, NavRoutes.TOOL_TEXT_EDITOR, 104)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_qr,
            createToolIntent(context, NavRoutes.TOOL_QR_GENERATOR, 105)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_stopwatch,
            createToolIntent(context, NavRoutes.TOOL_STOPWATCH, 106)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_password,
            createToolIntent(context, NavRoutes.TOOL_PASSWORD_GENERATOR, 107)
        )
        views.setOnClickPendingIntent(
            R.id.btn_widget_bmi,
            createToolIntent(context, NavRoutes.TOOL_BMI_CALCULATOR, 108)
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
