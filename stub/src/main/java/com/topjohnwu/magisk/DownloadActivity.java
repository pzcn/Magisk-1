package com.topjohnwu.magisk;

import static android.R.string.no;
import static android.R.string.ok;
import static android.R.string.yes;
import static com.topjohnwu.magisk.R.string.dling;
import static com.topjohnwu.magisk.R.string.no_internet_msg;
import static com.topjohnwu.magisk.R.string.upgrade_msg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.topjohnwu.magisk.net.Networking;
import com.topjohnwu.magisk.net.Request;
import com.topjohnwu.magisk.utils.APKInstall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class DownloadActivity extends Activity {

    private static final String APP_NAME = "Magisk";
    private static final String CDN_URL = "https://cdn.jsdelivr.net/gh/vvb2060/magisk_files@%s/%s";

    private String apkLink;
    private String sha;
    private Context themed;
    private ProgressDialog dialog;
    private boolean dynLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DelegateClassLoader.cl instanceof AppClassLoader) {
            // For some reason activity is created before Application.attach(),
            // relaunch the activity using the same intent
            finishAffinity();
            startActivity(getIntent());
            return;
        }

        themed = new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault);

        // Only download and dynamic load full APK if hidden
        dynLoad = !getPackageName().equals(BuildConfig.APPLICATION_ID);

        // Inject resources
        loadResources();

        if (Networking.checkNetworkStatus(this)) {
            fetchAPKURL();
        } else {
            new AlertDialog.Builder(themed)
                    .setCancelable(false)
                    .setTitle(APP_NAME)
                    .setMessage(getString(no_internet_msg))
                    .setNegativeButton(ok, (d, w) -> finish())
                    .show();
        }
    }

    private void fetchAPKURL() {
        dialog = ProgressDialog.show(themed, "", "", true);
        String url = "https://api.github.com/repos/vvb2060/magisk_files/branches/alpha";
        request(url).getAsJSONObject(this::handleCanary);
    }

    private void error(Throwable e) {
        Log.e(getClass().getSimpleName(), "", e);
        finish();
    }

    private Request request(String url) {
        return Networking.get(url).setErrorHandler((conn, e) -> error(e));
    }

    private void handleCanary(JSONObject json) {
        try {
            sha = json.getJSONObject("commit").getString("sha");
            String url = String.format(CDN_URL, sha, "alpha.json");
            request(url).getAsJSONObject(this::handleJSON);
        } catch (JSONException e) {
            error(e);
        }
    }

    private void handleJSON(JSONObject json) {
        dialog.dismiss();
        try {
            apkLink = json.getJSONObject("magisk").getString("link");
            apkLink = String.format(CDN_URL, sha, apkLink);
            new AlertDialog.Builder(themed)
                    .setCancelable(false)
                    .setTitle(APP_NAME)
                    .setMessage(getString(upgrade_msg))
                    .setPositiveButton(yes, (d, w) -> dlAPK())
                    .setNegativeButton(no, (d, w) -> finish())
                    .show();
        } catch (JSONException e) {
            error(e);
        }
    }

    private void dlAPK() {
        dialog = ProgressDialog.show(themed, getString(dling), getString(dling) + " " + APP_NAME, true);
        // Download and upgrade the app
        var request = request(apkLink).setExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (dynLoad) {
            request.getAsFile(StubApk.current(this), file -> StubApk.restartProcess(this));
        } else {
            request.getAsInputStream(input -> {
                var session = APKInstall.startSession(this);
                try (input; var out = session.openStream(this)) {
                    if (out != null)
                        APKInstall.transfer(input, out);
                } catch (IOException e) {
                    error(e);
                }
                Intent intent = session.waitIntent();
                if (intent != null)
                    startActivity(intent);
            });
        }
    }

    private void loadResources() {
        File apk = new File(getCacheDir(), "res.apk");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey key = new SecretKeySpec(Bytes.key(), "AES");
            IvParameterSpec iv = new IvParameterSpec(Bytes.iv());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            var is = new CipherInputStream(new ByteArrayInputStream(Bytes.res()), cipher);
            var out = new FileOutputStream(apk);
            try (is; out) {
                APKInstall.transfer(is, out);
            }
            StubApk.addAssetPath(getResources().getAssets(), apk.getPath());
        } catch (Exception ignored) {
        }
    }

}
