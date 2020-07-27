package kfa.training.smack.utilities

/** End points **/
// Access to localhost on the machine hosting the Android emulators, is via the special mapped IP
// 10.0.2.2.
const val SOCKET_URL = "http://10.0.2.2:3005/"  // Socket URL is the site root URL (minus /v1/)
const val BASE_URL = "http://10.0.2.2:3005/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}/user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"  // End '/' is required.
const val URL_GET_CHANNELS = "${BASE_URL}channel/"   // End '/' is required.

/** Broadcast constants **/
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"
