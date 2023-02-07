package com.simonorow.zoomscheduler;

import com.simonorow.zoomscheduler.Models.LettuceMeet.Availability;
import com.simonorow.zoomscheduler.Models.LettuceMeet.LettuceMeetResponse;
import com.simonorow.zoomscheduler.Models.LettuceMeet.PollResponse;

import java.util.*;

public class Scheduler {

    private Map<String, Integer> availabilityTimes = new HashMap<>();

    public void findOptimalTime(LettuceMeetResponse lettuceMeetResponse) {
        ArrayList<PollResponse> responses = lettuceMeetResponse.data.event.pollResponses;
        for (PollResponse individualResponse : responses) {
            ArrayList<Availability> availabilities = individualResponse.availabilities;
            for (Availability currentAvailability : availabilities) {
                Date start = currentAvailability.start;
                Date end = currentAvailability.end;
                findHoursBetweenRange(start, end);
            }
        }
    }

    private void findHoursBetweenRange(Date date1, Date date2) {
        Calendar calendar1 = GregorianCalendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.setTime(date2);

        double seconds = (date2.getTime() - date1.getTime()) * 0.001;
        int duration  = (int)seconds;
        int ThirtyMinIntervals = duration/60/30;

        for(int interval=0;interval<ThirtyMinIntervals; interval++) {
            String hours = String.valueOf(calendar1.get(Calendar.HOUR));
            String minutes = String.format("%02d", calendar1.get(Calendar.MINUTE));
            String time = hours + ":" + minutes;

            if(availabilityTimes.containsKey(time)) {
                availabilityTimes.merge(time, 1, Integer::sum);
            } else {
                availabilityTimes.put(time, 1);
            }
            calendar1.add(Calendar.MINUTE, 30);
        }
    }

    public Map<String, Integer> getAvailabilityTimes() {
        return availabilityTimes;
    }
}

