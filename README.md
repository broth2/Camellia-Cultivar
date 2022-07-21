# Camellia-Cultivar
## How to run
```
cd backend
./run_docker.sh
cd ../webapp npm start
```

**Admin Login:** nunoadmin@ua.pt:batman123  
**User Login:**  nunofahla@ua.pt:batman123

Webapp available on *http://localhost:3000*  
Backend API availble on *https://localhost:8085*
Twitter page https://twitter.com/onthisdayalbum

## Changes made
- Reputation algorithm improvements
- Reputation tiers represented in user profile
- Emailing service changed and diversified the type of notifications
- Twitter bot for camellia confirmation and milestone accomplishments
- Camellias can now be reported to admins/mods, in case the system misidentifies them
- Admins/mods can refuse or accept the report request, removing the request or both the request and the camellia, respectivly
- Both backend and frontend are in docker containers
- Set the docker-compose HTTP request timeout to a higher value
- *Reference* quiz answers are processed before *To Identify* quiz answers
- Unidentified specimen quizzes now appear in quiz groups
- Quiz groups now can have less than 3 unidentified specimens
- Quiz groups now need to have 6 reference specimens
- Made it so all cultivars and specimens that initialize the database have photos
- Reference cultivares in quizzes refer to actual different cultivars
- Removed useless imports and variables from backend
- Removed Moderator reference in Requests, since it served no pourpose
- Requests getters provide more varied information
- Report Request and Report Request DTO classes created
- Delete cultivar endpoint created
- API container now always restarts if it fails to connecto do DB(in case it boots before it)