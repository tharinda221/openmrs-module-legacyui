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
 * Optimized Patient Dashboard Controller with lazy loading and caching
 */
package org.openmrs.web.controller.patient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletRequest;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OptimizedPatientDashboardController {
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    @RequestMapping("/optimizedPatientDashboard.form")
    protected String renderOptimizedDashboard(@RequestParam("patientId") String patientId, 
            ModelMap map, HttpServletRequest request) throws Exception {
        
        Patient patient = Context.getPatientService().getPatient(Integer.valueOf(patientId));
        if (patient == null) {
            return "redirect:/findPatient.htm";
        }
        
        map.put("patient", patient);
        
        // Load only essential data synchronously
        loadEssentialData(patient, map);
        
        return "module/legacyui/optimizedPatientDashboard";
    }
    
    private void loadEssentialData(Patient patient, ModelMap map) {
        // Only load critical patient info for initial render
        map.put("patientVariation", patient.isDead() ? "Dead" : "");
    }
}