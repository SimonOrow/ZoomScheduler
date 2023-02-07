package com.simonorow.zoomscheduler.Models.ZoomMeeting;

import java.util.Date;

public class CreationResponse {
    public String uuid;
    public long id;
    public String host_id;
    public String host_email;
    public String topic;
    public int type;
    public String status;
    public Date start_time;
    public int duration;
    public String timezone;
    public String agenda;
    public Date created_at;
    public String start_url;
    public String join_url;
    public String password;
    public String h323_password;
    public String pstn_password;
    public String encrypted_password;
    public Settings settings;
    public boolean pre_schedule;
}

