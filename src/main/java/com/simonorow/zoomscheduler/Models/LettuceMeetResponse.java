package com.simonorow.zoomscheduler.Models;

import java.util.ArrayList;
import java.util.Date;

// Utilized https://json2csharp.com/code-converters/json-to-pojo for quick and accurate model generation.
public class LettuceMeetResponse {
    public Data data;
}


class Availability{
    public Date start;
    public Date end;
}

class Data {
    public Event event;
}

class Event{
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

class PollResponse{
    public String id;
    public User user;
    public ArrayList<Availability> availabilities;
    public Event event;
}



class User{
    public String __typename;
    public String name;
    public String email;
}

