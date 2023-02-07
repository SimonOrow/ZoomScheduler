package com.simonorow.zoomscheduler;


import com.google.gson.Gson;
import com.simonorow.zoomscheduler.Models.ZoomGetUsers.UsersList;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.BasicMeetingInfo;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.CreationResponse;
import com.simonorow.zoomscheduler.Models.ZoomOAuthResponse;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

// Utilizes Server-to-Server for simplicity.
public class Zoom {



    public static String ZoomToken() throws Exception {

        String accountId = System.getenv("ZOOM_ACCOUNT_ID");
        String clientId = System.getenv("ZOOM_CLIENT_ID");
        String clientSecret = System.getenv("ZOOM_CLIENT_SECRET");

        System.out.println(clientSecret);

        String idSecretCombo = clientId+":"+clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(idSecretCombo.getBytes());

        TrustOverride.begin();

        URL url = new URL("https://zoom.us/oauth/token");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Host", "zoom.us");
        httpConn.setRequestProperty("Authorization", "Basic " + base64Auth);
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("grant_type=account_credentials&account_id=" + accountId);
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);

        Gson gson = new Gson();
        ZoomOAuthResponse model = gson.fromJson(response, ZoomOAuthResponse.class);
        return model.access_token;
    }

    public String getUserId(String zoomToken) throws Exception {
        TrustOverride.begin();

        String authenticationString = ZoomToken();


        URL url = new URL("https://api.zoom.us/v2/users?status=active&page_size=30&role_id=0&page_number=1&include_fields=custom_attributes");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        httpConn.setRequestProperty("Accept", "application/json");
        httpConn.setRequestProperty("Authorization", "Bearer " + zoomToken);
        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);


        Gson gson = new Gson();
        UsersList model = gson.fromJson(response, UsersList.class);

        return model.users.get(0).id;
    }

    public static BasicMeetingInfo scheduleMeeting(String zoomToken, String userId) throws Exception {
        TrustOverride.begin();

        URL url = new URL("https://api.zoom.us/v2/users/"+userId+"/meetings");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("Accept", "application/json");
        httpConn.setRequestProperty("Authorization", "Bearer " + zoomToken);

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("{\n  \"agenda\": \"My Meeting\",\n  \"default_password\": false,\n  \"duration\": 30,\n  \"password\": \"123456\",\n  \"pre_schedule\": false,\n  \"join_before_host\": true,\n  \"start_time\": \"2022-03-25T07:32:55Z\",\n  \"topic\": \"My Meeting\"\n  \n}");
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);

        Gson gson = new Gson();
        CreationResponse model = gson.fromJson(response, CreationResponse.class);

        return new BasicMeetingInfo(model.join_url, model.password);

    }

}
