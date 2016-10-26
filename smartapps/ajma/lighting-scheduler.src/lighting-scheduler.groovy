/**
 *  Copyright 2016 Andrew Ma
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Lighting Scheduler
 *
 *  Author: SmartThings
 */
definition(
    name: "Lighting Scheduler",
    namespace: "ajma",
    author: "Andrew Ma",
    description: "Set lighting color temperature and level based on a fixed schedule.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png"
)

preferences {
	section("Control these bulbs...") {
		input "ctbulbs", "capability.colorTemperature", title: "Which Temperature Changing Bulbs?", multiple:true, required: false
		input "dimmers", "capability.switchLevel", title: "Which Dimmers?", multiple:true, required: false
	}
	section("First Schedule") {
    	input "time1", "time", title: "What time?"
        input "temperature1", "number", title: "Color Temperature", range: "2700..5000"
        input "level1", "number", title: "Level (1-100%)", range: "1..100"
        input "manual1", "capability.momentary", title: "Manually Run when pressed", required: false
	}
    section("Second Schedule") {
    	input "time2", "time", title: "What time?", required: false
        input "temperature2", "number", title: "Color Temperature", range: "2700..5000", required: false
        input "level2", "number", title: "Level (1-100%)", range: "1..100", required: false
        input "manual2", "capability.momentary", title: "Manually Run when pressed", required: false
	}
}

def installed() {
	initialize(	)
}

def updated() {
	unschedule()
	initialize()
}

def initialize() {
	log.info "Schedule 1 is ${time1}"
	log.info "Schedule 2 is ${time2}"
    log.debug "momentary"
	schedule(time1, schedule1)
    subscribe(ctbulbs, "switch.on", switchOnHandler)
    subscribe(manual1, "momentary", manual1Handler)
    subscribe(manual2, "momentary", manual2Handler)
    state.nextLevel = [:]
    state.nextColor = [:]
}

def schedule1() {
	setLighting(1, temperature1, level1);
    unschedule()
    schedule(time2, schedule2)    
}

def schedule2() {
	setLighting(2, temperature2, level2);
    unschedule()
	schedule(time1, schedule1)
}

def manual1Handler(evt) {
	log.debug "test"
    log.debug evt.value
	if (evt.value == "pushed") {
    	log.debug "Manual 1 button pushed"
		setLighting(1, temperature1, level1);
	}
}

def manual2Handler(evt) {
	if (evt.value == "pushed") {
    	log.debug "Manual 2 button pushed"
		setLighting(2, temperature2, level2);
	}
}

def setLighting(number, temperature, level) {
	log.info "Running schedule $number. Temp: $temperature Level $level"
	for(ctbulb in ctbulbs) { 		
        if(ctbulb.currentValue("switch") == "on") {
        	ctbulb.setLevel(level);
            ctbulb.setColorTemperature(temperature)
            log.info "Setting bulb to level ${level} and temperature ${temperature} for ${ctbulb.name}"
        } else {
        	state.nextLevel.put(ctbulb.id, level)
            state.nextColor.put(ctbulb.id, temperature)
        	log.info "Bulb is off, queuing level: ${level} temperature ${temperature} for ${ctbulb.name}"
        }
	}
}

def switchOnHandler(evt) {
	if(state.nextLevel[evt.deviceId]) {
    	log.info "Applying queued bulb level ${state.nextLevel[evt.deviceId]}"
        evt.device.setLevel(state.nextLevel[evt.deviceId])
        state.nextLevel.remove(evt.deviceId)
    }
    if(state.nextColor[evt.deviceId]) {
    	log.info "Applying queued bulb color ${state.nextColor[evt.deviceId]}"
        evt.device.setColorTemperature(state.nextColor[evt.deviceId])
        state.nextColor.remove(evt.deviceId)
    }
}