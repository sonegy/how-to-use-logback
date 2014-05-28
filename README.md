# How to use logback

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

logback을 사용할수 있는 준비가 끝났습니다. 하지만 실제 프로젝트는 여러가지 의존라이브러리가 추가되면 다른 loggin framework는 삭제하고 logback으로 통합해줘야 합니다. 후반에 다시 설명하겠습니다.

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
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
    }
}
```
Logger와 LoggerFactory는 SLF4J에 있는 interface와 implements입니다. 실제 어플리케이션상에 logger를 정의 할때 logback 관련 코드를 넣지는 않습니다. 다시 말해 logback과 dependency가 없는 코드를 구현하게 되면 차후 다른 logging framework로 교체하는게 가능하게 됩니다.
```
		Logger logger = LoggerFactory.getLogger(Tutorial.class);
```
logback에 있는 LoggerFactory와 StatusPrinter를 활용해서 현재 logback설정 상태를 확인 할 수 있습니다. 사실 아래 현재 logback상태를 출력하기 위해 logback class로 강제 캐스팅을 했습니다. 사실 ch.qos.logback에 로그 설정을 하면 관련 로그를 볼수 있습니다.
```
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
```
```console
20:57:59.346 [main] DEBUG example.logback.Tutorial - debug
20:57:59.350 [main] INFO  example.logback.Tutorial - info
20:57:59.350 [main] WARN  example.logback.Tutorial - warn
20:57:59.350 [main] ERROR example.logback.Tutorial - error
20:57:59,270 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
20:57:59,270 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
20:57:59,271 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.xml]
20:57:59,273 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Setting up default configuration.
```
logback.groovy, logback-test.xml, logback.xml resource가 없다면 디폴트 설정 이라는 정보를 출력합니다. 현재 로그상태를 알수 있습니다. logback.xml를 classpath에 추가 되면 변경된 설정을 적용할 수 있습니다. 그리고 junit에서는 별도의 logbac-test.xml을 둔다면 우선 설정 됩니다.

## Logger
### Log Name
Logger들은 이름 기반으로 생성이 됩니다. LoggerFactory.getLogger("NAME")로 Logger를 호출하면 "NAME"에 대한 딱하나의 instance를 반환합니다. String 대신 .class로 클래스 정보를 넘겨주면 `.getName()`으로 클래스 이름을 사용하게 됩니다. 흔히 Class객체를 넘겨주어 결국 이름은 `packageName.ClassName`으로 구성이 됩니다.

그럼으로 우리가 만드는 어플리케이션에서의 Logger들은 `.` 으로 구분된 Hierarchy가 생기게 되는것 입니다. 일종의 상속 구조가 됩니다.
코드로 예를 들어 봅시다.
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
간단히 설정을 설명하면 `<root/>` 정보는 모든 Logger NAME들의 최상의를 가리킵니다. 현재 설정은 ROOT이하 모든 NAME들은 level을 DEBUG 기준(DEBUG,INFO,WARN,ERROR 포함)으로 하고 ConspleAppender를 사용하여 console에 출력하라는 의미입니다.
```
    <logger name="example.logback.level.grandparents" level="TRACE"/>
```
example.logback.level.grandparents 이하 모든 Logger들의 level은 TRACE라는 설정입니다.

### Level 종류
1. trace
1. debug
1. info
1. warn
1. error

위처럼 5단계의 level이 존재합니다. TRACE라고 선언을 하면 trace,debug,info,warn,error 모든 Level을 포함합니다. 그리고 INFO로 설정된다면 INFO,WARN,ERROR의 Level이 포함 됩니다.
```
    public static void main(String[] args) {
        new Grandparents().run();
        new Parents().run();
        new Children().run();
    }
```
아래의 결과는 현재 logback.xml에 logger설정에 따른 결과입니다. Parents도 trace부터 출력되고, Children은 info부터 출력됨을 확인했습니다.
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
결국 Logger은 Name의 상속(Name의 prefix가 같음음 말함)으로 설정된 level기준으로 log를 출력하게 됩니다. 그리고 level은 해당 level이하를 포함함을 알수 있습니다.

## Appender

## Layouts
## Filters

초간단 쓰는법
appender
root
