# logback-kafka

Logback appenders for logging data to Apache Kafka 0.8.0, based on the project at https://github.com/ptgoetz/logback-kafka (which is for Kafka 0.7).


## Maven Dependency
To use logback-kafka in your project add to following to your pom.xml:

```xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
...
<dependency>
  <groupId>org.clojars.brenden</groupId>
  <artifactId>logback-kafka</artifactId>
  <version>0.2.7</version>
</dependency>
```

## Configuration

To configure your application to log to kafka, add an appender entry in your logback configuration file, and specify 
a zookeeper host string, and kafka topic name to log to.


```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <appender name="KAFKA"
        class="org.clojars.brenden.logback.KafkaAppender">
        <topic>mytopic</topic>
        <zookeeperHost>localhost:2181</zookeeperHost>
    </appender>
    <root level="debug">
        <appender-ref ref="KAFKA" />
    </root>
</configuration>
```

## Overriding Default Behavior
By default, the Kafka appender will simply write the received log message to the kafka queue. You can override this 
behavior by specifying a custom formatter class:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <appender name="KAFKA"
        class="org.clojars.brenden.logback.KafkaAppender">
        <topic>foo</topic>
        <zookeeperHost>localhost:2181</zookeeperHost>
        <!-- specify a custom formatter -->
        <formatter class="org.clojars.brenden.logback.KafkaAppender">
            <!-- 
            Whether we expect the log message to be JSON encoded or not.
            If set to "false", the log message will be treated as a string, 
            and wrapped in quotes. Otherwise it will be treated as a parseable
            JSON object.
            -->
            <expectJson>true</expectJson>
        </formatter>
    </appender>
    <root level="debug">
        <appender-ref ref="KAFKA" />
    </root>
</configuration>
```



Formatters simply need to implement the `org.clojars.brenden.logback.formatter.Formatter` interface:

```java
package org.clojars.brenden.logback.formatter;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Formatter {
    String format(ILoggingEvent event);
}
```

You can find the `ch.qos.logback.classic.spi.ILoggingEvent` javadoc [here](http://logback.qos.ch/apidocs/ch/qos/logback/classic/spi/ILoggingEvent.html).



