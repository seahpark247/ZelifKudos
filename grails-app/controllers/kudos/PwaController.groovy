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

    def badge() {
        serveImage("static/badges", params.filename)
    }

    def icon() {
        serveImage("static/icons", params.filename)
    }

    private void serveImage(String dir, String filename) {
        // Reject anything with a path separator: params come from the URL, and
        // "../../application.yml" would otherwise walk out of the image folder.
        if (!filename || filename.contains('/') || filename.contains('\\') || filename.contains('..')) {
            render status: 400
            return
        }
        def resource = new ClassPathResource("${dir}/${filename}")
        if (!resource.exists()) {
            render status: 404
            return
        }
        response.contentType = "image/png"
        response.outputStream << resource.inputStream
        response.outputStream.flush()
    }
}
