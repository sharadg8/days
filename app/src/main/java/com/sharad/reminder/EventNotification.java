package com.sharad.reminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.sharad.days.DetailsActivity;
import com.sharad.days.Event;
import com.sharad.days.R;


/**
 * Helper class for showing and canceling event
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class EventNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Event";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p/>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of event notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context, final Event event) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableNotify = prefs.getBoolean("notifications_enable", true);

        if(enableNotify) {
            final Resources res = context.getResources();
            final Bitmap picture = getNotificationBitmap(res, event);
            final String ticker = event.get_title();
            final String title = event.get_title();
            final String text = event.get_dateText();

            String strRingtonePreference = prefs.getString("notifications_ringtone", "DEFAULT_SOUND");
            boolean vibrate = prefs.getBoolean("notifications_vibrate", true);
            long[] vibPattern = {0, 0, 0};
            if(vibrate) {
                vibPattern[0] = vibPattern[1] = vibPattern[2] = 500;
            }

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                    // Set appropriate defaults for the notification light, sound,
                    // and vibration.
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setSmallIcon(R.drawable.ic_today_black_24dp)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setLights(Color.BLUE, 500, 500)
                    .setSound(Uri.parse(strRingtonePreference))
                    .setVibrate(vibPattern)

                            // All fields below this line are optional.

                            // Use a default priority (recognized on devices running Android
                            // 4.1 or later)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                            // Provide a large icon, shown with the notification in the
                            // notification drawer on devices running Android 3.0 or later.
                    .setLargeIcon(picture)

                            // Set ticker text (preview) information for this notification.
                    .setTicker(ticker)

                            // If this notification relates to a past or upcoming event, you
                            // should set the relevant time information using the setWhen
                            // method below. If this call is omitted, the notification's
                            // timestamp will by set to the time at which it was shown.
                            // TODO: Call setWhen if this notification relates to a past or
                            // upcoming event. The sole argument to this method should be
                            // the notification timestamp in milliseconds.
                            //.setWhen(...)

                            // Set the pending intent to be initiated when the user touches
                            // the notification.
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    new Intent(context, DetailsActivity.class)
                                            .putExtra(DetailsActivity.ID_KEY, event.get_id()),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);
            //notify(context, builder.build());

            NotificationManager notificationMgr = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationMgr.notify((int) event.get_id(), builder.build());
        }
    }

    private static Bitmap getNotificationBitmap(Resources res, Event event) {
        int size = 120;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(event.get_colorId());
        paint.setAlpha(160);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        Bitmap icon = BitmapFactory.decodeResource(res, event.get_favorite());
        paint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        paint.setAlpha(255);
        int posX = (size - icon.getWidth()) / 2;
        int posY = (size - icon.getHeight()) / 2;
        canvas.drawBitmap(icon, posX, posY, paint);
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}