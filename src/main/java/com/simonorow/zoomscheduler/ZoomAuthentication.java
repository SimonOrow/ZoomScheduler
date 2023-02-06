package com.simonorow.zoomscheduler;


import com.google.gson.Gson;
import com.simonorow.zoomscheduler.Models.ZoomOAuthResponse;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

// Utilizes Server-to-Server for simplicity.
public class ZoomAuthentication {

    public static ZoomOAuthResponse ZoomCredentials() throws Exception {

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
        return model;
    }
}
