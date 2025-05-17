package com.example.barnaoszkarja7nv5csaladfa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

/**
 * Háttérszolgáltatás (Service), amely értesítéseket küld a családtagok mentéséről.
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "family_member_channel";
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Szerviz elindult");
        
        if (intent != null && intent.hasExtra("member_name")) {
            String memberName = intent.getStringExtra("member_name");
            sendNotification(memberName);
        }
        
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Családtag Értesítések",
                    NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Értesítések a családtagok mentéséről");
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setShowBadge(true);
                
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            } catch (Exception e) {
                Log.e(TAG, "Hiba az értesítési csatorna létrehozásakor: " + e.getMessage());
            }
        }
    }

    private void sendNotification(String memberName) {
        try {
            // Főképernyőre való ugrás intent
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_IMMUTABLE
            );

            // Értesítés létrehozása
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Családtag Mentve")
                .setContentText(memberName + " sikeresen mentve!")
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(memberName + " sikeresen mentve a családfába!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            // Értesítés megjelenítése
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            
            // Ellenőrizzük a jogosultságot
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                } else {
                    Log.w(TAG, "Nincs értesítési jogosultság");
                }
            } else {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        } catch (Exception e) {
            Log.e(TAG, "Hiba az értesítés küldésekor: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Szerviz leállt");
    }
} 