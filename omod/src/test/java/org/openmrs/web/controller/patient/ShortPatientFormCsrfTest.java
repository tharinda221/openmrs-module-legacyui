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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.legacyui.security.CsrfTokenUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests CSRF protection for ShortPatientForm endpoint
 */
public class ShortPatientFormCsrfTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void postWithoutToken_shouldReturn403() throws Exception {
		mockMvc.perform(post("/admin/patients/shortPatientForm.form")
				.param("patientId", "2")
				.param("personName.givenName", "Test")
				.param("personName.familyName", "Patient"))
				.andExpect(status().isForbidden());
	}
	
	@Test
	public void postWithValidToken_shouldSucceed() throws Exception {
		// First GET to seed session/cookie
		MvcResult getResult = mockMvc.perform(get("/admin/patients/shortPatientForm.form")
				.param("patientId", "2"))
				.andExpect(status().isOk())
				.andReturn();
		
		HttpSession session = getResult.getRequest().getSession();
		String token = (String) session.getAttribute("_csrf_token");
		
		// POST with valid token in header
		mockMvc.perform(post("/admin/patients/shortPatientForm.form")
				.session((org.springframework.mock.web.MockHttpSession) session)
				.header("X-CSRF-Token", token)
				.param("patientId", "2")
				.param("personName.givenName", "Test")
				.param("personName.familyName", "Patient"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void postWithValidTokenInFormField_shouldSucceed() throws Exception {
		// Generate token manually for form field test
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.getSession(true);
		
		CsrfTokenUtil.ensureToken(request, response);
		String token = CsrfTokenUtil.getToken(request);
		
		// POST with valid token in form field
		mockMvc.perform(post("/admin/patients/shortPatientForm.form")
				.session((org.springframework.mock.web.MockHttpSession) request.getSession())
				.param("_csrf", token)
				.param("patientId", "2")
				.param("personName.givenName", "Test")
				.param("personName.familyName", "Patient"))
				.andExpect(status().isOk());
	}
}