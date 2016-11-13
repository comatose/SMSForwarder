package com.example.comatose.smsforwarder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSForwarder extends BroadcastReceiver {
    public SMSForwarder() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if(bundle == null)
                return;

            Object[] pdus= (Object[]) bundle.get("pdus");
            if(pdus == null)
                return;

            SmsMessage[] smsMessages = new SmsMessage[pdus.length];

            MatcherDatabase db = new MatcherDatabase(context);

            for (int i = 0; i < pdus.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String sender = smsMessages[i].getOriginatingAddress();
                String message = smsMessages[i].getMessageBody();

                Log.i("SMSForwarder", sender + " : " + message);

                MatcherDatabase.Matcher matcher = db.executeMatchers(message);
                if(matcher != null) {
                    Log.i("SMSForwarder", "found matcher: " + matcher.value);
                    sendGMail(context, "[" + sender + "] " + message);
                }
            }
        }
    }

    public void sendSMS(Context context, String smsNumber, String smsText){
        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
    }

    public void sendGMail(Context context, String text){
        try {
            GMailSender mailSender = new GMailSender("mailsender.for.gu@gmail.com", "bmdcibqjhhhgvtcz");
            mailSender.sendMail(text, text, "mailsender.for.gu@gmail.com", "borahong330@gmail.com");
            Log.i("SMSForwarder", "mail sent to her");
            mailSender.sendMail(text, text, "mailsender.for.gu@gmail.com", "boncheol.gu@gmail.com");
            Log.i("SMSForwarder", "mail sent to him");
            Toast.makeText(context, "Mail sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SMSForwarder", e.getMessage(), e);
        }
    }
}
