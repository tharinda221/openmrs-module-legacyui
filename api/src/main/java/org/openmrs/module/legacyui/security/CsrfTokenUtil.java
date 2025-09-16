/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.legacyui.security;

import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Utility for CSRF token generation and validation
 */
public class CsrfTokenUtil {
	
	private static final String SESSION_TOKEN_KEY = "_csrf_token";
	private static final String COOKIE_NAME = "XSRF-TOKEN";
	private static final String HEADER_NAME = "X-CSRF-Token";
	private static final String FORM_FIELD_NAME = "_csrf";
	private static final SecureRandom random = new SecureRandom();
	
	/**
	 * Ensures a CSRF token exists for the session and exposes it in a cookie
	 */
	public static void ensureToken(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		
		String token = (String) session.getAttribute(SESSION_TOKEN_KEY);
		if (token == null) {
			token = generateToken();
			session.setAttribute(SESSION_TOKEN_KEY, token);
		}
		
		// Set cookie for JavaScript access
		Cookie cookie = new Cookie(COOKIE_NAME, token);
		cookie.setPath(request.getContextPath());
		cookie.setHttpOnly(false); // Allow JavaScript access
		response.addCookie(cookie);
	}
	
	/**
	 * Validates the CSRF token from request header or form field
	 */
	public static boolean isValidToken(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}
		
		String sessionToken = (String) session.getAttribute(SESSION_TOKEN_KEY);
		if (sessionToken == null) {
			return false;
		}
		
		// Check header first, then form field
		String requestToken = request.getHeader(HEADER_NAME);
		if (requestToken == null) {
			requestToken = request.getParameter(FORM_FIELD_NAME);
		}
		
		return sessionToken.equals(requestToken);
	}
	
	/**
	 * Gets the current CSRF token for the session
	 */
	public static String getToken(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (String) session.getAttribute(SESSION_TOKEN_KEY);
	}
	
	private static String generateToken() {
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}
}