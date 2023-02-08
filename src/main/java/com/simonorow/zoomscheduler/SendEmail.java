package com.simonorow.zoomscheduler;

import com.google.gson.Gson;
import com.simonorow.zoomscheduler.Models.SendGridRequest.*;
import com.simonorow.zoomscheduler.Models.TableUser;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.BasicMeetingInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SendEmail {

    public static void sendEmail(Map<String, String> users, BasicMeetingInfo meetingInfo) throws Exception {

        Date date = Zoom.shiftTimeZone(meetingInfo.date, TimeZone.getTimeZone("UTC"), TimeZone.getTimeZone(meetingInfo.timezone));
        String strDate = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm a").format(date);

        Personalization personalization = new Personalization();
        ArrayList<To> to = new ArrayList<>();

        for (Map.Entry<String, String> entry : users.entrySet()) {
            String value = entry.getValue();
            if(value.isEmpty()) {
                continue;
            }
            to.add(new To(value));
        }
        personalization.to = to;
        ArrayList<Personalization> personalizationList = new ArrayList<>();
        personalizationList.add(personalization);

        Content content = new Content();
        content.type = "text/plain";
        content.value = "You've been invited to a meeting.\n\nLink: " + meetingInfo.link + "\n\nPassword: "+meetingInfo.password + "\n\n" + "Date: "+strDate;
        ArrayList<Content> contentList = new ArrayList<>();
        contentList.add(content);

        Email email = new Email();
        email.from = new From("simon@simonorow.com");
        email.subject = "Zoom Invitation Link";
        email.personalizations = personalizationList;
        email.content = contentList;

        Gson gson = new Gson();
        String json = gson.toJson(email);
        System.out.println(json);

        TrustOverride.begin();
        URL url = new URL("https://api.sendgrid.com/v3/mail/send");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        String sendGridKey = System.getenv("SENDGRID_API_KEY");;
        httpConn.setRequestProperty("authorization", "Bearer "+sendGridKey);
        httpConn.setRequestProperty("Content-Type", "application/json");

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
    }


}
