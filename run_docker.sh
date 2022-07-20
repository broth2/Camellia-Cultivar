cd backend/camellia/

mvn package

cd ../..

docker-compose rm -f
docker-compose pull
COMPOSE_HTTP_TIMEOUT=180 docker-compose up --build
