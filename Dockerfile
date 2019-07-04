FROM airhacks/glassfish
COPY ./target/mycloud.war ${DEPLOYMENT_DIR}
