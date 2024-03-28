package com.example.bodyboost.Receivers;

import static com.example.bodyboost.Helpers.NotificationHelper.DOSE_TIME;
import static com.example.bodyboost.Helpers.NotificationHelper.MEDICATION_ID;
import static com.example.bodyboost.Helpers.NotificationHelper.NOTIFICATION_ID;
import static com.example.bodyboost.Helpers.NotificationHelper.clearPendingNotifications;
import static com.example.bodyboost.Helpers.NotificationHelper.createNotifications;
import static com.example.bodyboost.Helpers.NotificationHelper.scheduleIn15Minutes;
import static com.example.bodyboost.Services.NotificationService.SNOOZE_ACTION;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.bodyboost.Helpers.DBHelper;
import com.example.bodyboost.Helpers.TimeFormatting;
import com.example.bodyboost.Services.NotificationService;
import com.example.bodyboost.SimpleClasses.Medication;

public class EventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final DBHelper db = new DBHelper(context);
        ArrayList<Medication> medications = db.getMedications();

        if (intent.getAction().contains(NotificationService.MARK_AS_TAKEN_ACTION)) {
            String medId = "_" + intent.getAction().split("_")[1];

            markDoseTaken(
                    context,
                    intent.getLongExtra(NOTIFICATION_ID + medId, 0),
                    intent.getLongExtra(MEDICATION_ID + medId, 0),
                    intent.getStringExtra(DOSE_TIME + medId),
                    db
            );

            return;
        } else if (intent.getAction().contains(SNOOZE_ACTION)) {
            String medId = "_" + intent.getAction().split("_")[1];

            snoozeFor15(
                    context,
                    intent.getLongExtra(NOTIFICATION_ID + medId, 0),
                    intent.getLongExtra(MEDICATION_ID + medId, 0),
                    intent.getStringExtra(DOSE_TIME + medId),
                    db
            );

            return;
        }

        for (final Medication medication : medications) {
            prepareNotification(context, db, medication);
        }

        db.close();
    }

    /**
     * Prepares pending notifications on boot
     *
     * @param context    Notification context
     * @param db         DBHelper containing medication data
     * @param medication Medication in need of a notification
     */
    private void prepareNotification(Context context, DBHelper db, Medication medication) {
        clearPendingNotifications(medication, context);
        createNotifications(medication, context);
    }

    /**
     * Marks a dose as taken from the notification
     *
     * @param context        Application context
     * @param notificationId Id of notification to cancel
     * @param medId          ID of medication taken
     * @param doseTimeString Dose time for DB.
     */
    private void markDoseTaken(Context context, long notificationId, long medId, String doseTimeString, DBHelper db) {
        Medication med;
        LocalDateTime doseTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            doseTime = LocalDateTime.parse(doseTimeString);
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        med = db.getMedication(medId);

        long doseId = db.isInMedicationTracker(med, doseTime) ?
                db.getDoseId(med.getId(), TimeFormatting.localDateTimeToString(doseTime)) :
                db.addToMedicationTracker(med, doseTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            db.updateDoseStatus(
                    doseId, TimeFormatting.localDateTimeToString(LocalDateTime.now()), true
            );
        }

        notificationManager.cancel((int) notificationId);
        db.close();
    }

    private void snoozeFor15(Context context, long notificationId, long medId, String doseTimeString, DBHelper db) {
        Medication med;
        LocalDateTime doseTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            doseTime = LocalDateTime.parse(doseTimeString);
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        med = db.getMedication(medId);

        scheduleIn15Minutes(
                context,
                med,
                doseTime,
                notificationId
        );

        notificationManager.cancel((int) notificationId);
        db.close();
    }
}
