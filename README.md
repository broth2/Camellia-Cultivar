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
- Emailing service changed and diversified the type of notifications
- Reputation tiers represented in user profile
- Both backend and frontend are in docker containers
- Twitter bot for camellia confirmation and milestone accomplishments
- Set the docker-compose HTTP request timeout to a higher value
- *Reference* quiz answers are processed before *To Identify* quiz answers