# Create by Xie Xianbin, For centos7 jdk image
# v 1.0.0

# base image
FROM ansible/centos7-ansible:latest

# remark
MAINTAINER  fuzq "fuzq@bjshfb.com"

# Dockerfile build cache
ENV REFRESHED_AT 2018-0-25

COPY apache-tomcat-8.5.34.tar.gz /opt/
COPY jdk-8u181-linux-x64.rpm /opt/

RUN rpm -ivh /opt/jdk-8u181-linux-x64.rpm
RUN tar -zxf /opt/apache-tomcat-8.5.34.tar.gz -C /opt/

RUN rm -rf /opt/jdk-8u181-linux-x64.rpm
RUN echo "sh /opt/apache-tomcat-8.5.34/bin/startup.sh" >> /etc/rc.local
RUN rm -rf /opt/apache-tomcat-8.5.34/webapps/docs
RUN rm -rf /opt/apache-tomcat-8.5.34/webapps/examples
RUN rm -rf /opt/apache-tomcat-8.5.34/webapps/host-manager
RUN rm -rf /opt/apache-tomcat-8.5.34/webapps/manager

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    cron && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

RUN chmod +x ./docker-entrypoint.sh
ENV LC_ALL C.UTF-8

RUN yum clean all
RUN history -c

EXPOSE 8080
ENTRYPOINT ["./docker-entrypoint.sh"]