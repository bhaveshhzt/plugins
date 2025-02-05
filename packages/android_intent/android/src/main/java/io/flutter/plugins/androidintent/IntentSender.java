package io.flutter.plugins.androidintent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Nullable;

/** Forms and launches intents. */
public final class IntentSender {
  private static final String TAG = "IntentSender";

  @Nullable private Activity activity;
  @Nullable private Context applicationContext;

  /**
   * Caches the given {@code activity} and {@code applicationContext} to use for sending intents
   * later.
   *
   * <p>Either may be null initially, but at least {@code applicationContext} should be set before
   * calling {@link #send}.
   *
   * <p>See also {@link #setActivity}, {@link #setApplicationContext}, and {@link #send}.
   */
  public IntentSender(@Nullable Activity activity, @Nullable Context applicationContext) {
    this.activity = activity;
    this.applicationContext = applicationContext;
  }

  /**
   * Creates and launches an intent with the given params using the cached {@link Activity} and
   * {@link Context}.
   *
   * <p>This will fail to create and send the intent if {@code applicationContext} hasn't been set
   * at the time of calling.
   *
   * <p>This uses {@code activity} to start the intent whenever it's not null. Otherwise it falls
   * back to {@code applicationContext} and adds {@link Intent#FLAG_ACTIVITY_NEW_TASK} to the intent
   * before launching it.
   *
   * @param action the Intent action, such as {@code ACTION_VIEW}.
   * @param flags forwarded to {@link Intent#addFlags(int)} if non-null.
   * @param category forwarded to {@link Intent#addCategory(String)} if non-null.
   * @param data forwarded to {@link Intent#setData(Uri)} if non-null.
   * @param arguments forwarded to {@link Intent#putExtras(Bundle)} if non-null.
   * @param packageName forwarded to {@link Intent#setPackage(String)} if non-null. This is forced
   *     to null if it can't be resolved.
   * @param componentName forwarded to {@link Intent#setComponent(ComponentName)} if non-null.
   */
  void send(
      String action,
      @Nullable Integer flags,
      @Nullable String category,
      @Nullable Uri data,
      @Nullable Bundle arguments,
      @Nullable String packageName,
      @Nullable ComponentName componentName,
      @Nullable String apkUrl) {
    if (applicationContext == null) {
      Log.wtf(TAG, "Trying to send an intent before the applicationContext was initialized.");
      return;
    }

    if(apkUrl == null) {
      Intent intent = new Intent(action);

      if (flags != null) {
        intent.addFlags(flags);
      }
      if (!TextUtils.isEmpty(category)) {
        intent.addCategory(category);
      }
      if (data != null) {
        intent.setData(data);
      }
      if (arguments != null) {
        intent.putExtras(arguments);
      }
      if (!TextUtils.isEmpty(packageName)) {
        intent.setPackage(packageName);
        if (componentName != null) {
          intent.setComponent(componentName);
        }
        if (intent.resolveActivity(applicationContext.getPackageManager()) == null) {
          Log.i(TAG, "Cannot resolve explicit intent - ignoring package");
          intent.setPackage(null);
        }
      }

      Log.v(TAG, "Sending intent " + intent);
      if (activity != null) {
        activity.startActivity(intent);
      } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
      }
    } else {
      Intent intent = IntentUtils.getOpenFileIntent(this.applicationContext, apkUrl);

      this.applicationContext.startActivity(intent);
      IntentUtils.validateIntent(this.applicationContext, intent);
    } 
  }

  /** Caches the given {@code activity} to use for {@link #send}. */
  void setActivity(@Nullable Activity activity) {
    this.activity = activity;
  }

  /** Caches the given {@code applicationContext} to use for {@link #send}. */
  void setApplicationContext(@Nullable Context applicationContext) {
    this.applicationContext = applicationContext;
  }
}
