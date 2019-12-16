package io.flutter.plugins.androidintent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class IntentUtils {

  public static synchronized Intent getOpenFileIntent(Context context, String path) {
    File file = new File(path);
    Intent intent = new Intent(Intent.ACTION_VIEW);

    if (Build.VERSION.SDK_INT >= 24) {
      Uri apkURI = FileProvider.getUriForFile(
              context,
              context.getPackageName() + ".flutter_downloader.provider", file);
      intent.setDataAndType(apkURI,
              "application/vnd.android.package-archive");
    } else {
      intent.setDataAndType(Uri.fromFile(file),
              "application/vnd.android.package-archive");
    }

    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return intent;
  }

  public static synchronized boolean validateIntent(Context context, Intent intent) {
    PackageManager manager = context.getPackageManager();
    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
    if (infos.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

}