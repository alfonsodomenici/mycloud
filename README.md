# Build
mvn clean package && docker build -t it.ciacformazione/mycloud .

# RUN

docker rm -f mycloud || true && docker run -d -p 8080:8080 -p 4848:4848 --name mycloud it.ciacformazione/mycloud 