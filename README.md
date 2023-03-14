# Zoom Scheduler

Zoom Scheduler is an application I created for the DeveloperWeek 2023 Hackathon. This project was given second place.

**DevPost Link:** https://devpost.com/software/zoom-scheduler-84p72i

## Inspiration

After seeing Zoom's sponsor challenge details, I began thinking about what are the challenges with meeting with team members in a hybrid work environment. I realized that scheduling a meeting that works for everyone might be difficult, so I wanted to make a solution that would take care of this with an auto scheduling tool. After looking at the Zoom APIs, I noted that Zoom does not have the capability to keep track of meetings that users were invited to, or the ability to pull their schedule. What I wanted to achieve wouldn't be possible with Zoom's API's alone.

This is where LettuceMeet comes in. LettuceMeet is an online tool where anyone can create a meeting link and share it with others. From there, anyone with the link can select time slots where they are available. Since LettuceMeet has a simple API that returns all the details, I decided to make a tool that integrates Zoom and LettuceMeet.

The tool makes scheduling and finding the perfect time easier!

## What it does

The Zoom Scheduler tool requires a LettuceMeet link. After entering one and clicking the "Import Attendees" button, it will load all attendees and the meeting times. The meeting times are sorted by which times are most of the attendees available. Following this, the user would need to select a time, and hit schedule. A zoom meeting is generated through the Zoom APIs and emails are sent out to all attendees through SendGrid's APIs.

## Challenges we ran into

The biggest challenge was getting the scheduling time correct across the program, the zoom APIs, and the email. In the program, everything should be in local time and we're given UTC time. Similarly, Zoom can take in a local time in their APIs but they always return UTC time. Ensuring all of the times were correct was a difficult task as there are different time requirements for the APIs.

Another challenge was figuring out how to properly display all of the available times and what the best strategy might be to do that. Finding the most optimal time can be a challenge. I thought it would be simpler to show the time slots and how many people selected that time slot. This way a user can see which ones have the most users that are willing to attend and make a decision.

## Accomplishments that we're proud of

My biggest accomplishment is figuring out OAuth and spending the time to learn more about it. I was able to successfully integrate everything and get a working application without any issues.

## What we learned

I learned more about the OAuth protocol as well as how to utilize the Zoom APIs and SendGrid APIs. In addition to this, I gained more knowledge on building an object-oriented program with the use of the Java programming language.

## What's next for Zoom Scheduler

Zoom Scheduler currently works great, but I see a lot of opportunities for improvement. The main one I would like to accomplish in the future is scheduling with multiple dates. Currently Zoom Scheduler only works for LettuceMeet sessions where there's one day. I also see an opportunity to clean up the code so that future improvements are not a big hassle.
