package edu.aku.hassannaqvi.smk_hhlisting_app.activities.other;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Validator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.aku.hassannaqvi.smk_hhlisting_app.R;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.map.MapsActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.menu.MenuActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.ui.SetupActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.EnumBlockContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.ListingContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VersionAppContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VerticesContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.AndroidDatabaseManager;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.AndroidUtilityKt;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp;
import edu.aku.hassannaqvi.smk_hhlisting_app.databinding.ActivityMainBinding;
import edu.aku.hassannaqvi.smk_hhlisting_app.other.models.Members;
import edu.aku.hassannaqvi.smk_hhlisting_app.repository.UtilsKt;
import edu.aku.hassannaqvi.smk_hhlisting_app.repository.WarningActivityInterface;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.REQUEST_APP_UPDATE;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.REQUEST_PSU_EXIST;
import static edu.aku.hassannaqvi.smk_hhlisting_app.core.DatabaseHelper.DATABASE_NAME;
import static edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp.appInfo;

public class MainActivity extends MenuActivity implements WarningActivityInterface {

    public static String TAG = MainActivity.class.getName();
    private static File file;
    private Boolean exit = false;
    private DownloadManager downloadManager;
    private SharedPreferences sharedPrefDownload;
    private SharedPreferences.Editor editorDownload;
    private ActivityMainBinding bi;
    private String preVer = "", newVer = "", clusterName;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(sharedPrefDownload.getLong("refID", 0));

                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                assert downloadManager != null;
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int colIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(colIndex)) {

                        editorDownload.putBoolean("flag", true);
                        editorDownload.commit();

                        Toast.makeText(context, "New App downloaded!!", Toast.LENGTH_SHORT).show();
                        bi.lblAppVersion.setText(new StringBuilder(getString(R.string.app_name) + " App New Version ").append(newVer).append("  Downloaded"));

                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

                        if (Objects.requireNonNull(taskInfo.get(0).topActivity).getClassName().equals(MainActivity.class.getName())) {
                            showDialog(newVer, preVer);
                        }
                    }
                }
            }
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem dbManager = menu.findItem(R.id.menu_openDB);
        dbManager.setVisible(MainApp.admin);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bi.setCallback(this);
        bi.adminBlock.setVisibility(MainApp.admin ? View.VISIBLE : View.GONE);

        //Database handler
        bi.msgText.setText(String.format("%s records found in Listings table.", appInfo.getDbHelper().getListingCount()));

        // Auto download app
        sharedPrefDownload = getSharedPreferences("appDownload", MODE_PRIVATE);
        editorDownload = sharedPrefDownload.edit();
        VersionAppContract versionAppContract = appInfo.getDbHelper().getVersionApp();
        if (versionAppContract.getVersioncode() != null) {
            preVer = appInfo.getVersionName() + "." + appInfo.getVersionCode();
            newVer = versionAppContract.getVersionname() + "." + versionAppContract.getVersioncode();
            if (appInfo.getVersionCode() < Integer.parseInt(versionAppContract.getVersioncode())) {
                bi.lblAppVersion.setVisibility(View.VISIBLE);

                String fileName = DATABASE_NAME.replace(".db", "_New_Apps");
                file = new File(Environment.getDataDirectory() + File.separator + fileName, versionAppContract.getPathname());

                if (file.exists()) {
                    bi.lblAppVersion.setText(new StringBuilder(getString(R.string.app_name)).append("\nNew Ver.").append(newVer).append("  is downloaded. Kindly accept the installation prompt."));
                    showDialog(newVer, preVer);
                } else {
                    if (!AndroidUtilityKt.isNetworkConnected(this)) {
                        bi.lblAppVersion.setText(new StringBuilder(getString(R.string.app_name)).append(" App New Ver.").append(newVer).append("  is available..\n(Couldn't able to download due to Internet connectivity issue!!)"));
                        return;
                    }
                    bi.lblAppVersion.setText(new StringBuilder(getString(R.string.app_name)).append(" App \nNew Ver.").append(newVer).append("  is downloading.."));
                    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(MainApp._UPDATE_URL + versionAppContract.getPathname());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(fileName, versionAppContract.getPathname())
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setTitle("Downloading updated version of " + getString(R.string.app_name) + " Ver." + newVer);
                    long refID = downloadManager.enqueue(request);
                    editorDownload.putLong("refID", refID);
                    editorDownload.putBoolean("flag", false);
                    editorDownload.apply();
                }

            } else {
                bi.lblAppVersion.setVisibility(View.GONE);
                bi.lblAppVersion.setText(null);
            }
        }
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

//        Testing visibility
        if (Integer.parseInt(appInfo.getVersionName().split("\\.")[0]) > 0)
            bi.testing.setVisibility(View.GONE);
        else
            bi.testing.setVisibility(View.VISIBLE);

        setUIContent();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void callWarningActivity(int id) {
        if (id == REQUEST_APP_UPDATE) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == REQUEST_PSU_EXIST) {
            startActivity(new Intent(MainActivity.this, SetupActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }


    //Screen Buttons
    public void CheckClusterBtn(View v) {

        if (MainApp.userEmail.equals("0000")) {
            finish();
            return;
        }

        if (!Validator.emptyCheckingContainer(this, bi.txtPSU)) return;

        boolean loginFlag;
        String subCluster = bi.txtPSU.getText().toString();
        int cluster = Integer.parseInt(subCluster.substring(subCluster.length() - 3));
        if (cluster < 900) {
            loginFlag = !(MainApp.userEmail.equals("test1234") || MainApp.userEmail.equals("dmu@aku") || MainApp.userEmail.substring(0, 4).equals("user"));
        } else {
            loginFlag = MainApp.userEmail.equals("test1234") || MainApp.userEmail.equals("dmu@aku") || MainApp.userEmail.substring(0, 4).equals("user");
        }
        if (loginFlag) {
            gettingEnumClusterData(bi.txtPSU.getText().toString());
        } else {
            Toast.makeText(this, "Can't proceed test cluster for current user!!", Toast.LENGTH_SHORT).show();
            onSettingVisibilityContent(View.GONE);
        }

    }

    public void OpenClusterMapBtn(View view) {
        Collection<VerticesContract> v = appInfo.getDbHelper().getVerticesByCluster(clusterName);
        if (v.size() > 3) {
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            Toast.makeText(this, "Cluster map do not exist for " + clusterName, Toast.LENGTH_SHORT).show();
        }
    }

    public void StartListingBtn(View view) {
        if (MainApp.PSUExist(MainApp.enumCode)) {
            alertPSU();
        } else {
            startActivity(new Intent(this, SetupActivity.class));
        }
    }

    public void OpenDBManagerBtn(View view) {
        startActivity(new Intent(this, AndroidDatabaseManager.class));
    }

    public void CopyDataToFileBtn(View view) {
        new CopyTask(this).execute();
    }


    //TextWatchers
    public void txtPSUOnTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onSettingVisibilityContent(View.GONE);
    }


    //Other Dependent Functions
    private void setUIContent() {
        bi.chkconfirm.setOnCheckedChangeListener((compoundButton, b) -> {
            if (bi.chkconfirm.isChecked()) {
                bi.openForm.setBackgroundColor(getResources().getColor(R.color.green));
                bi.lllstwarning.setVisibility(View.VISIBLE);
                MainApp.hh01txt = 1;
            } else {
                bi.lllstwarning.setVisibility(View.GONE);
            }
        });
    }

    private void onSettingVisibilityContent(int visibility) {
        bi.fldGrpna101.setVisibility(visibility);
        bi.chkconfirm.setChecked(false);
    }

    private void alertPSU() {
        UtilsKt.openWarningActivity(
                this,
                REQUEST_PSU_EXIST,
                "WARNING",
                "PSU data already exist. Are you sure to continue this cluster?",
                "Yes",
                "Cancel"
        );
    }

    private void showDialog(String newVer, String preVer) {
        UtilsKt.openWarningActivity(
                this,
                REQUEST_APP_UPDATE,
                getString(R.string.app_name) + " APP is available!",
                getString(R.string.app_name) + " App Ver." + newVer + " is now available. Your are currently using older Ver." + preVer + ".\nInstall new version to use this app.",
                "Install",
                "Cancel"
        );
    }


    //Reactive Streams
    private Observable<EnumBlockContract> getEnumBlockForCluster(String clusterNo) {
        return Observable.create(emitter -> {
            emitter.onNext(appInfo.getDbHelper().getEnumBlock(clusterNo));
            emitter.onComplete();
        });
    }


    //Getting data from db
    public void gettingEnumClusterData(String clusterNo) {
        getEnumBlockForCluster(clusterNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EnumBlockContract>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(EnumBlockContract enumContract) {
                        String selected = enumContract.getGeoarea();
                        if (!selected.equals("")) {
                            String[] selSplit = selected.split("\\|");
                            bi.na101a.setText(selSplit[0]);
                            bi.na101b.setText(selSplit[1].equals("") ? "----" : selSplit[1]);
                            bi.na101c.setText(selSplit[2].equals("") ? "----" : selSplit[2]);
                            bi.na101d.setText(selSplit[3]);
                            clusterName = selSplit[3];

                            onSettingVisibilityContent(View.VISIBLE);

                            MainApp.hh02txt = bi.txtPSU.getText().toString();
                            MainApp.enumCode = enumContract.getCluster();
                            MainApp.enumStr = enumContract.getGeoarea();
                            Members.txtClusterCode.set(bi.txtPSU.getText().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Sorry not found any block", Toast.LENGTH_SHORT).show();
                        onSettingVisibilityContent(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });
    }


    //Async tasks
    public static class CopyTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog Asycdialog;
        private Context mContext;

        public CopyTask(Context mContext) {
            this.mContext = mContext;
            Asycdialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Asycdialog.setTitle("COPYING DATA");
            Asycdialog.setMessage("Loading...");
            Asycdialog.setCancelable(false);
            Asycdialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // do the task you want to do. This will be executed in background.
            try {

                File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)
                        + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        + "_" + ListingContract.ListingEntry.TABLE_NAME);
                FileWriter writer = new FileWriter(gpxfile);

                Collection<ListingContract> listing = appInfo.getDbHelper().getUnsyncedListings();
                if (listing.size() > 0) {
                    JSONArray jsonSync = new JSONArray();
                    for (ListingContract fc : listing) {
                        jsonSync.put(fc.toJSONObject());
                    }

                    writer.append(String.valueOf(jsonSync));
                    writer.flush();
                    writer.close();

                    if (listing.size() < 100) {
                        Thread.sleep(3000);
                    }
                }
            } catch (JSONException | IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Asycdialog.dismiss();
            Toast.makeText(mContext, "Copying done!!", Toast.LENGTH_SHORT).show();
        }
    }

}
