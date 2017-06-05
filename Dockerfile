FROM openjdk

MAINTAINER Matthias Zimmermann <matthias.zimmermann@bsi-software.com>

# workaround to ensure access to internet from within container (windows issue)
RUN echo "nameserver 8.8.8.8" > /etc/resolv.conf

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

#--- maven installation ------------------------------------------------------#

ENV MAVEN_VERSION 3.3.3

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

# create a java hello world to download mvn standard packages into javadev image
RUN cd \
  && mvn archetype:generate -DgroupId=org.thethingsnetwork.helloworld -DartifactId=helloworld -DinteractiveMode=false \
  && cd helloworld \
  && mvn clean package

