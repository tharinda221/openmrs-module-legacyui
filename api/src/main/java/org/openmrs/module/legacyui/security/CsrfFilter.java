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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that provides CSRF protection for non-safe HTTP methods
 */
public class CsrfFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// No initialization needed
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// Always ensure token exists for authenticated sessions
		CsrfTokenUtil.ensureToken(httpRequest, httpResponse);
		
		String method = httpRequest.getMethod();
		
		// Only validate CSRF token for non-safe methods
		if (!"GET".equals(method) && !"HEAD".equals(method) && !"OPTIONS".equals(method)) {
			if (!CsrfTokenUtil.isValidToken(httpRequest)) {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token validation failed");
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
		// No cleanup needed
	}
}