package kudos

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync

@CompileStatic
@EnableAsync
@Import([WebSocketConfig, SessionConfig])
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
