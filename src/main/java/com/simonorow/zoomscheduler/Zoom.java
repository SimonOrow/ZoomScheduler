package com.simonorow.zoomscheduler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simonorow.zoomscheduler.Models.ZoomGetUsers.UsersList;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.BasicMeetingInfo;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.CreationResponse;
import com.simonorow.zoomscheduler.Models.ZoomMeetingRequestParams;
import com.simonorow.zoomscheduler.Models.ZoomOAuthResponse;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

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

    public static String getUserId(String zoomToken) throws Exception {
        TrustOverride.begin();

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

    public static BasicMeetingInfo scheduleMeeting(String zoomToken, String userId, Date date) throws Exception {
        TrustOverride.begin();

        String strDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
        System.out.println("Date for Meeting: "+ strDate);


        ZoomMeetingRequestParams zoomMeetingRequestParams = new ZoomMeetingRequestParams();
        zoomMeetingRequestParams.agenda = "Scheduled Zoom Meeting";
        zoomMeetingRequestParams.topic = "Scheduled Zoom Meeting";
        zoomMeetingRequestParams.default_password = false;
        zoomMeetingRequestParams.duration = 30;
        zoomMeetingRequestParams.password = "123456";
        zoomMeetingRequestParams.pre_schedule = false;
        zoomMeetingRequestParams.join_before_host = true;
        zoomMeetingRequestParams.start_time = strDate;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
        String json = gson.toJson(zoomMeetingRequestParams);


        URL url = new URL("https://api.zoom.us/v2/users/"+userId+"/meetings");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("Accept", "application/json");
        httpConn.setRequestProperty("Authorization", "Bearer " + zoomToken);

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write(json);
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);

        CreationResponse model = gson.fromJson(response, CreationResponse.class);

        return new BasicMeetingInfo(model.join_url, model.password, model.start_time, model.timezone);

    }


    public static Date GenerateMeetingDate(String meetDate, String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(meetDate);

        String[] SplitNumericalAndAMPM = time.split(" ");
        String[] timeData = SplitNumericalAndAMPM[0].split(":");
        int ap = (SplitNumericalAndAMPM[1].equals("AM")) ? 0 : 1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, Integer.valueOf(timeData[0])); // 12-hour format. Calendar.HOUR_OF_DAY = 24-hour format.
        cal.set(Calendar.MINUTE, Integer.valueOf(timeData[1]));
        cal.set(Calendar.AM_PM, ap);
        return cal.getTime();
    }

    public static Date shiftTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        return targetCalendar.getTime();
    }
}
