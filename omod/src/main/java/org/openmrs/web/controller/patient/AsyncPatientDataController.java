/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 * Async controller for loading patient data on demand
 */
package org.openmrs.web.controller.patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AsyncPatientDataController {
    
    @RequestMapping("/async/patientCauseOfDeath.json")
    @ResponseBody
    public Map<String, String> getCauseOfDeath(@RequestParam("patientId") Integer patientId) {
        Map<String, String> result = new HashMap<>();
        
        Patient patient = Context.getPatientService().getPatient(patientId);
        if (patient != null && Context.isAuthenticated()) {
            String propCause = Context.getAdministrationService().getGlobalProperty("concept.causeOfDeath");
            Concept conceptCause = Context.getConceptService().getConcept(propCause);
            
            if (conceptCause != null) {
                List<Obs> obssDeath = Context.getObsService().getObservationsByPersonAndConcept(patient, conceptCause);
                if (obssDeath.size() == 1) {
                    Obs obsDeath = obssDeath.iterator().next();
                    String causeOfDeathOther = obsDeath.getValueText();
                    result.put("causeOfDeathOther", causeOfDeathOther != null ? causeOfDeathOther : "");
                }
            }
        }
        
        return result;
    }
    
    @RequestMapping("/async/patientExitReason.json")
    @ResponseBody
    public Map<String, Object> getExitReason(@RequestParam("patientId") Integer patientId) {
        Map<String, Object> result = new HashMap<>();
        
        Patient patient = Context.getPatientService().getPatient(patientId);
        if (patient != null) {
            Concept reasonForExitConcept = Context.getConceptService().getConcept(
                Context.getAdministrationService().getGlobalProperty("concept.reasonExitedCare"));
            
            if (reasonForExitConcept != null) {
                List<Obs> patientExitObs = Context.getObsService().getObservationsByPersonAndConcept(patient, reasonForExitConcept);
                if (patientExitObs != null && patientExitObs.size() == 1) {
                    Obs exitObs = patientExitObs.iterator().next();
                    result.put("exitReason", exitObs.getValueCoded());
                    result.put("exitDate", exitObs.getObsDatetime());
                }
            }
        }
        
        return result;
    }
}