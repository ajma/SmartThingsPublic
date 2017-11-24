definition(
    name: "Synced Light Groups",
    namespace: "ajma",
    author: "Andrew Ma",
    description: "Use a master switch to control state",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png")


preferences {
    // The parent app preferences are pretty simple: just use the app input for the child app.
    page(name: "mainPage", title: "Groups", install: true, uninstall: true, submitOnChange: true) {
        section {
            app(name: "groups", appName: "Synced Light Group", namespace: "ajma/lightgroups", title: "Create New Group", multiple: true)
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    // nothing needed here, since the child apps will handle preferences/subscriptions
    // this just logs some messages for demo/information purposes
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
}