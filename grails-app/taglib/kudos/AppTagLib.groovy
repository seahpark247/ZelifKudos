package kudos

import grails.core.GrailsApplication

class AppTagLib {

    static namespace = 'app'

    GrailsApplication grailsApplication

    /**
     * The configured product name — "MobileIT Kudos", "Kudos", whatever
     * APP_NAME is set to. Kept in one place so rebranding never means hunting
     * strings through the views again.
     *
     *   <app:name/>            in markup
     *   ${app.name()}          inside an attribute
     */
    def name = { attrs ->
        out << (grailsApplication.config.getProperty('app.displayName') ?: 'Kudos')
    }

}
