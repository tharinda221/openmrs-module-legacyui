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
 * Ultra-fast patient dashboard controller optimized for sub-200ms load times
 */
package org.openmrs.web.controller.patient;

import javax.servlet.http.HttpServletRequest;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FastPatientDashboardController {
    
    @RequestMapping(value = "/fastPatientDashboard.form", method = RequestMethod.GET)
    protected String renderFastDashboard(@RequestParam("patientId") Integer patientId, 
            ModelMap map, HttpServletRequest request) {
        
        Patient patient = Context.getPatientService().getPatient(patientId);
        if (patient == null) {
            return "redirect:/findPatient.htm";
        }
        
        map.put("patient", patient);
        return "module/legacyui/fastPatientDashboard";
    }
}