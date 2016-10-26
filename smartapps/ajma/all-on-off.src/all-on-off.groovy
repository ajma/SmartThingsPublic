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
 *  All on/off
 *
 *  Author: Andrew Ma
 */
definition(
    name: "All on/off",
    namespace: "ajma",
    author: "Andrew Ma",
    description: "Use two momentary buttons for all on/off.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png"
)

preferences {
    section("Control these lights...") {
        input "switches", "capability.switch", title: "Which Switches?", multiple:true, required: false
    }
    section("Momentary Buttons") {
        input "onButton", "capability.momentary", title: "Button for all on", required: true
        input "offButton", "capability.momentary", title: "Button for all off", required: true
    }
}

def installed() {
    initialize()
}

def initialize() {
    subscribe(onButton, "momentary.pushed", onButtonHandler)
    subscribe(offButton, "momentary.pushed", offButtonHandler)
}

def onButtonHandler(evt) {
    for(swit in switches) {
        swit.on()
    }
}

def offButtonHandler(evt) {
    for(swit in switches) {
        swit.off()
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