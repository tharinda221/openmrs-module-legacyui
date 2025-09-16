package org.openmrs.module.legacyui.security;

import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CsrfTokenUtilTest {

	@Test
	public void ensureToken_createsAndStoresToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.getSession();
		
		CsrfTokenUtil.ensureToken(request, response);
		
		String token = CsrfTokenUtil.getToken(request);
		assertNotNull(token);
		assertEquals(token, request.getSession().getAttribute("_csrf_token"));
		
		Cookie cookie = response.getCookie("XSRF-TOKEN");
		assertNotNull(cookie);
		assertEquals(token, cookie.getValue());
	}

	@Test
	public void isValid_true_withHeader() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.getSession();
		
		CsrfTokenUtil.ensureToken(request, response);
		String token = CsrfTokenUtil.getToken(request);
		request.addHeader("X-CSRF-Token", token);
		
		assertTrue(CsrfTokenUtil.isValidToken(request));
	}

	@Test
	public void isValid_true_withParam() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.getSession();
		
		CsrfTokenUtil.ensureToken(request, response);
		String token = CsrfTokenUtil.getToken(request);
		request.setParameter("_csrf", token);
		
		assertTrue(CsrfTokenUtil.isValidToken(request));
	}

	@Test
	public void isValid_false_whenMissingOrWrong() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.getSession();
		
		CsrfTokenUtil.ensureToken(request, response);
		
		assertFalse(CsrfTokenUtil.isValidToken(request));
		
		request.addHeader("X-CSRF-Token", "wrong-token");
		assertFalse(CsrfTokenUtil.isValidToken(request));
	}
}