package kudos

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "login", action: "index")
        "/demo"(controller: "demo", action: "list")
        "/manifest.json"(controller: "pwa", action: "manifest")
        "/service-worker.js"(controller: "pwa", action: "serviceWorker")
        "/icons/$filename"(controller: "pwa", action: "icon")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
