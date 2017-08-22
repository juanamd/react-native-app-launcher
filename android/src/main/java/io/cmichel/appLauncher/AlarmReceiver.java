package io.cmichel.appLauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmID = intent.getAction();
        launchApplication(context, alarmID);
    }

    private void launchApplication(Context context, String alarmID) {
        SharedPreferences sharedPref = context.getSharedPreferences("io.cmichel.appLauncher", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alarmID", alarmID);
        editor.commit();

        ReactApplication reapp = ((ReactApplication) context.getApplicationContext());
        ReactInstanceManager manager = reapp.getReactNativeHost().getReactInstanceManager();
        ReactContext recontext = manager.getCurrentReactContext();
        if(recontext != null && recontext.hasActiveCatalystInstance()) {
            Log.i("ReactNativeAppLauncher", "AlarmReceiver: Sending react event");
            recontext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("alarmFired", null);
        }

        String packageName = context.getApplicationContext().getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(launchIntent);
        Log.i("ReactNativeAppLauncher", "AlarmReceiver: Launching: " + packageName);
    }
}

