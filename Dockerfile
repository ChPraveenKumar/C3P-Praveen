FROM tomcat:8

COPY tomcat-users.xml /usr/local/tomcat/conf/

COPY build/libs/* /usr/local/tomcat/webapps/
