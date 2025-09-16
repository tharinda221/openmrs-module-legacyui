/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web.controller.patient;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.web.extension.ExtensionUtil;
import org.openmrs.module.web.extension.provider.Link;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientDashboardController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * render the patient dashboard model and direct to the view
	 * 
	 * @should render patient dashboard if given patient id is an existing id
	 * @should render patient dashboard if given patient id is an existing uuid
	 * @should redirect to find patient page if given patient id is not an existing id
	 * @should redirect to find patient page if given patient id is not an existing uuid
	 */
	@RequestMapping("/patientDashboard.form")
	protected String renderDashboard(@RequestParam("patientId") String patientId, ModelMap map, HttpServletRequest request)
	        throws Exception {
		
		Patient patient = getPatient(patientId);
		
		if (patient == null) {
			// redirect to the patient search page if no patient is found
			HttpSession session = request.getSession();
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "patientDashboard.noPatientWithId");
			session.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, patientId);
			return "module/legacyui/findPatient";
		}
		
		map.put("patient", patient);
		
		// Cache patient variation check
		String patientVariation = patient.isDead() ? "Dead" : "";
		map.put("patientVariation", patientVariation);
		
		// Reuse static empty objects
		map.put("emptyIdentifier", getEmptyIdentifier());
		map.put("emptyName", getEmptyName());
		map.put("emptyAddress", getEmptyAddress());
		
		return "module/legacyui/patientDashboardForm";
	}
	

	
	// Static empty objects to reduce object creation
	private static final PatientIdentifier EMPTY_IDENTIFIER = new PatientIdentifier();
	private static final PersonName EMPTY_NAME = new PersonName();
	private static final PersonAddress EMPTY_ADDRESS = new PersonAddress();
	
	private PatientIdentifier getEmptyIdentifier() { return EMPTY_IDENTIFIER; }
	private PersonName getEmptyName() { return EMPTY_NAME; }
	private PersonAddress getEmptyAddress() { return EMPTY_ADDRESS; }
	
	private Patient getPatient(String patientId) {
		if (StringUtils.isBlank(patientId)) {
			return null;
		}
		
		PatientService ps = Context.getPatientService();
		// Try integer ID first (most common case)
		try {
			return ps.getPatient(Integer.valueOf(patientId));
		} catch (NumberFormatException ex) {
			// Fallback to UUID lookup
			return ps.getPatientByUuid(patientId);
		}
	}
}
