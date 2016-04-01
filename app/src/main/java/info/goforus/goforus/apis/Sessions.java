package info.goforus.goforus.apis;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import info.goforus.goforus.apis.listeners.LoginResponseListener;
import info.goforus.goforus.apis.listeners.LogoutResponseListener;
import info.goforus.goforus.event_results.LoginFromApiResult;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import static us.monoid.web.Resty.content;
import static us.monoid.web.Resty.put;

public class Sessions {
    public static final String loginURI = Utils.BaseURI + "login";


    private static final Sessions sessions = new Sessions();

    private Sessions() {
    }

    public static Sessions getInstance() {
        return sessions;
    }

    public JSONObject logIn(final String email, final String password) {
        JSONObject response = null;

        try {
            JSONObject baseJson = new JSONObject();
            JSONObject customerData = new JSONObject();

            customerData.put("email", email);
            customerData.put("password", password);
            baseJson.put("customer", customerData);

            response = Utils.resty.json(loginURI, put(content(baseJson))).object();
        } catch (JSONException | IOException e) {
            response = Utils.errorToJson(e.getMessage());
        }

        EventBus.getDefault().post(new LoginFromApiResult(response));
        return response;
    }
    /* Stop: Login */


    /* Start: Logout */
    public static final String logoutURI = Utils.BaseURI + "logout";

    public void logOut(final LogoutResponseListener listener) {
        new Thread(new Runnable() {
            public void run() {
                JSONObject response;
                try {
                    response = Utils.resty.json(logoutURI + Utils.tokenParams(), put(content(""))).object();
                } catch (IOException | JSONException e) {
                    response = Utils.errorToJson(e.getMessage());
                }

                listener.onLogoutResponse(response);
            }
        }).start();
    }
    /* Stop: Logout */

}
