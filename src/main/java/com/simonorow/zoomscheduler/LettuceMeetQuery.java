package com.simonorow.zoomscheduler;

import com.simonorow.zoomscheduler.Models.LettuceMeetResponse;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import com.google.gson.Gson;
import javax.net.ssl.*;

public class LettuceMeetQuery {

    public static LettuceMeetResponse getSchedule(String lettuceMeetUrl) throws Exception {

        Boolean isValidUrl = URLFunctions.isValid(lettuceMeetUrl);
        Boolean isLettuceMeetUrl = lettuceMeetUrl.startsWith("https://lettucemeet.com/l/");
        String code = lettuceMeetUrl.substring(lettuceMeetUrl.lastIndexOf("/") + 1);

        if(!isValidUrl || !isLettuceMeetUrl || code.isEmpty()) {
            System.out.println("Invalid URL");
            // Invalid URL.
            return new LettuceMeetResponse();
        }

        TrustOverride.begin();

        URL url = new URL("https://api.lettucemeet.com/graphql");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Host", "api.lettucemeet.com");
        httpConn.setRequestProperty("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"");
        httpConn.setRequestProperty("accept", "*/*");
        httpConn.setRequestProperty("content-type", "application/json");
        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
        httpConn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        httpConn.setRequestProperty("sec-ch-ua-platform", "\"macOS\"");
        httpConn.setRequestProperty("origin", "https://lettucemeet.com");
        httpConn.setRequestProperty("sec-fetch-site", "same-site");
        httpConn.setRequestProperty("sec-fetch-mode", "cors");
        httpConn.setRequestProperty("sec-fetch-dest", "empty");
        httpConn.setRequestProperty("referer", "https://lettucemeet.com/");
        httpConn.setRequestProperty("accept-language", "en-US,en;q=0.9");

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("{\"id\":\"EventQuery\",\"query\":\"query EventQuery(\\n  $id: ID!\\n) {\\n  event(id: $id) {\\n    ...Event_event\\n    ...EditEvent_event\\n    id\\n  }\\n}\\n\\nfragment EditEvent_event on Event {\\n  id\\n  title\\n  description\\n  type\\n  pollStartTime\\n  pollEndTime\\n  maxScheduledDurationMins\\n  pollDates\\n  isScheduled\\n  start\\n  end\\n  timeZone\\n  updatedAt\\n}\\n\\nfragment Event_event on Event {\\n  id\\n  title\\n  description\\n  type\\n  pollStartTime\\n  pollEndTime\\n  maxScheduledDurationMins\\n  timeZone\\n  pollDates\\n  start\\n  end\\n  isScheduled\\n  createdAt\\n  updatedAt\\n  user {\\n    id\\n  }\\n  googleEvents {\\n    title\\n    start\\n    end\\n  }\\n  pollResponses {\\n    id\\n    user {\\n      __typename\\n      ... on AnonymousUser {\\n        name\\n        email\\n      }\\n      ... on User {\\n        id\\n        name\\n        email\\n      }\\n      ... on Node {\\n        __isNode: __typename\\n        id\\n      }\\n    }\\n    availabilities {\\n      start\\n      end\\n    }\\n    event {\\n      id\\n    }\\n  }\\n}\\n\",\"variables\":{\"id\":\""+code+"\"}}");
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
        LettuceMeetResponse model = gson.fromJson(response, LettuceMeetResponse.class);
        return model;

    }


}
