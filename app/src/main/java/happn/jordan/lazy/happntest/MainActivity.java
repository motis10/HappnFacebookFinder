package happn.jordan.lazy.happntest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends Activity {

    public static final String NOT_EXIST = "NOT_EXIST";
    public static final String FACEBOOK_URL = "http://www.facebook.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        View button = findViewById(R.id.getLastFbId);
        final WebView webView = (WebView) findViewById(R.id.facebookWebView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Loading...", "");

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        final String fbId = findFacebookId();
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (fbId == null) {
                                    Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT);
                                } else if (!fbId.equals(NOT_EXIST)) {
                                    webView.loadUrl(FACEBOOK_URL + fbId);
                                } else {
                                    Toast.makeText(MainActivity.this, "Facebook page not exist", Toast.LENGTH_SHORT);
                                }

                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                };
                mThread.start();
            }

            private String findFacebookId() {

                try {
                    runShellCommand("cp -f data/data/com.ftw_and_co.happn/databases/happn.db /sdcard/");
                } catch (Exception e) {
                    Log.e("Error comando Root", e.getMessage());
                }

                File dir = Environment.getExternalStorageDirectory();
                File mypath = new File(dir, "happn.db");
                JSONObject fullJson;
                String fbId = null;
                long modification_date = -1L;
                final SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(mypath.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                if (myDataBase != null) {
                    Cursor user = myDataBase.query("user", null, null, null, null, null, null);
                    if (user != null && user.getCount() > -1 && user.moveToFirst()) {
                        try {
                            while (!user.isAfterLast()) {
                                String userJson = user.getString(user.getColumnIndex("serialized_object"));
                                if (userJson != null) {
                                    fullJson = new JSONObject(userJson);
                                    final long currentModificationDate = getModificationDate(fullJson);
                                    if (currentModificationDate > modification_date && isNotMe(fullJson)) {
                                        modification_date = currentModificationDate;
                                        final String currentFbId = getFacebookId(fullJson);
                                        if (currentFbId != null) {
                                            fbId = currentFbId;
                                        } else {
                                            fbId = NOT_EXIST;
                                        }
                                    }
                                    user.moveToNext();
                                }
                            }
                        } catch (JSONException ignored) {
                        }
                    }
                }

                return fbId;
            }
        });
    }

    private boolean isNotMe(JSONObject json) {
        return !(json.has("achievements"));
    }

    private long getModificationDate(JSONObject json) {
        if (json.has("merge_timestamp")) {
            try {
                return json.getLong("merge_timestamp");
            } catch (JSONException e) {
            }
        }

        return -1;
    }

    private String getFacebookId(JSONObject json) {
        if (json.has("social_synchronization")) {
            JSONObject social_synchronization = null;
            try {
                social_synchronization = json.getJSONObject("social_synchronization");
                if (social_synchronization.has("facebook")) {
                    JSONObject facebook = social_synchronization.getJSONObject("facebook");
                    if (facebook.has("id")) {
                        String fbId = facebook.getString("id");
                        try {
                            return new BlowfishDecrypter().decrypt(fbId).trim();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException ignored) {
            }
        }

        return null;
    }

    private void runShellCommand(String command) throws Exception {
        Process suProcess = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
        os.writeBytes(command + "\n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        try {
            int suProcessRetval = suProcess.waitFor();
            if (255 != suProcessRetval) {
                // Acceso Root concedido
            } else {
                // Acceso Root denegado
            }
        } catch (Exception ex) {
            Log.e("Error comando Root", ex.getMessage());
        }
    }
}
