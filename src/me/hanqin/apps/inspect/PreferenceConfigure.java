package me.hanqin.apps.inspect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.test.InstrumentationTestRunner;

/**
 * Guide for using this tool to setup the data server url
 * 1, build HHT5_Core, install
 * ant debug; ant installd
 * 2, build HHT5_Config (this project), install
 * ant debug; ant installd
 * 3, adb shell am instrument -e tcp '10.18.2.31:10006' me.hanqin.apps.inspect/me.hanqin.apps.inspect.PreferenceConfigure
 * Note, please update the address quoted above
 * <p/>
 */
public class PreferenceConfigure extends InstrumentationTestRunner {

    @Override
    public void onCreate(Bundle arguments) {
        Context targetContext = getTargetContext();
        SharedPreferences preferences = targetContext.getSharedPreferences("TcpServer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TcpServerAddress", arguments.getString("tcp"));
        editor.commit();
    }
}
