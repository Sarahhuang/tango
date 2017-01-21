package com.lvonasek.openconstructor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class AbstractActivity extends Activity
{
  protected static final int BUFFER_SIZE = 65536;
  protected static final String FILE_EXT = ".ply";
  protected static final String FILE_KEY = "FILE2OPEN";
  protected static final String MODEL_DIRECTORY = "/Models/";
  protected static final String RESOLUTION_KEY = "RESOLUTION";
  protected static final String TAG = "tango_app";
  protected static final String ZIP_TEMP = "upload.zip";
  protected static final int REQUEST_CODE_PERMISSION_CAMERA = 1987;
  protected static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 1988;
  protected static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 1989;
  protected static final int REQUEST_CODE_PERMISSION_INTERNET = 1990;

  public static boolean isPortrait(Context context) {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    String key = context.getString(R.string.pref_landscape);
    return !pref.getBoolean(key, false);
  }

  public boolean isNoiseFilterOn()
  {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    String key = getString(R.string.pref_noisefilter);
    return pref.getBoolean(key, false);
  }

  public boolean isPhotoModeOn()
  {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    String key = getString(R.string.pref_photomode);
    return pref.getBoolean(key, false);
  }

  public boolean isTexturingOn()
  {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    String key = getString(R.string.pref_texture);
    return pref.getBoolean(key, true);
  }

  public static void setOrientation(boolean portrait, Activity activity) {
    int value = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    if (!portrait)
      value = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    activity.setRequestedOrientation(value);
  }

  @Override
  protected void onResume() {
    super.onResume();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setOrientation(isPortrait(this), this);
  }

  public Uri filename2Uri(String filename) {
    if(filename == null)
      return null;
    return Uri.fromFile(new File(getPath(), filename));
  }

  public String getPath() {
    String dir = Environment.getExternalStorageDirectory().getPath() + MODEL_DIRECTORY;
    if (new File(dir).mkdir())
      Log.d(TAG, "Directory " + dir + "created");
    return dir;
  }

  protected void setupPermission(String permission, int requestCode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
        requestPermissions(new String[]{permission}, requestCode);
      else
        onRequestPermissionsResult(requestCode, null, new int[]{PackageManager.PERMISSION_GRANTED});
    } else
      onRequestPermissionsResult(requestCode, null, new int[]{PackageManager.PERMISSION_GRANTED});
  }

  protected void zip(String[] files, String zip) throws Exception {
    try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip))))
    {
      byte data[] = new byte[BUFFER_SIZE];
      for (String file : files)
      {
        FileInputStream fi = new FileInputStream(file);
        try (BufferedInputStream origin = new BufferedInputStream(fi, BUFFER_SIZE))
        {
          ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
          out.putNextEntry(entry);
          int count;
          while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
          {
            out.write(data, 0, count);
          }
        }
      }
    }
  }

  public abstract void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}
