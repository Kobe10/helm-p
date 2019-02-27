#/bin/sh
APP_HOME=/home/eland/apache-tomcat-7.0.63/webapps/eland_dev
echo $APP_HOME
SERV_HOME=/home/eland/apache-tomcat-7.0.63
echo $SERV_HOME
#stop tomcat server
cd $SERV_HOME/bin
su - eland -c $SERV_HOME/bin/stop.sh
#delete deploy war files
rm -fR $APP_HOME/*
#copy new war file to deploy
cp $WORKSPACE/eland-web/target/*.war $APP_HOME
#deyloy war
cd $APP_HOME
jar -xf $APP_HOME/*.war
#replace deploy file
cp -f $WORKSPACE/eland-web/build/dev/jdbc.properties $APP_HOME/WEB-INF/classes
cp -f $WORKSPACE/eland-web/build/dev/log4j2.xml $APP_HOME/WEB-INF/classes
cp -f $WORKSPACE/eland-web/build/dev/oframeweb.properties $APP_HOME/WEB-INF/classes
cp -f $WORKSPACE/eland-web/build/dev/config.json $APP_HOME/oframe/plugin/ueditor/jsp
## change file owner
chown -R eland $APP_HOME/*
## start server
su - eland -c ${SERV_HOME}/bin/startup.sh