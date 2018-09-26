package de.freddi.bananaapp.http;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import de.freddi.bananaapp.App;
import de.freddi.bananaapp.database.DBTransaction;
import de.freddi.bananaapp.database.DBUser;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by freddi on 22.05.2017.
 *
 * HTTP calls, gepostetes JSON
 */
public class HTTPConnector {

    public static void updateUsers() {
        final boolean isDebug = new Preferences().isDebugLogging();

        if (hasInvalidToken()) {
            //EventBus.getDefault().post(new EventbusMessage(EventbusMessage.EventbusAction.MESSAGE_SNACK, "login error"));
            L.log("HTTPConnector", "updateUsers: login error", isDebug);
            return;
        }
        final ConnectorResponse response = generateAndSend("get_user_list", null);

        if (response.hasStatusOk()) {
            App.get().getDB().databaseInterface().wipeUsers();
            try {
                final JSONArray arr = new JSONObject(response.getResponsePayload()).getJSONArray("action_result");
                JSONObject jsonUser;
                DBUser dbUser;
                List<DBUser> listUsers = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    jsonUser = arr.getJSONObject(i);

                    dbUser = new DBUser();
                    dbUser.display_name = jsonUser.optString("display_name");
                    dbUser.bananas_to_spend = jsonUser.optInt("bananas_to_spend");
                    dbUser.bananas_received = jsonUser.optInt("bananas_received");
                    listUsers.add(dbUser);
                }
                App.get().getDB().databaseInterface().insertAllUsers(listUsers);

            } catch (final Exception e) {
                if (isDebug) {
                    e.printStackTrace();
                }
            }
        } else {
            displayMessage(response);
        }
    }

    public static void updateTransactions() {
        final boolean isDebug = new Preferences().isDebugLogging();

        if (hasInvalidToken()) {
            L.log("HTTPConnector", "updateTransactions: login error", isDebug);
            return;
        }

        final Preferences prefs = new Preferences();

        JSONObject jsonTransaction = new JSONObject();
        try {
            jsonTransaction.put("limit", prefs.getAsString(Preferences.PREF.OTHER_LIMIT_TRANSACTIONS));
        } catch (final JSONException e) {
            if (prefs.isDebugLogging()) {
                e.printStackTrace();
            }
        }

        final ConnectorResponse response = generateAndSend("get_transaction_list", jsonTransaction);
        if (response.hasStatusOk()) {
            App.get().getDB().databaseInterface().wipeTransactions();
            try {
                final JSONArray arr = new JSONObject(response.getResponsePayload()).getJSONArray("action_result");
                JSONObject json;
                DBTransaction dbTransaction;
                List<DBTransaction> listTransactions = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    json = arr.getJSONObject(i);
                    dbTransaction = new DBTransaction();
                    dbTransaction.timestamp = json.optString("timestamp");
                    dbTransaction.from_user = json.optString("from_user");
                    dbTransaction.to_user = json.optString("to_user");
                    dbTransaction.comment = json.optString("comment");
                    dbTransaction.source = json.optString("source");
                    dbTransaction.category = json.optString("category");

                    listTransactions.add(dbTransaction);
                }
                App.get().getDB().databaseInterface().insertAllTransactions(listTransactions);
            } catch (final Exception e) {
                if (isDebug) {
                    e.printStackTrace();
                }
            }
        } else {
            displayMessage(response);
        }
    }

    /**
     * @return true = success
     */
    public static boolean updateAccountDetails() {
        final boolean isDebug = new Preferences().isDebugLogging();

        if (hasInvalidToken()) {
            L.log("HTTPConnector", "updateAccountDetails: login error", isDebug);
            return false;
        }

        final ConnectorResponse response = generateAndSend("get_account_details", null);
        if (response.hasStatusOk()) {
            final Preferences prefs = new Preferences();
            try {
                final JSONArray arr = new JSONObject(response.getResponsePayload()).getJSONArray("action_result");
                if (arr.length() > 0) {
                    JSONObject jsonUser = arr.getJSONObject(0);

                    prefs.set(Preferences.PREF.ACCOUNT_TOKEN_EXPIRATION, jsonUser.optString("token_expiration_timestamp"));
                    prefs.set(Preferences.PREF.ACCOUNT_TOKEN_DURATION, jsonUser.optString("token_duration"));
                    prefs.set(Preferences.PREF.ACCOUNT_IS_ADMIN, jsonUser.optString("is_admin"));
                    prefs.set(Preferences.PREF.ACCOUNT_AD_USER, jsonUser.optString("ad_user"));
                    prefs.set(Preferences.PREF.ACCOUNT_DISPLAYNAME, jsonUser.optString("display_name"));
                    prefs.set(Preferences.PREF.ACCOUNT_ID, jsonUser.optString("id"));
                    prefs.set(Preferences.PREF.ACCOUNT_BANANAS_TO_SPEND, jsonUser.optString("bananas_to_spend"));
                    prefs.set(Preferences.PREF.ACCOUNT_BANANAS_RECEIVED, jsonUser.optString("bananas_received"));

                    return true;
                }
            } catch (final Exception e) {
                if (isDebug) {
                    e.printStackTrace();
                }
            }
        } else {
            displayMessage(response);
        }

        return false;
    }

    /**
     * @param strTargetUserDisplayName banana receiver
     * @param strComment               banana comment
     * @return true = success
     */
    public static boolean sendBananas(final String strTargetUserDisplayName, final String strComment, final String strCategory) {
        final boolean isDebug = new Preferences().isDebugLogging();

        if (hasInvalidToken()) {
            L.log("HTTPConnector", "sendBananas: login error", isDebug);
            return false;
        }

        JSONObject jsonTransaction = new JSONObject();
        try {
            jsonTransaction.put("to_user", strTargetUserDisplayName);
            jsonTransaction.put("banana_count", 1);
            jsonTransaction.put("comment", strComment);
            jsonTransaction.put("category", strCategory);
        } catch (final JSONException e) {
            if (isDebug) {
                e.printStackTrace();
            }
        }

        final ConnectorResponse response = generateAndSend("create_transaction", jsonTransaction);

        return response.hasStatusOk();
    }

    /**
     * @return true = success
     */
    public static boolean doLogout() {
        final boolean isDebug = new Preferences().isDebugLogging();

        if (hasInvalidToken()) {
            L.log("HTTPConnector", "doLogout: login error", isDebug);
            return false;
        }

        final ConnectorResponse response = generateAndSend("logout", null);
        if (response.hasStatusOk()) {
            Preferences prefs = new Preferences();
            prefs.set(Preferences.PREF.ACCOUNT_TOKEN, "");
            prefs.set(Preferences.PREF.ACCOUNT_TOKEN_EXPIRATION, "");
            prefs.set(Preferences.PREF.ACCOUNT_TOKEN_DURATION, "");
            prefs.set(Preferences.PREF.ACCOUNT_IS_ADMIN, "");
            prefs.set(Preferences.PREF.ACCOUNT_AD_USER, "");
            prefs.set(Preferences.PREF.ACCOUNT_DISPLAYNAME, "");
            prefs.set(Preferences.PREF.ACCOUNT_ID, "");
            prefs.set(Preferences.PREF.ACCOUNT_BANANAS_TO_SPEND, "");
            prefs.set(Preferences.PREF.ACCOUNT_BANANAS_RECEIVED, "");
        }

        return response.hasStatusOk();
    }

    private static boolean hasInvalidToken() {
        final String strToken = new Preferences().getAsString(PREF.ACCOUNT_TOKEN);
        return StringUtils.isBlank(strToken);
    }

    private static ConnectorResponse generateAndSend(final String strAction, final JSONObject action_request) {
        final boolean bIsDebug = new Preferences().isDebugLogging();

        final String strRequest = generateJsonRequest(strAction, action_request);
        L.log(strAction, "Request=" + strRequest, bIsDebug);

        final ConnectorResponse response = performHttpPostCall(strRequest);
        L.log(strAction, "Response=" + response.getResponsePayload(), bIsDebug);

        return response;
    }

    private static String generateJsonRequest(final String strAction, final JSONObject action_request) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("actionname", strAction);

            JSONObject jsonLogin = new JSONObject();
            jsonLogin.put("source", "app");
            jsonLogin.put("token", new Preferences().getAsString(PREF.ACCOUNT_TOKEN));

            jsonRequest.put("login", jsonLogin);

            if (action_request != null) {
                jsonRequest.put("action_request", action_request);
            }
            return jsonRequest.toString(2);
        } catch (final Exception ignored) {
        }

        return "";
    }

    private static ConnectorResponse performHttpPostCall(final String strPostMe) {
        final Preferences prefs = new Preferences();

        L.log("performPostCall", "URL=" + prefs.getAsString(PREF.CONNECTION_SERVER), prefs);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(prefs.getAsInt(PREF.CONNECTION_TIMEOUT_CONNECT, 8000), TimeUnit.MILLISECONDS)
                .readTimeout(prefs.getAsInt(PREF.CONNECTION_TIMEOUT_READ, 5000), TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(prefs.getAsString(PREF.CONNECTION_SERVER))
                .header("Accept-Encoding", "gzip")
                .header("Authorization", Credentials.basic(prefs.getAsString(PREF.CONNECTION_HTTP_USER), prefs.getAsString(PREF.CONNECTION_HTTP_PASS)))
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), strPostMe))
                .build();

        ConnectorResponse response = new ConnectorResponse();
        try (Response res = client.newCall(request).execute()) {
            L.log("performPostCall", "success = " + res.isSuccessful() + " http-code = " + res.code(), prefs);

            if (res.body() != null && res.body().byteStream() != null) {
                response.setResponsePayload(IOUtils.toString(new GZIPInputStream(res.body().byteStream()), StandardCharsets.UTF_8));
                //response.setResponsePayload(res.body().string());
            }
        } catch (final IOException e) {
            response.setException(e);
        }

        return response;
    }

    private static void displayMessage(final ConnectorResponse res) {
        final boolean isDebug = new Preferences().isDebugLogging();
        String strStatusToDisplay = null;

        // 1. Status checken
        if (res.hasValidPayload()) {
            try {
                final JSONObject jsonResponse = new JSONObject(res.getResponsePayload());
                String strStatus = jsonResponse.getString("status");
                if (strStatus != null && strStatus.trim().length() > 0) {
                    strStatusToDisplay = strStatus;
                }
            } catch (final JSONException e) {
                strStatusToDisplay = "error parsing server response";
                if (isDebug) {
                    e.printStackTrace();
                }
            }
        }

        // 2. Exceptions checken
        if (strStatusToDisplay == null && res.getException() != null) {
            strStatusToDisplay = res.getException().getMessage();
        }

        L.log("HTTPConnector", strStatusToDisplay, isDebug);
    }
}