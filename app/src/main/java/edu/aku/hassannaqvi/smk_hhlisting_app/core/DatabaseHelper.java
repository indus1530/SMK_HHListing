package edu.aku.hassannaqvi.smk_hhlisting_app.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.BLRandomContract.singleRandomHH;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.DistrictContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.DistrictContract.DistrictTable;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.EnumBlockContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.EnumBlockContract.EnumBlockTable;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.ListingContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.ListingContract.ListingEntry;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.UsersContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.UsersContract.UsersTable;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VersionAppContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VersionAppContract.VersionAppTable;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VerticesContract;
import edu.aku.hassannaqvi.smk_hhlisting_app.contracts.VerticesContract.VerticesTable;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getName();
    public static final String DATABASE_NAME = "SMK-hhl.db";
    public static final String PROJECT_NAME = "DMU-SMK/LISTING";
    public static final String DB_NAME = DATABASE_NAME.replace(".db", "-copy.db");
    private static final int DATABASE_VERSION = 1;
    final String SQL_CREATE_BL_RANDOM = "CREATE TABLE " + singleRandomHH.TABLE_NAME + "("
            + singleRandomHH.COLUMN_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
            + singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE + " TEXT,"
            + singleRandomHH.COLUMN_LUID + " TEXT,"
            + singleRandomHH.COLUMN_STRUCTURE_NO + " TEXT,"
            + singleRandomHH.COLUMN_FAMILY_EXT_CODE + " TEXT,"
            + singleRandomHH.COLUMN_HH_HEAD + " TEXT,"
            + singleRandomHH.COLUMN_CONTACT + " TEXT,"
            + singleRandomHH.COLUMN_HH_SELECTED_STRUCT + " TEXT,"
            + singleRandomHH.COLUMN_RANDOMDT + " TEXT );";
    final String SQL_CREATE_LISTING_TABLE = "CREATE TABLE " + ListingEntry.TABLE_NAME + " (" +
            ListingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ListingEntry.COLUMN_NAME_UID + " TEXT, " +
            ListingEntry.COLUMN_NAME_HHDATETIME + " TEXT, " +
            ListingEntry.COLUMN_NAME_HHDATETIME01 + " TEXT, " +
            ListingEntry.COLUMN_NAME_ENUMCODE + " TEXT, " +
            ListingEntry.COLUMN_NAME_CLUSTERCODE + " TEXT, " +
            ListingEntry.COLUMN_NAME_ENUMSTR + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH01 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH02 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH03 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH04 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH04OTHER + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH05 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH06 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH07 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH07n + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH08 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH08A1 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH09 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH09A1 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH10 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH11 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH12 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH13 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH14 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH15 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH16 + " TEXT, " +
            ListingEntry.COLUMN_ADDRESS + " TEXT, " +
            ListingEntry.COLUMN_ISNEWHH + " TEXT, " +
            ListingEntry.COLUMN_COUNTER + " TEXT, " +
            ListingEntry.COLUMN_USERNAME + " TEXT, " +
            ListingEntry.COLUMN_NAME_DEVICEID + " TEXT, " +
            ListingEntry.COLUMN_TAGID + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSLat + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSLng + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSTime + " TEXT, " +
            ListingEntry.COLUMN_APPVER + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSAccuracy + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSAltitude + " TEXT, " +
            ListingEntry.COLUMN_RANDOMIZED + " TEXT, " +
            ListingEntry.COLUMN_SYNCED + " TEXT, " +
            ListingEntry.COLUMN_SYNCED_DATE + " TEXT );";
    final String SQL_CREATE_ENUMBLOCK = "CREATE TABLE " + EnumBlockTable.TABLE_NAME + " (" +
            EnumBlockTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EnumBlockTable.COLUMN_DIST_ID + " TEXT, " +
            EnumBlockTable.COLUMN_GEO_AREA + " TEXT, " +
            EnumBlockTable.COLUMN_CLUSTER_AREA + " TEXT );";
    final String SQL_CREATE_VERTICES = "CREATE TABLE " + VerticesTable.TABLE_NAME + " (" +
            VerticesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VerticesTable.COLUMN_CLUSTER_CODE + " TEXT," +
            VerticesTable.COLUMN_POLY_LAT + " TEXT, " +
            VerticesTable.COLUMN_POLY_LANG + " TEXT, " +
            VerticesTable.COLUMN_POLY_SEQ + " TEXT );";
    final String SQL_COUNT_LISTINGS = "SELECT count(*) as ttlisting from " + ListingEntry.TABLE_NAME;
    final String SQL_CREATE_VERSIONAPP = "CREATE TABLE " + VersionAppTable.TABLE_NAME + " (" +
            VersionAppTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VersionAppTable.COLUMN_VERSION_CODE + " TEXT, " +
            VersionAppTable.COLUMN_VERSION_NAME + " TEXT, " +
            VersionAppTable.COLUMN_PATH_NAME + " TEXT );";
    final String SQL_CREATE_USERS = "CREATE TABLE " + UsersTable.TABLE_NAME + "("
            + UsersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + UsersTable.ROW_USERNAME + " TEXT,"
            + UsersTable.ROW_PASSWORD + " TEXT,"
            + UsersTable.DIST_ID + " TEXT );";
    final String SQL_CREATE_DISTRICTS = "CREATE TABLE " + DistrictTable.TABLE_NAME + "("
            + DistrictTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DistrictTable.COLUMN_DIST_ID + " TEXT,"
            + DistrictTable.COLUMN_DIST_NAME + " TEXT,"
            + DistrictTable.COLUMN_PROVINCE_NAME + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LISTING_TABLE);
        db.execSQL(SQL_CREATE_ENUMBLOCK);
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_BL_RANDOM);
        db.execSQL(SQL_CREATE_VERTICES);
        db.execSQL(SQL_CREATE_VERSIONAPP);
        db.execSQL(SQL_CREATE_DISTRICTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        /*db.execSQL("DROP TABLE IF EXISTS " + ListingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SingleTaluka.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EnumBlockTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SingleVertices.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VersionAppTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SignUpTable.TABLE_NAME);
        onCreate(db);*/
    }


    //Other Functions
    public boolean Login(String username, String password) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM " + UsersContract.UsersTable.TABLE_NAME + " WHERE " + UsersContract.UsersTable.ROW_USERNAME + "=? AND " + UsersContract.UsersTable.ROW_PASSWORD + "=?", new String[]{username, password});
        if (mCursor != null) {

            if (mCursor.getCount() > 0) {

                if (mCursor.moveToFirst()) {
//                    MainApp.DIST_ID = mCursor.getString(mCursor.getColumnIndex(UsersContract.UsersTable.DIST_ID));
                    mCursor.close();
                }
                return true;
            }
        }
        return false;
    }


    //Add Forms
    public Long addForm(ListingContract lc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_NAME_UID, lc.getUID());
        values.put(ListingEntry.COLUMN_NAME_HHDATETIME, lc.getHhDT());
        values.put(ListingEntry.COLUMN_NAME_HHDATETIME01, lc.getHhDT01());

        values.put(ListingEntry.COLUMN_NAME_ENUMCODE, lc.getEnumCode());
        values.put(ListingEntry.COLUMN_NAME_CLUSTERCODE, lc.getClusterCode());
        values.put(ListingEntry.COLUMN_NAME_ENUMSTR, lc.getEnumStr());

        values.put(ListingEntry.COLUMN_NAME_HH01, lc.getHh01());
        values.put(ListingEntry.COLUMN_NAME_HH02, lc.getHh02());
        values.put(ListingEntry.COLUMN_NAME_HH03, lc.getHh03());

        MainApp.updatePSU(lc.getHh02(), lc.getHh03());
        Log.d(TAG, "PSUExist (Test): " + MainApp.sharedPref.getString(lc.getHh02(), "0"));

        values.put(ListingEntry.COLUMN_NAME_HH04, lc.getHh04());
        values.put(ListingEntry.COLUMN_NAME_HH04OTHER, lc.getHh04other());
        values.put(ListingEntry.COLUMN_NAME_HH05, lc.getHh05());
        values.put(ListingEntry.COLUMN_NAME_HH06, lc.getHh06());
        values.put(ListingEntry.COLUMN_NAME_HH07, lc.getHh07());
        values.put(ListingEntry.COLUMN_NAME_HH07n, lc.getHh07n());
        values.put(ListingEntry.COLUMN_NAME_HH08, lc.getHh08());
        values.put(ListingEntry.COLUMN_NAME_HH09, lc.getHh09());
        values.put(ListingEntry.COLUMN_NAME_HH08A1, lc.getHh08a1());
        values.put(ListingEntry.COLUMN_NAME_HH09A1, lc.getHh09a1());
        values.put(ListingEntry.COLUMN_NAME_HH10, lc.getHh10());
        values.put(ListingEntry.COLUMN_NAME_HH11, lc.getHh11());
        values.put(ListingEntry.COLUMN_NAME_HH12, lc.getHh12());
        values.put(ListingEntry.COLUMN_NAME_HH13, lc.getHh13());
        values.put(ListingEntry.COLUMN_NAME_HH14, lc.getHh14());
        values.put(ListingEntry.COLUMN_NAME_HH15, lc.getHh15());
        values.put(ListingEntry.COLUMN_NAME_HH16, lc.getHh16());
        values.put(ListingEntry.COLUMN_ISNEWHH, lc.getIsNewHH());
        values.put(ListingEntry.COLUMN_ADDRESS, lc.getHhadd());
        values.put(ListingEntry.COLUMN_COUNTER, lc.getCounter());
        values.put(ListingEntry.COLUMN_NAME_DEVICEID, lc.getDeviceID());
        values.put(ListingEntry.COLUMN_USERNAME, lc.getUsername());
        values.put(ListingEntry.COLUMN_NAME_GPSLat, lc.getGPSLat());
        values.put(ListingEntry.COLUMN_NAME_GPSLng, lc.getGPSLng());
        values.put(ListingEntry.COLUMN_NAME_GPSTime, lc.getGPSTime());
        values.put(ListingEntry.COLUMN_NAME_GPSAccuracy, lc.getGPSAcc());
        values.put(ListingEntry.COLUMN_NAME_GPSAltitude, lc.getGPSAlt());
        values.put(ListingEntry.COLUMN_APPVER, lc.getAppVer());
        values.put(ListingEntry.COLUMN_RANDOMIZED, lc.getIsRandom());
        values.put(ListingEntry.COLUMN_TAGID, lc.getTagId());

        long newRowId;
        newRowId = db.insert(
                ListingEntry.TABLE_NAME,
                ListingEntry.COLUMN_NAME_NULLABLE,
                values);

        return newRowId;
    }

    public void addBLRandom(ListingContract lc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(singleRandomHH.COLUMN_LUID, lc.getUID());
        values.put(singleRandomHH.COLUMN_RANDOMDT, lc.getHhDT());
        values.put(singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE, lc.getClusterCode());
        values.put(singleRandomHH.COLUMN_STRUCTURE_NO, lc.getHh03());
        values.put(singleRandomHH.COLUMN_FAMILY_EXT_CODE, lc.getHh07());
        values.put(singleRandomHH.COLUMN_HH_HEAD, lc.getHh08());
        values.put(singleRandomHH.COLUMN_CONTACT, lc.getHh09());

        values.put(singleRandomHH.COLUMN_HH_SELECTED_STRUCT, lc.getHh10().equals("1") ? "1" : "2");

        long newRowId;
        newRowId = db.insert(
                singleRandomHH.TABLE_NAME,
                null,
                values);

    }


    //Update forms in DB
    public void updateListingUID() {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_NAME_UID, MainApp.lc.getUID());

// Which row to update, based on the title
        String where = ListingEntry._ID + " = ?";
        String[] whereArgs = {MainApp.lc.getID()};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateListingRecord(String Clustercode) {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_RANDOMIZED, "1");

// Which row to update, based on the title
        String where = ListingEntry.COLUMN_NAME_CLUSTERCODE + " = ?";
        String[] whereArgs = {Clustercode};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }


    //Sync functions
    public void syncListingFromDevice(JSONArray fmlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            JSONArray jsonArray = fmlist;
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectUser = jsonArray.getJSONObject(i);

                ListingContract fmc = new ListingContract();
                fmc.Sync(jsonObjectUser);

                addForm(fmc);
            }


        } catch (Exception e) {
            Log.d(TAG, "syncListing(e): " + e);
        } finally {
            db.close();
        }
    }

    public int syncUser(JSONArray userList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UsersContract.UsersTable.TABLE_NAME, null, null);
        int insertCount = 0;
        try {
            for (int i = 0; i < userList.length(); i++) {

                JSONObject jsonObjectUser = userList.getJSONObject(i);

                UsersContract user = new UsersContract();
                user.Sync(jsonObjectUser);
                ContentValues values = new ContentValues();

                values.put(UsersContract.UsersTable.ROW_USERNAME, user.getUserName());
                values.put(UsersContract.UsersTable.ROW_PASSWORD, user.getPassword());
                values.put(UsersContract.UsersTable.DIST_ID, user.getDIST_ID());
                long rowID = db.insert(UsersContract.UsersTable.TABLE_NAME, null, values);
                if (rowID != -1) insertCount++;
            }

        } catch (Exception e) {
            Log.d(TAG, "syncUser(e): " + e);
            db.close();
        } finally {
            db.close();
        }
        return insertCount;
    }

    public int syncDistrict(JSONArray distList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DistrictContract.DistrictTable.TABLE_NAME, null, null);
        int insertCount = 0;
        try {
            for (int i = 0; i < distList.length(); i++) {

                JSONObject jsonObjectUser = distList.getJSONObject(i);

                DistrictContract dist = new DistrictContract();
                dist.Sync(jsonObjectUser);
                ContentValues values = new ContentValues();

                values.put(DistrictTable.COLUMN_DIST_ID, dist.getDist_id());
                values.put(DistrictTable.COLUMN_DIST_NAME, dist.getDistrict());
                values.put(DistrictTable.COLUMN_PROVINCE_NAME, dist.getProvince());
                long rowID = db.insert(DistrictTable.TABLE_NAME, null, values);
                if (rowID != -1) insertCount++;
            }

        } catch (Exception e) {
            Log.d(TAG, "syncDist(e): " + e);
            db.close();
        } finally {
            db.close();
        }
        return insertCount;
    }

    public int syncEnumBlocks(JSONArray enumList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EnumBlockContract.EnumBlockTable.TABLE_NAME, null, null);
        int insertCount = 0;
        try {
            for (int i = 0; i < enumList.length(); i++) {
                JSONObject jsonObjectCC;
                try {
                    jsonObjectCC = enumList.getJSONObject(i);
                    EnumBlockContract Vc = new EnumBlockContract();
                    Vc.Sync(jsonObjectCC);

                    ContentValues values = new ContentValues();

                    values.put(EnumBlockContract.EnumBlockTable.COLUMN_DIST_ID, Vc.getDist_id());
                    values.put(EnumBlockContract.EnumBlockTable.COLUMN_GEO_AREA, Vc.getGeoarea());
                    values.put(EnumBlockContract.EnumBlockTable.COLUMN_CLUSTER_AREA, Vc.getCluster());

                    long rowID = db.insert(EnumBlockContract.EnumBlockTable.TABLE_NAME, null, values);
                    if (rowID != -1) insertCount++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "syncEnumBlocks(e): " + e);
            db.close();
        } finally {
            db.close();
        }
        return insertCount;
    }

    public void syncVertices(JSONArray vcList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VerticesTable.TABLE_NAME, null, null);
        try {
            for (int i = 0; i < vcList.length(); i++) {
                JSONObject jsonObjectVR = vcList.getJSONObject(i);

                VerticesContract vc = new VerticesContract();
                vc.sync(jsonObjectVR);

                ContentValues values = new ContentValues();

                values.put(VerticesTable.COLUMN_CLUSTER_CODE, vc.getCluster_code());
                values.put(VerticesTable.COLUMN_POLY_LAT, vc.getPoly_lat());
                values.put(VerticesTable.COLUMN_POLY_LANG, vc.getPoly_lng());
                values.put(VerticesTable.COLUMN_POLY_SEQ, vc.getPoly_seq());

                db.insert(VerticesTable.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            db.close();
        }
    }

    public int syncVersionApp(JSONObject versionList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VersionAppContract.VersionAppTable.TABLE_NAME, null, null);
        long count = 0;
        try {
            JSONObject jsonObjectCC = ((JSONArray) versionList.get(VersionAppContract.VersionAppTable.COLUMN_VERSION_PATH)).getJSONObject(0);
            VersionAppContract Vc = new VersionAppContract();
            Vc.Sync(jsonObjectCC);

            ContentValues values = new ContentValues();

            values.put(VersionAppContract.VersionAppTable.COLUMN_PATH_NAME, Vc.getPathname());
            values.put(VersionAppContract.VersionAppTable.COLUMN_VERSION_CODE, Vc.getVersioncode());
            values.put(VersionAppContract.VersionAppTable.COLUMN_VERSION_NAME, Vc.getVersionname());

            count = db.insert(VersionAppContract.VersionAppTable.TABLE_NAME, null, values);
            if (count > 0) count = 1;

        } catch (Exception ignored) {
        } finally {
            db.close();
        }

        return (int) count;
    }


    //Update from server response
    public void updateSyncedForms(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_SYNCED, true);
        values.put(ListingEntry.COLUMN_SYNCED_DATE, new Date().toString());

// Which row to update, based on the title
        String where = ListingEntry._ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }


    //Get Functions
    public int getListingCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_COUNT_LISTINGS, null);
        int count = 0;

        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public Collection<ListingContract> getUnsyncedListings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_HHDATETIME01,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_SYNCED + " is null";
        String[] whereArgs = null;

        String groupBy = null;
        String having = null;

        String orderBy = ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<ListingContract>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(Query, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (Exception sqlEx) {
            Log.d("printing exception", Objects.requireNonNull(sqlEx.getMessage()));
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

    public Collection<ListingContract> getAllListingsForRandom() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED,
                "COUNT(*) as RESCOUNTER, " +
                        "COUNT(case " + ListingEntry.COLUMN_NAME_HH10 + " when '1' then 1 else null end) as CHILDCOUNTER," +
                        "COUNT(case " + ListingEntry.COLUMN_RANDOMIZED + " when '1' then 1 else null end) as RANDCOUNTER," +
                        "COUNT(*) as TOTALHH"
        };

        String whereClause = ListingEntry.COLUMN_NAME_HH08A1 + " =?";
        String[] whereArgs = {"1"};
        String groupBy = ListingEntry.COLUMN_NAME_CLUSTERCODE;
        String having = null;

        String orderBy = ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<>();
        try {
            c = db.query(
                    true,
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy,                    // The sort order
                    null

            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 1));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public JSONArray getListingsByCluster(String cluster) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_NAME_CLUSTERCODE + " = ?";
        String[] whereArgs = {cluster};
        String groupBy = null;
        String having = null;

        String orderBy =
                ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";
        JSONArray jsonArray = new JSONArray();

        Collection<ListingContract> allLC = new ArrayList<ListingContract>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }

            for (ListingContract lc : allLC) {
                try {
                    jsonArray.put(lc.toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return jsonArray;
    }

    public ArrayList<ListingContract> getRandomLisiting(String clusterCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_NAME_HH08A1 + " =? AND "
                + ListingEntry.COLUMN_NAME_CLUSTERCODE + " =? AND "
                + ListingEntry.COLUMN_RANDOMIZED + " =?";
        String[] whereArgs = {"1", clusterCode, "2"};
        String groupBy = null;
        String having = null;

        String orderBy = null;

        ArrayList<ListingContract> allLC = new ArrayList<>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order

            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public JSONArray getAllListingsJSON() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy =
                ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
            for (ListingContract fc : allLC) {
                jsonArray.put(fc.toJSONObject());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return jsonArray;
    }

    public EnumBlockContract getEnumBlock(String cluster) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                EnumBlockTable._ID,
                EnumBlockTable.COLUMN_DIST_ID,
                EnumBlockTable.COLUMN_GEO_AREA,
                EnumBlockTable.COLUMN_CLUSTER_AREA
        };

        String whereClause = EnumBlockTable.COLUMN_CLUSTER_AREA + " =?";
        String[] whereArgs = new String[]{cluster};
        String groupBy = null;
        String having = null;

        String orderBy =
                EnumBlockTable._ID + " ASC";

        EnumBlockContract allEB = null;
        try {
            c = db.query(
                    EnumBlockTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allEB = new EnumBlockContract().HydrateEnum(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allEB;
    }

    public Collection<VerticesContract> getVerticesByCluster(String cluster_code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                VerticesTable._ID,
                VerticesTable.COLUMN_CLUSTER_CODE,
                VerticesTable.COLUMN_POLY_LAT,
                VerticesTable.COLUMN_POLY_LANG,
                VerticesTable.COLUMN_POLY_SEQ
        };

        String whereClause = VerticesTable.COLUMN_CLUSTER_CODE + " = ?";
        String[] whereArgs = {cluster_code};
        String groupBy = null;
        String having = null;

        String orderBy =
                VerticesTable.COLUMN_POLY_SEQ + " ASC";

        Collection<VerticesContract> allVC = new ArrayList<>();
        try {
            c = db.query(
                    VerticesTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                VerticesContract vc = new VerticesContract();
                allVC.add(vc.hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allVC;
    }

    public VersionAppContract getVersionApp() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                VersionAppTable._ID,
                VersionAppTable.COLUMN_VERSION_CODE,
                VersionAppTable.COLUMN_VERSION_NAME,
                VersionAppTable.COLUMN_PATH_NAME
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy = VersionAppTable._ID + " ASC";

        VersionAppContract allVC = new VersionAppContract();
        try {
            c = db.query(
                    VersionAppTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allVC.hydrate(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allVC;
    }

    public List<EnumBlockContract> getEnumBlock() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                EnumBlockTable._ID,
                EnumBlockTable.COLUMN_DIST_ID,
                EnumBlockTable.COLUMN_GEO_AREA,
                EnumBlockTable.COLUMN_CLUSTER_AREA
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy = EnumBlockTable._ID + " ASC";
        List<EnumBlockContract> allEB = new ArrayList<>();
        try {
            c = db.query(
                    EnumBlockTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allEB.add(new EnumBlockContract().HydrateEnum(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allEB;
    }

    public List<DistrictContract> getDistrictProv() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                DistrictTable._ID,
                DistrictTable.COLUMN_DIST_ID,
                DistrictTable.COLUMN_DIST_NAME,
                DistrictTable.COLUMN_PROVINCE_NAME
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy = DistrictTable._ID + " ASC";
        List<DistrictContract> allEB = new ArrayList<>();
        try {
            c = db.query(
                    DistrictTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allEB.add(new DistrictContract().Hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allEB;
    }
}