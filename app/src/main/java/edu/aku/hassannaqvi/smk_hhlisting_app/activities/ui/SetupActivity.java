package edu.aku.hassannaqvi.smk_hhlisting_app.activities.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Clear;
import com.validatorcrawler.aliazaz.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS;
import edu.aku.hassannaqvi.smk_hhlisting_app.R;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.other.LoginActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.other.MainActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.ListingContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.DatabaseHelper;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp;
import edu.aku.hassannaqvi.smk_hhlisting_app.databinding.ActivitySetupBinding;
import edu.aku.hassannaqvi.smk_hhlisting_app.other.models.Members;

import static edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp.lc;
import static edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp.userEmail;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = SetupActivity.class.getName();
    private ActivitySetupBinding bi;
    private static String fmDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_setup);
        bi.setCallback(this);

        MainApp.hh07txt = "1";
        if (MainApp.hh02txt == null) {
            MainApp.hh03txt = 1;
        } else {
            MainApp.hh03txt++;
        }
        bi.hh03.setText(String.format(Locale.getDefault(), "%04d", MainApp.hh03txt));
        bi.hh04.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == bi.hh04h.getId() || checkedId == bi.hh04i.getId()) {
                Clear.clearAllFields(bi.fldGrpHH12);
                bi.fldGrpHH12.setVisibility(View.GONE);
                bi.btnNextStructure.setVisibility(View.GONE);
                bi.btnChangePSU.setVisibility(View.VISIBLE);
                if (bi.hh04h.isChecked())
                    bi.btnChangePSU.setText(R.string.logout);
                else
                    bi.btnChangePSU.setText(R.string.change_enumeration_block);
            } else if (checkedId == bi.hh04a.getId()) {
                bi.fldGrpHH12.setVisibility(View.VISIBLE);
                bi.btnChangePSU.setVisibility(View.GONE);
                bi.fldGrpHH04.setVisibility(View.VISIBLE);
                bi.btnNextStructure.setVisibility(View.GONE);
            } else {
                Clear.clearAllFields(bi.fldGrpHH12);
                bi.fldGrpHH12.setVisibility(View.GONE);
                bi.hh05.setChecked(false);
                bi.btnChangePSU.setVisibility(View.GONE);
                bi.btnNextStructure.setVisibility(View.VISIBLE);
            }
        });

        bi.hh05.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bi.hh06.setVisibility(View.VISIBLE);
                bi.hh06.requestFocus();
            } else {
                bi.hh06.setVisibility(View.GONE);
                bi.hh06.setText(null);
            }
        });

        bi.hh14.setOnCheckedChangeListener((group, checkedId) -> {
            if (bi.hh14a.isChecked()) {
                bi.fldGrpHH04.setVisibility(View.VISIBLE);
                bi.btnNextStructure.setVisibility(View.GONE);
            } else {
                Clear.clearAllFields(bi.fldGrpHH04);
                bi.fldGrpHH04.setVisibility(View.GONE);
                bi.hh05.setChecked(false);
                bi.hh06.setText(null);
                bi.btnNextStructure.setVisibility(View.VISIBLE);
            }
        });

        if (getIntent().getBooleanExtra(CONSTANTS.MAIN_DT_FLAG, false)) {
            bi.formdate.setEnabled(true);
        } else {
            bi.formdate.setText(fmDate);
        }

    }


    private void saveDraft() {
        lc = new ListingContract();
        SharedPreferences sharedPref = getSharedPreferences("tagName", MODE_PRIVATE);
        lc.setTagId(sharedPref.getString("tagName", null));
        lc.setAppVer(MainApp.appInfo.getAppVersion());
        lc.setHhDT(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date().getTime()));
        fmDate = bi.formdate.getText().toString();
        lc.setHhDT01(fmDate);
        lc.setEnumCode(MainApp.enumCode);
        lc.setClusterCode(Members.txtClusterCode.get());
        lc.setEnumStr(MainApp.enumStr);
        lc.setHh01(String.valueOf(MainApp.hh01txt));
        lc.setHh02(MainApp.hh02txt);
        lc.setHh03(String.valueOf(MainApp.hh03txt));
        lc.setHh04(
                bi.hh04a.isChecked() ? "1" :
                        bi.hh04b.isChecked() ? "2" :
                                bi.hh04c.isChecked() ? "3" :
                                        bi.hh04d.isChecked() ? "4" :
                                                bi.hh04e.isChecked() ? "5" :
                                                        bi.hh04f.isChecked() ? "6" :
                                                                bi.hh04h.isChecked() ? "8" :
                                                                        bi.hh04i.isChecked() ? "9" :
                                                                                bi.hh0496.isChecked() ? "96" :
                                                                                        "0");
        lc.setUsername(MainApp.userEmail);
        lc.setHh05(bi.hh05.isChecked() ? "1" : "2");
        lc.setHh06(Objects.requireNonNull(bi.hh06.getText()).toString());
        lc.setHh07(MainApp.hh07txt);
        lc.setDeviceID(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        lc.setHh08a1(bi.hh14a.isChecked() ? "1" : bi.hh14b.isChecked() ? "2" : "0");
        setGPS();
        MainApp.fTotal = bi.hh06.getText().toString().isEmpty() ? 0 : Integer.parseInt(bi.hh06.getText().toString());
    }


    private void setGPS() {
        SharedPreferences GPSPref = getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);
        try {
            String lat = GPSPref.getString("Latitude", "0");
            String lang = GPSPref.getString("Longitude", "0");
            String acc = GPSPref.getString("Accuracy", "0");
            String dt = GPSPref.getString("Time", "0");
            assert lat != null;
            assert lang != null;
            if (lat.equals("0") && lang.equals("0")) {
                Toast.makeText(this, "Could not obtained GPS points", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS set", Toast.LENGTH_SHORT).show();
            }
            String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(Objects.requireNonNull(GPSPref.getString("Time", "0")))).toString();
            lc.setGPSLat(GPSPref.getString("Latitude", "0"));
            lc.setGPSLng(GPSPref.getString("Longitude", "0"));
            lc.setGPSAcc(GPSPref.getString("Accuracy", "0"));
            lc.setGPSAlt(GPSPref.getString("Altitude", "0"));
            lc.setGPSTime(date); // Timestamp is converted to date above
            Toast.makeText(this, "GPS set", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "setGPS: " + e.getMessage());
        }

    }


    private boolean formValidation() {
        return Validator.emptyCheckingContainer(this, bi.fldGrpSecA01);
    }


    private boolean updateDB() {
        DatabaseHelper db = MainApp.appInfo.getDbHelper();
        long updcount = db.addForm(lc);
        lc.setID(String.valueOf(updcount));
        if (updcount > 0) {
            lc.setUID((lc.getDeviceID() + lc.getID()));
            db.updateListingUID();
            return true;
        } else {
            Toast.makeText(this, "Sorry. You can't go further.\n Please contact IT Team (Failed to update DB)", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void onBtnAddHHClick(View v) {
        if (MainApp.hh02txt == null)
            MainApp.hh02txt = bi.hh02.getText().toString();
        if (formValidation()) {
            saveDraft();
            MainApp.fCount++;
            finish();
            startActivity(new Intent(this, FamilyListingActivity.class));
        }
    }

    public void onBtnChangePSUClick(View v) {
        Intent fA = null;
        if (bi.hh04h.isChecked()) {
            fA = new Intent(this, LoginActivity.class);
        } else {
            saveDraft();
            if (updateDB()) {
                MainApp.hh02txt = null;
                fA = new Intent(this, MainActivity.class);
            }
        }
        if (fA != null) {
            finish();
            startActivity(fA);
        }
    }

    public void onBtnNextStructureClick(View v) {
        if (MainApp.hh02txt == null)
            MainApp.hh02txt = bi.hh02.getText().toString();
        if (formValidation()) {
            saveDraft();
            if (updateDB()) {
                MainApp.fCount = 0;
                MainApp.fTotal = 0;
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                finish();
                startActivity(new Intent(this, SetupActivity.class));
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (userEmail == null || userEmail.equals("")) {
            Toast.makeText(this, "Username not found. Kindly, re-start app!!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }
}


