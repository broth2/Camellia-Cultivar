# Camellia-Cultivar
## How to run
```
./run_docker.sh
```

**Admin Login:** nunoadmin@ua.pt:batman123  
**User Login:**  nunofahla@ua.pt:batman123

Webapp available on *http://192.168.160.226:3000*  
Backend API availble on *https://192.168.160.226:8085* 
Flask API GET endpoint *http://192.168.160.226:5000/predict?url=*
Twitter page *https://twitter.com/onthisdayalbum*

If running locally, replace *192.168.160.226* with *localhost* on **RequestControlller.java** and **proxy.js**

## Changes made
- Reputation algorithm improvements
- Reputation tiers represented in user profile
- Emailing service changed and diversified the type of notifications
- Twitter bot for camellia confirmation and milestone accomplishments
- Camellias can now be reported to admins/mods, in case the system misidentifies them
- Admins/mods can refuse or accept the report request, removing the request or both the request and the camellia, respectively
- Camellia Recognition System to filter photographs
- Fully deployed to a **DETI**'s vm on *http://192.168.160.226:3000/*
- Both backend and frontend are in docker containers
- Set the docker-compose HTTP request timeout to a higher value
- *Reference* quiz answers are processed before *To Identify* quiz answers
- Unidentified specimen quizzes now appear in quiz groups
- Quiz groups now can have less than 3 unidentified specimens
- Quiz groups now need to have 6 reference specimens
- Made it so all cultivars and specimens that initialize the database have photos
- Reference cultivares in quizzes refer to actual different cultivars
- Removed useless imports and variables from backend
- Removed Moderator reference in Request Class, since it served no pourpose
- Request's getters provide more varied and pertinent information
- Report Request and Report Request DTO classes created
- Created an endpoint to delete cultivars
- API container now always restarts if it fails to connect to DB(in case it boots before it)
- SpecimenDTO class now has an image url attribute