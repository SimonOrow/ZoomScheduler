package com.simonorow.zoomscheduler.Models.LettuceMeet;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    public String id;
    public String title;
    public String description;
    public int type;
    public String pollStartTime;
    public String pollEndTime;
    public Object maxScheduledDurationMins;
    public String timeZone;
    public ArrayList<String> pollDates;
    public Object start;
    public Object end;
    public boolean isScheduled;
    public Date createdAt;
    public Date updatedAt;
    public Object user;
    public Object googleEvents;
    public ArrayList<PollResponse> pollResponses;
}
