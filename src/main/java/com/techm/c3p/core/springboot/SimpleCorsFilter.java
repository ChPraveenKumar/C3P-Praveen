package com.techm.c3p.core.springboot;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class SimpleCorsFilter implements Filter {

	public SimpleCorsFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) res;
		final HttpServletRequest request = (HttpServletRequest) req;
		response.setHeader("Access-Control-Allow-Origin", "*");
		//
		// BufferedReader reader = request.getReader();
		// Gson gson = new Gson();
		// String username=null;
		// String password=null;
		//
		// UserPojo dto = gson.fromJson(reader,
		// UserPojo.class);
		//
		// if(null!=dto){
		// username = dto.getUsername();
		// password = dto.getPassword();
		// }
		//
		// request.getServletContext().getRealPath("/")+
		//
		// if(null==Global.loggedInUser){
		// response.sendRedirect("/");
		// }
		//
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
		// response.setHeader("Access-Control-Max-Age", "-1");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
		response.setHeader("Cache-Control", "no-store, must-revalidate");
		//
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(final FilterConfig filterConfig) {
	}
}
