The <i>Track</i> App
-----
The Track Android application has been developed as part of a joint research project between the Schools of Informatics and Health in Social Science at the University of Edinburgh, and is available on the Google Play store at https://play.google.com/store/apps/details?id=informatics.uk.ac.ed.track.

The app provides emotion tracking by requesting users to fill out short surveys throughout the day, and providing visual feedback in the form of illustrative graphs.

Non-participants of the study can use the app to personally track their emotions and daily activities. Any diary entries provided by non-participants are kept solely on their personal device and never shared with University researchers.

Survey Configurability
-----
<i>Track</i> has been developed in such a way so that the survey questions are loaded dynamically from the <b>survey_json.txt</b> file supplied as a raw resources to the application.

The application currently supports five different types of questions:
- free-text, single-line
- free-text, multiple-line
- multiple-choice, multiple-answer
- multiple-choice, single-answer
- Likert scale.

The <i>TrackLib</i> API can be used to generate <i>Track</i>-compatible JSON surveys, so that researchers can use the application to run their own ESM study.

Configuration
-----
If you are supplying a custom survey_json.txt file, make sure to set this useFeedbackModule in the <b>config.xml</b> file to ```false```, as this module contains code specifically tailored to the current survey.
```
<bool name="useFeedbackModule">false</bool>
```

If you are using this application for data gathering, you will also need to update the web server details to point to your own host, so that the user responses are uploaded accordingly.

```
<!-- Web Service details -->
<string name="trackWebServiceUrl">http://trackserver.no-ip.org/webservice/</string>
<string name="loginWebMethod">login.php</string>
<string name="addSurveyResponseWebMethod">addSurveyResponse.php</string>
```

License
---
Copyright 2015 Rachel Gauci<br/>
<i>This work is licensed under a Creative Commons Attribution 4.0 License.</i>

Acknowledgement
---
<i>The research work disclosed in this publication is funded by the MASTER it! Scholarship Scheme (Malta). The scholarship is part-financed by the European Union - European Social Fund (ESF) under Operational Programme II - Cohesion Policy 2007-2013, “Empowering People for More Jobs and a Better Quality of Life”.</i>
