package com.simonorow.zoomscheduler.Models.ZoomMeeting;

import java.util.Date;

public class BasicMeetingInfo {
    public String link;
    public String password;

    public Date date;

    public BasicMeetingInfo(String link, String password, Date date) {
        this.link = link;
        this.password = password;
        this.date = date;
    }
}
