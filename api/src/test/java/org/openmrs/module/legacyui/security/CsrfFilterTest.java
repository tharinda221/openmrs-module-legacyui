package org.openmrs.module.legacyui.security;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CsrfFilterTest {

	private static class TestFilterChain implements FilterChain {
		private boolean invoked = false;
		
		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			invoked = true;
		}
		
		public boolean wasInvoked() {
			return invoked;
		}
	}

	@Test
	public void getRequest_seedsToken_andPassesThrough() throws Exception {
		CsrfFilter filter = new CsrfFilter();
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		TestFilterChain chain = new TestFilterChain();
		request.getSession();
		
		filter.doFilter(request, response, chain);
		
		assertTrue(chain.wasInvoked());
		assertNotNull(CsrfTokenUtil.getToken(request));
		assertNotNull(response.getCookie("XSRF-TOKEN"));
	}

	@Test
	public void post_withoutToken_is403_andDoesNotInvokeChain() throws Exception {
		CsrfFilter filter = new CsrfFilter();
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		TestFilterChain chain = new TestFilterChain();
		request.getSession();
		
		filter.doFilter(request, response, chain);
		
		assertFalse(chain.wasInvoked());
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
	}

	@Test
	public void post_withValidHeaderToken_invokesChain() throws Exception {
		CsrfFilter filter = new CsrfFilter();
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		TestFilterChain chain = new TestFilterChain();
		request.getSession();
		
		CsrfTokenUtil.ensureToken(request, response);
		String token = CsrfTokenUtil.getToken(request);
		request.addHeader("X-CSRF-Token", token);
		
		filter.doFilter(request, response, chain);
		
		assertTrue(chain.wasInvoked());
	}
}