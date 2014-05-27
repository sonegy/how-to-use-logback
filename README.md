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
logback에 있는 LoggerFactory와 StatusPrinter를 활용해서 현재 logback설정 상태를 확인 할 수 있습니다.
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
logback.groovy, logback-test.xml, logback.xml resource가 없다면 디폴트 설정 이라는 정보를 출력합니다. 현재 로그상태를 알수 있습니다. logback.xml를 classpath에 추가 되면 변경된 설정을 적용할 수 있습니다. 그리고 junit에서는 별도의 logbac-test.xml을 둔다면 설정을 test설정우선으로 둘수 있습니다.

## Architecture
## Appender
## Layouts
## Filters

초간단 쓰는법
appender
root
