FROM adoptopenjdk/openjdk11:jdk-11.0.7_10-alpine-slim

WORKDIR .
ADD ./cli/build/libs/cli-0.0.1-all.jar app.jar
ENV BPP_PROJECT_DIR=/home/app/project/
CMD java -jar app.jar
