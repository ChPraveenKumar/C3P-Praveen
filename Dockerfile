FROM camunda/camunda-bpm-platform:wildfly-7.13.0
EXPOSE 8082
ADD target/C3P.war /camunda/standalone/deployments/
CMD ["/camunda/camunda.sh","-b","0.0.0.0", "-bmanagement" ,"0.0.0.0"]
