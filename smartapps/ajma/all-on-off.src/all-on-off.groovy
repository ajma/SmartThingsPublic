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
    section("Group 1") {
        input "switches", "capability.switch", title: "Which Switches?", multiple:true, required: false
        input "onButton", "capability.momentary", title: "Button for all on", required: false
        input "offButton", "capability.momentary", title: "Button for all off", required: false
    }
    section("Group 2") {
        input "switches2", "capability.switch", title: "Which Switches?", multiple:true, required: false
        input "onButton2", "capability.momentary", title: "Button for all on", required: false
        input "offButton2", "capability.momentary", title: "Button for all off", required: false
    }

}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
    subscribe(onButton, "momentary.pushed", onButtonHandler)
    subscribe(offButton, "momentary.pushed", offButtonHandler)
    subscribe(onButton2, "momentary.pushed", onButtonHandler2)
    subscribe(offButton2, "momentary.pushed", offButtonHandler2)
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

def onButtonHandler2(evt) {
    for(swit in switches2) {
        swit.on()
    }
}

def offButtonHandler2(evt) {
    for(swit in switches2) {
        swit.off()
    }
}