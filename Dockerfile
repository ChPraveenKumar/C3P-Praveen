FROM jboss/wildfly
EXPOSE 8080
ADD target/C3P.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh","-b","0.0.0.0", "-bmanagement" ,"0.0.0.0"]