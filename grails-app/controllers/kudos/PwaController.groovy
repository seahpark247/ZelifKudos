package kudos

import grails.core.GrailsApplication
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.core.io.ClassPathResource

class PwaController {

    GrailsApplication grailsApplication

    def manifest() {
        def manifest = new JsonSlurper().parse(
            new ClassPathResource("static/manifest.json").inputStream, 'UTF-8')
        // Only "name" is branded. "short_name" stays short on purpose — home
        // screen labels get truncated at roughly a dozen characters.
        manifest.name = grailsApplication.config.getProperty('app.displayName') ?: 'Kudos'
        render(text: JsonOutput.toJson(manifest),
               contentType: "application/manifest+json",
               encoding: "UTF-8")
    }

    def serviceWorker() {
        def resource = new ClassPathResource("static/service-worker.js")
        response.contentType = "application/javascript"
        response.outputStream << resource.inputStream
        response.outputStream.flush()
    }

    def icon() {
        String filename = params.filename
        def resource = new ClassPathResource("static/icons/${filename}")
        if (!resource.exists()) {
            render status: 404
            return
        }
        response.contentType = "image/png"
        response.outputStream << resource.inputStream
        response.outputStream.flush()
    }
}
