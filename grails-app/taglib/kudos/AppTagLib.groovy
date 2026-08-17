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

    /**
     * Chat rate limits, emitted as plain integers so the browser enforces the
     * same windows the WebSocket controller does. Without this the client sends
     * into a guard it cannot see and the user's text disappears.
     */
    def chatCooldownMs = { attrs ->
        out << grailsApplication.config.getProperty('app.chat.cooldownMs', Integer, 3000)
    }

    def chatDuplicateWindowMs = { attrs ->
        out << grailsApplication.config.getProperty('app.chat.duplicateWindowMs', Integer, 10000)
    }
}
