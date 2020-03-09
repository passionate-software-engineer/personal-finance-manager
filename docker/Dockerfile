FROM ubuntu:16.04

# Install tools
RUN apt-get -qqy update && apt-get -qqy --assume-yes --no-install-recommends install \
  apt-utils \
  curl \
  ca-certificates \
  openssh-server \
  git \
  sudo \
  nano \
  wget \
  jq

# Install NodeJs
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get -qqy update && apt-get -qqy --assume-yes --no-install-recommends install nodejs

# Install Google chrome
RUN curl -sL https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN echo 'deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main' | tee /etc/apt/sources.list.d/google-chrome.list
RUN apt-get -qqy update && apt-get -qqy --assume-yes --no-install-recommends install google-chrome-stable

# Install npm global packages
RUN npm install -g @angular/cli protractor npm-check-updates

# Install java
RUN wget https://github.com/AdoptOpenJDK/openjdk13-binaries/releases/download/jdk-13.0.2%2B8/OpenJDK13U-jdk_x64_linux_hotspot_13.0.2_8.tar.gz -O /tmp/openjdk-13.tar.gz
RUN mkdir /usr/lib/jvm
RUN tar xfvz /tmp/openjdk-13.tar.gz --directory /usr/lib/jvm
RUN rm -f /tmp/openjdk-13.tar.gz
ENV JAVA_HOME /usr/lib/jvm/jdk-13.0.2+8
ENV PATH $JAVA_HOME/bin:$PATH

# Add Jenkins user
RUN useradd -ms /bin/bash jenkins -g sudo
RUN echo "jenkins:jenkins" | chpasswd
RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Install aws-cli
RUN curl -O https://bootstrap.pypa.io/get-pip.py
RUN python3 get-pip.py --user
ENV PATH /root/.local/bin:$PATH
RUN pip install awscli --upgrade --user
