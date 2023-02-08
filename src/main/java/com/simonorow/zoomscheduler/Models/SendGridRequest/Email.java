package com.simonorow.zoomscheduler.Models.SendGridRequest;

import java.util.ArrayList;

public class Email{
    public ArrayList<Personalization> personalizations;
    public From from;
    public String subject;
    public ArrayList<Content> content;
}

