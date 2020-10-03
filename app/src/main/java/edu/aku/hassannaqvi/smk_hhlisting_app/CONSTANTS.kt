package edu.aku.hassannaqvi.smk_hhlisting_app

class CONSTANTS {
    companion object {
        //For Login
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0
        const val MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1
        const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2
        const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3
        const val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 4
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 5
        const val MINIMUM_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // in Meters
        const val MINIMUM_TIME_BETWEEN_UPDATES: Long = 1000 // in Milliseconds
        const val TWO_MINUTES = 1000 * 60 * 2

        const val SYNC_LOGIN = "sync_login"

        //Login Result Code
        const val LOGIN_RESULT_CODE = 10101
        const val LOGIN_SPLASH_FLAG = "splash_flag"

        //SubInfo
        const val SUB_INFO_END_FLAG = "sub_info_end_flag"

        //Child Activity
        const val CHILD_ENDING_AGE_ISSUE = "childAgeIssue"
        const val CHILD_SERIAL = "serial_extra"

    }
}