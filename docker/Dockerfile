FROM ubuntu:16.04

# Install tools
RUN apt-get -qqy update && apt-get -qqy --force-yes --no-install-recommends install \
  curl \
  ca-certificates \
  openssh-server \
  git \
  sudo \
  nano \
  openjdk-8-jdk

# Install NodeJs
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get -qqy update && apt-get -qqy --force-yes --no-install-recommends install nodejs

# Install Google chrome
RUN curl -sL https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN echo 'deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main' | tee /etc/apt/sources.list.d/google-chrome.list
RUN apt-get -qqy update && apt-get -qqy --force-yes --no-install-recommends install google-chrome-stable

# Install npm global packages
RUN npm install -g @angular/cli protractor ncu 

# Add Jenkins user
RUN useradd -ms /bin/bash jenkins -g sudo
RUN echo "jenkins:jenkins" | chpasswd
RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers