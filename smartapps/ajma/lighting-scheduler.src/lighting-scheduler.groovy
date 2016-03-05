/**
 *  Copyright 2015 SmartThings
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
	}
    section("Second Schedule") {
    	input "time2", "time", title: "What time?", required: false
        input "temperature2", "number", title: "Color Temperature", range: "2700..5000", required: false
        input "level2", "number", title: "Level (1-100%)", range: "1..100", required: false
	}
}

def installed() {
	schedule(time1, schedule1)
    schedule(time2, schedule2)
    subscribe(ctbulbs, "switch.on", switchOnHandler)
    state.nextLevel = [:]
    state.nextColor = [:]
}

def updated() {
	schedule(time1, schedule1)
    schedule(time2, schedule2)
    subscribe(ctbulbs, "switch.on", switchOnHandler)
}

def schedule1() {
	setLighting(1, temperature1, level1);
}

def schedule2() {
	setLighting(2, temperature2, level2);
}

def setLighting(number, temperature, level) {
	log.debug("Running schedule $number. Temp: $temperature Level $level")
	for(ctbulb in ctbulbs) { 		
        if(ctbulb.currentValue("switch") == "on") {
        	ctbulb.setLevel(level);
            ctbulb.setColorTemperature(temperature)
            log.info "Setting bulb to level ${level} and temperature ${temperature}"
        } else {
        	state.nextLevel.put(ctbulb.id, level)
            state.nextColor.put(ctbulb.id, temperature)
        	log.info "Bulb is off, queue level: ${level} temperature ${temperature} for next time bulb turns on"
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