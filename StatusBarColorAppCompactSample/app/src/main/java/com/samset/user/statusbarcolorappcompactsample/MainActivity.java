package com.samset.user.statusbarcolorappcompactsample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       statusBarColor(this,R.color.colorPrimaryDark);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static View statusBarColor(final Activity act, final int colorResID ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {

                if (act.getWindow() != null) {

                    final ViewGroup vg = (ViewGroup) act.getWindow().getDecorView();
                    if (vg.getParent() == null && applyColoredStatusBar(act, colorResID)) {
                        final View statusBar = new View(act);

                        vg.post(new Runnable() {
                            @Override
                            public void run() {

                                int statusBarHeight = (int) Math.ceil(25 * vg.getContext().getResources().getDisplayMetrics().density);
                                statusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
                                statusBar.setBackgroundColor(act.getResources().getColor(colorResID));
                                vg.addView(statusBar, 0);
                            }
                        });
                        return statusBar;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (act.getWindow() != null) {
            act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            act.getWindow().setStatusBarColor(act.getResources().getColor(colorResID));
        }
        return null;
    }

    private static boolean applyColoredStatusBar( Activity act, int colorResID ) {
        final Window window = act.getWindow();
        final int flag;
        if (window != null) {
            View decor = window.getDecorView();
            if (decor != null) {
                flag = resolveTransparentStatusBar(act);

                if (flag != 0) {
                    decor.setSystemUiVisibility(flag);
                    return true;
                }
                else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    act.findViewById(android.R.id.content).setFitsSystemWindows(false);
                    setTranslucentStatus(window, true);

                }
            }
        }
        return false;
    }

    public static int resolveTransparentStatusBar(Context context ) {
        String[] libs = context.getPackageManager().getSystemSharedLibraryNames();
        String reflect = null;

        if (libs == null)
            return 0;

        final String SAMSUNG = "touchwiz";
        final String SONY = "com.sonyericsson.navigationbar";

        for (String lib : libs) {

            if (lib.equals(SAMSUNG)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
            }
            else if (lib.startsWith(SONY)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT";
            }
        }

        if (reflect == null)
            return 0;

        try {
            Field field = View.class.getField(reflect);
            if (field.getType() == Integer.TYPE) {
                return field.getInt(null);
            }
        } catch (Exception e) {
        }

        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setTranslucentStatus( Window win, boolean on ) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        }
        else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
