package edu.aku.hassannaqvi.smk_hhlisting_app.activities.other;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.validatorcrawler.aliazaz.Validator;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS;
import edu.aku.hassannaqvi.smk_hhlisting_app.R;
import edu.aku.hassannaqvi.smk_hhlisting_app.activities.sync.SyncActivity;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.DistrictContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.DatabaseHelper;
import edu.aku.hassannaqvi.smk_hhlisting_app.core.MainApp;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;

import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.LOGIN_SPLASH_FLAG;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.MINIMUM_DISTANCE_CHANGE_FOR_UPDATES;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.MINIMUM_TIME_BETWEEN_UPDATES;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.MY_PERMISSIONS_REQUEST_READ_CONTACTS;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE;
import static edu.aku.hassannaqvi.smk_hhlisting_app.CONSTANTS.TWO_MINUTES;
import static edu.aku.hassannaqvi.smk_hhlisting_app.repository.SplashRepositoryKt.populatingSpinners;
import static edu.aku.hassannaqvi.smk_hhlisting_app.repository.UtilsKt.dbBackup;
import static edu.aku.hassannaqvi.smk_hhlisting_app.repository.UtilsKt.getPermissionsList;
import static java.lang.Thread.sleep;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static LocationManager locationManager;
    // UI references.
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.txtinstalldate)
    TextView txtinstalldate;
    @BindView(R.id.email_sign_in_button)
    ImageButton mEmailSignInButton;
    @BindView(R.id.syncData)
    ImageButton syncData;
    @BindView(R.id.showPassword)
    ImageButton showPassword;
    @BindView(R.id.signup)
    Button signup;
    @BindView(R.id.testing)
    TextView testing;
    Location location;
    DatabaseHelper db;
    ArrayAdapter<String> provinceAdapter;
    @BindView(R.id.spinnerProvince)
    Spinner spinnerProvince;
    @BindView(R.id.spinners)
    LinearLayout spinners;
    @BindView(R.id.spinnerDistrict)
    Spinner spinnerDistrict;
    private UserLoginTask mAuthTask = null;
    private int clicks;

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        try {

            String packageName = this.getPackageName();

            long installedOn = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .lastUpdateTime;
            MainApp.versionCode = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .versionCode;
            MainApp.versionName = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .versionName;
            txtinstalldate.setText("Ver. " + MainApp.versionName + "." + MainApp.versionCode + " \r\n( Last Updated: " + new SimpleDateFormat("dd MMM. yyyy").format(new Date(installedOn)) + " )");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                populateAutoComplete();
                loadIMEI();
            }
        } else {
            populateAutoComplete();
            loadIMEI();
        }

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        setListeners();

//        DB backup
        dbBackup(this);

        db = new DatabaseHelper(this);

//        Testing visibility
        if (Integer.parseInt(MainApp.versionName.split("\\.")[0]) > 0) {
            testing.setVisibility(View.GONE);
        } else {
            testing.setVisibility(View.VISIBLE);
        }

    }

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.
                requestReadPhoneStatePermission();
            } else {
                doPermissionGrantedStuffs();
            }
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void setListeners() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SplashscreenActivity.provinces);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                List<String> districts = new ArrayList<>(Collections.singletonList("...."));
                for (Map.Entry<String, Pair<String, DistrictContract>> entry : SplashscreenActivity.districtsMap.entrySet()) {
                    if (entry.getValue().getFirst().equals(spinnerProvince.getSelectedItem().toString()))
                        districts.add(entry.getKey());
                }
                spinnerDistrict.setAdapter(new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1
                        , districts));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                MainApp.DIST_ID = Objects.requireNonNull(SplashscreenActivity.districtsMap.get(spinnerDistrict.getSelectedItem().toString())).getSecond().getDist_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("permission read phone state rationale")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE))
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @OnClick(R.id.syncData)
    void onSyncDataClick() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            startActivity(new Intent(this, SyncActivity.class).putExtra(CONSTANTS.SYNC_LOGIN, true));
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        if (!getPermissionsList(this).isEmpty()) {
            ActivityCompat.requestPermissions(this, getPermissionsList(this).toArray(new String[getPermissionsList(this).size()]),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return false;
        }

        return true;
    }

    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.READ_CONTACTS:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        populateAutoComplete();
                    }
                    break;
                case Manifest.permission.GET_ACCOUNTS:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        doPermissionGrantedStuffs();
                    }
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        requestLocationUpdate();
                    }
                    break;
            }

        }
    }

    private void populateAutoComplete() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                checkAndRequestPermissions();
            }
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (!Validator.emptyCheckingContainer(this, spinners))
                return;
            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @OnClick(R.id.showPassword)
    void onShowPasswordClick() {
        //TODO implement
        AnimatedVectorDrawable drawable;

        if (mPasswordView.getTransformationMethod() == null) {

            drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_anim_close);
            showPassword.setImageDrawable(drawable);
            drawable.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
                }
            }, 1000);


        } else {

            drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_anim);
            showPassword.setImageDrawable(drawable);
            drawable.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasswordView.setTransformationMethod(null);
                }
            }, 1000);

        }
    }

    public void showCredits(View view) {
        if (clicks < 5) {
            clicks++;
            Toast.makeText(this, String.valueOf(clicks), Toast.LENGTH_SHORT).show();
        } else {
            clicks = 0;
            Toast.makeText(this, "TEAM CREDITS: " +
                            "\r\nHassan Naqvi, " +
                            "Ali Azaz, " +
                            "Abdul Sajid, " +
                            "Farooqui",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(Locale.getDefault(),
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            //Toast.makeText(this, message,
            //Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    private void callingCoroutine() {
        //To call coroutine here
        populatingSpinners(this, provinceAdapter, new SplashscreenActivity.Continuation<Unit>() {
            @Override
            public void resume(Unit value) {

            }

            @Override
            public void resumeWithException(@NotNull Throwable exception) {

            }

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return null;
            }
        });
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        MainApp.IMEI = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        MainApp.IMEI = getDeviceId(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(LOGIN_SPLASH_FLAG, false))
            callingCoroutine();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSTANTS.LOGIN_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                callingCoroutine();
            }
        }
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(@NotNull Location location) {

            SharedPreferences sharedPref = getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String dt = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(sharedPref.getString("Time", "0"))).toString();

            Location bestLocation = new Location("storedProvider");
            bestLocation.setAccuracy(Float.parseFloat(sharedPref.getString("Accuracy", "0")));
            bestLocation.setTime(Long.parseLong(sharedPref.getString(dt, "0")));
            bestLocation.setLatitude(Float.parseFloat(sharedPref.getString("Latitude", "0")));
            bestLocation.setLongitude(Float.parseFloat(sharedPref.getString("Longitude", "0")));

            if (isBetterLocation(location, bestLocation)) {
                editor.putString("Longitude", String.valueOf(location.getLongitude()));
                editor.putString("Latitude", String.valueOf(location.getLatitude()));
                editor.putString("Accuracy", String.valueOf(location.getAccuracy()));
                editor.putString("Time", String.valueOf(location.getTime()));
                editor.putString("Altitude", String.valueOf(location.getAltitude()));
                editor.apply();
            }

            Map<String, ?> allEntries = sharedPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("Map", entry.getKey() + ": " + entry.getValue().toString());
            }
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            showCurrentLocation();
        }

        public void onProviderDisabled(@NotNull String s) {

        }

        public void onProviderEnabled(@NotNull String s) {

        }

    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;

        UserLoginTask(Context context, String email, String password) {
            mEmail = email;
            mPassword = password;
            mContext = context;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            String[] DUMMY_CREDENTIALS = new String[]{
                    "test1234:test1234", "testS12345:testS12345", "bar@example.com:world"
            };

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (!success) return;
            LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert mlocManager != null;
            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DatabaseHelper db = new DatabaseHelper(LoginActivity.this);
                if ((mEmail.equals("dmu@aku") && mPassword.equals("aku?dmu")) ||
                        (mEmail.equals("guest@aku") && mPassword.equals("aku1234")) || db.Login(mEmail, mPassword)
                        || (mEmail.equals("test1234") && mPassword.equals("test1234"))) {
                    MainApp.userEmail = mEmail;
                    MainApp.admin = mEmail.contains("@");
                    Intent iLogin = new Intent(LoginActivity.this, MainActivity.class);
                    finish();
                    startActivity(iLogin);

                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    Toast.makeText(LoginActivity.this, mEmail + " " + mPassword, Toast.LENGTH_SHORT).show();
                }


            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        LoginActivity.this);
                alertDialogBuilder
                        .setMessage("GPS is disabled in your device. Enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Enable GPS",
                                (dialog, id) -> {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            }

        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

