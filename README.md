# How to use logback

[TOC]

1. logback은 무엇?
2. 아주 간단하게 실행.
3. Logger, LoggerFactory는 slfg4j를 사용.
4. logback.xml, logback-test.xml, logback.grooby 우선 순위
5. 설정 설명
6. Appender. Layout, Filter,
7. ConsoleAppender, FileAppender, 롤링. SMTPAppender
8. SimpleSokectServer

## logback이란

logback은 SLF4J의 native 구현체 입니다. slf4j로 어플리케이션 로그를 남긴다면 logback을 선택하는게 가장 좋습니다. slf4j의 도움으로 연관 라이브러리들이 다른 logging framework를 쓰더라도 logback으로 통합할 수 있습니다.

logback 은 logback-core, logback-classic, logback-access의 3개의 모듈이 있습니다. core는 classic과 access의 공통라이브러리입니다. maven repository를 쓴다면 classic만 추가하면 관련 라이브러리가 추가 됩니다.

Maven pom.xml
```
    <dependencies>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.2</version>
        </dependency>
    </dependencies>
```
```libraries
External Libraries
Maven: ch.qos.logback:logback-classic:1.1.2
Maven: ch.qos.logback:logback-core:1.1.2
Maven: org.slf4j:slf4j-api:1.7.6
```

## 전격 실행
logback을 사용할수 있는 준비가 끝났습니다. 아래 코드를 바로 실행해봅시다.
```
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tutorial {
    private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);

    public static void main(String[] args) {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
```
## Logger, LoggerFactory
Logger와 LoggerFactory는 SLF4J에 있는 interface와 implements입니다. 실제 어플리케이션상에 logger를 정의 할때 logback 관련 코드를 넣지는 않습니다. 다시 말해 logback과 dependency가 없는 코드를 구현하게 되면 차후 다른 logging framework로 교체하는게 가능하게 됩니다.
```
		Logger logger = LoggerFactory.getLogger(Tutorial.class);
```
```console
20:57:59.346 [main] DEBUG example.logback.Tutorial - debug
20:57:59.350 [main] INFO  example.logback.Tutorial - info
20:57:59.350 [main] WARN  example.logback.Tutorial - warn
20:57:59.350 [main] ERROR example.logback.Tutorial - error
```

## logback.groovy, logback-test.xml, logback.xml
logback.groovy, logback-test.xml, logback.xml resource가 없다면 디폴트 설정 이라는 정보를 출력합니다. 현재 로그상태를 알수 있습니다. 만약 모든 설졍이 root classpath에 등록이 되어 있다면. 우선순위 전략을 따랍니다.

1. logback.groovy 파일을 먼저 찾습니다.
2. 없다면 logback-test.xml 을 찾습니다.
3. 그래도 없다면 logback.xml을 찾습니다.
4. 모두 없다면 기본 설정 전략을 따릅니다. BasicConfiguration

maven 프로젝틀르 사용하는 경우에는 test/resources에 logback-test.xml을 두고 테스트가 실행될때는 logback-test.xml이 우선적용되도록 사용합니다. 어때요. 참 쉽죠?

## 좀더 멋진 maven dependency 설정
표준은 좋은것이죠. 사실 로그에 대한 표준이 없지만, SLF4J를 사용하게 된다면 다양한 logging framework을 선택적으로 사용할수 있습니다. SLF4J에만 코드가 의존적이 된다면 아주 좋죠. 그래서 사실 java compile에서는 logback이 필요 없습니다. 아래 pom.xml 설정에서 `<scope>runtime</scope>` 로 logback-classic 을 설정하면 logback-core도 같은 조건이 됩니다.

pom.xml
```
    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.2</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
            </exclusions>
            <scope>runtime</scope>
         </dependency>
    </dependencies>
```

## Logger Name
Logger들은 이름 기반으로 생성이 됩니다. LoggerFactory.getLogger("NAME")로 Logger를 호출하면 "NAME"에 대한 딱하나를 instance를 반환합니다. 여러번 호출해도 같은 객체입니다. String 대신 .class로 클래스 정보를 넘겨주면 `.getName()`으로 클래스 이름을 사용하게 됩니다. 흔히 Class객체를 넘겨주어 결국 이름은 `packageName + ClassName`으로 구성이 됩니다. 흔히 Logger는 특정 패키지 이하로 재한을 두고 정의를 합니다.

그럼으로 우리가 만드는 어플리케이션에서의 Logger들은 `.` 으로 구분된 Hierarchy가 생기게 되는것 입니다. 일종의 상속 구조가 됩니다.코드로 예를 들어 봅시다.
```
example.logback.level.Grandparents
example.logback.level.grandparents.Parents
example.logback.level.grandparents.parents.Children
```
위처럼 3개의 Class Grandparents, Parents, Children를 위와 같은 구조로 생성을 하고 아래처럼 Logger를 class마다 생성하면 3개의 Logger가 full package+className으로 생성이 됩니다.
```
    private static final Logger logger = LoggerFactory.getLogger(Grandparents.class);
    private static final Logger logger = LoggerFactory.getLogger(Parents.class);
    private static final Logger logger = LoggerFactory.getLogger(Children.class);

    ...
    // 각각 클래스마다 구현
    public void run() {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
```
Logger들은 Tree Hierarchy 구조로 level을 적용 받을수 있습니다.

logback.xml 설정
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm} %-5level %logger{36} - %msg%n</Pattern>
        </layout>
    </appender>

    <logger name="example.logback.level.grandparents" level="TRACE"/>
    <logger name="example.logback.level.grandparents.parents.children" level="INFO"/>
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```
여기서 `root`는 root logger를 말합니다. logger name=""과 같다고 생각하면 됩니다.
```
    <logger name="example.logback.level.grandparents" level="TRACE"/>
```
example.logback.level.grandparents 이하 모든 Logger들의 level은 TRACE라는 설정입니다.

## Logger Level
1. trace
2. debug
3. info
4. warn
5. error

위처럼 5단계의 level이 존재합니다. TRACE라고 선언을 하면 trace,debug,info,warn,error 모든 Level을 포함합니다. 그리고 INFO로 설정된다면 info.warn,error이 포함 됩니다.
```
    public static void main(String[] args) {
        new Grandparents().run();
        new Parents().run();
        new Children().run();
    }
```
```console
20:23 TRACE e.l.level.grandparents.Grandparents - trace
20:23 DEBUG e.l.level.grandparents.Grandparents - debug
20:23 INFO  e.l.level.grandparents.Grandparents - info
20:23 WARN  e.l.level.grandparents.Grandparents - warn
20:23 ERROR e.l.level.grandparents.Grandparents - error
20:23 TRACE e.l.l.grandparents.parents.Parents - trace
20:23 DEBUG e.l.l.grandparents.parents.Parents - debug
20:23 INFO  e.l.l.grandparents.parents.Parents - info
20:23 WARN  e.l.l.grandparents.parents.Parents - warn
20:23 ERROR e.l.l.grandparents.parents.Parents - error
20:23 INFO  e.l.l.g.parents.children.Children - info
20:23 WARN  e.l.l.g.parents.children.Children - warn
20:23 ERROR e.l.l.g.parents.children.Children - error
```
Logger Name의 Level설정은 Name마다 상속 적용됩니다. grandparents의 TRACE설정은 이하 모두 적용이 되고, children 이하 INFO로 재지정 됩니다.

## Appender
**Event마다 Log를 기록하는 기능**은 Appender가 처리합니다. 그래서 Logger는 어떤 appender에 해당이 되어 처리 되는게 중요합니다. Appender를 설정하더래도 log출력에 해당되지 않으면 작동하지 않습니다.
Appender는 출력될 형식을 직접 가지고 있지 않고, 해당 기능은 Layout과 Encoder에 위임을 합니다.

### ConsoleAppender
ConsoleAppender 는 OutputStreamAppender를 상속합니다. **encoder, pattern**으로 PatternLayoutEncoder가 생성해서 Appender에 주입됩니다.
```
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder>
      <pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n</pattern>
    </encoder>
  </appender>

```
PatternLayoutEncoder는 pattern으로 받은 값을 이용해서 PatternLayout을 생성하고 PatternLayoutEncoder는 log message를 byte[]로 변환하는 기능도 포함합니다. 이로써 Appender는 Layout기능과 Encoder기능을 모두 가지게 됩니다. 이것을 이용해서 OutputStreamAppender는 byte[]를 OuputStream에 write하게 됩니다.

### FileAppender

## Layouts
## Filters
## Configuration

초간단 쓰는법
appender
root
