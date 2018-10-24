FROM jenkins/jnlp-slave
MAINTAINER Kimbro Staken

USER root
RUN apt-get update
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get install -y nodejs shuf
RUN npm install -g @angular/cli
RUN npm install -g ncu