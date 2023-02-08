package com.simonorow.zoomscheduler.Models.ZoomMeeting;

import java.util.Date;

public class BasicMeetingInfo {
    public String link;
    public String password;

    public Date date;

    public String timezone;

    public BasicMeetingInfo(String link, String password, Date date, String timezone) {
        this.link = link;
        this.password = password;
        this.date = date;
        this.timezone = timezone;
    }
}
