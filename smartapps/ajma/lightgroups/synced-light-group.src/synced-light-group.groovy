definition(
    name: "Synced Light Group",
    namespace: "ajma/lightgroups",
    author: "a@ajmas.com",
    description: "Use a master switch to control state",
    category: "My Apps",
    parent: "ajma:Synced Light Groups",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png")

preferences {
    page(name: "pageOne", nextPage: "pageTwo", uninstall: true) {
        section("Select a master switch...") { 
            input "master", "capability.switch", 
                multiple: false, 
                title: "Master Switch...", 
                required: true
        }

        section("And these will follow...") {
            input "onoffSwitches", "capability.switch", 
                multiple: true, 
                title: "On/Off Switchs to sync...", 
                required: false
            input "dimmerSwitches", "capability.switchLevel", 
                multiple: true, 
                title: "Dimmer Switchs to sync...", 
                required: false
            input "colorTempSwitches", "capability.colorTemperature", 
                multiple: true, 
                title: "Tunable White Bulbs to sync...", 
                required: false
        }
    }
    
    page(name: "pageTwo", install: true, uninstall: true) {
        section("labels") {
            label(name: "label",
                  title: "Assign a name",
                  required: false,
                  multiple: false)
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
	app.updateLabel(master.displayName)
    
    subscribe(master, "switch", masterSwitchHandler)
    subscribe(master, "level", masterLevelHandler)
    subscribe(master, "colorTemperature", masterColorTempHandler)
    subscribe(onoffSwitches, "switch", slavesSwitchHandler)
    subscribe(dimmerSwitches, "switch", slavesSwitchHandler)
    subscribe(colorTempSwitches, "switch", slavesSwitchHandler)
}

def masterSwitchHandler(evt) {
    log.info "masterOnHandler Event: ${evt.value}"
    if (evt.value == "on") {
        onoffSwitches?.on()
        dimmerSwitches?.on()
        colorTempSwitches?.on()
    } else if (evt.value == "off") {
        onoffSwitches?.off()
        dimmerSwitches?.off()
        colorTempSwitches?.off()
    } else {
        log.error "masterOnHandler unable to handle ${evt.value}"
    }
}

def masterLevelHandler(evt)
{	
    def level = evt.value.toFloat()
    level = level.toInteger()
    log.info "masterLevelHandler Event: ${level}"
    dimmerSwitches?.setLevel(level)
    colorTempSwitches?.setLevel(level)
}

def masterColorTempHandler(evt)
{	
    def level = evt.value.toFloat()
    level = level.toInteger()
    log.info "masterColorTempHandler Event: ${level}"
    colorTempSwitches?.setColorTemperature(level)
}

def slavesSwitchHandler(evt) {
    log.info "slavesSwitchHandler Event: ${evt.value}"
    if (evt.value == "on") {
        def allOn = areSwitchValuesEqualTo(onoffSwitches, "on") && areSwitchValuesEqualTo(colorTempSwitches, "on")
        if(allOn && master.currentValue("switch") == "off")
            master.on()
    } else if (evt.value == "off") {
        def allOff = areSwitchValuesEqualTo(onoffSwitches, "off") && areSwitchValuesEqualTo(colorTempSwitches, "off")
        if(allOff && master.currentValue("switch") == "on")
            master.off()
    } else {
        log.error "slavesSwitchHandler unable to handle ${evt.value}"
    }
}

def areSwitchValuesEqualTo(switches, value) {
    def allSame = true
    for(s in switches) {
        if(s.currentValue("switch") != value) {
            allSame = false
            last
        }
    }
    allSame
}