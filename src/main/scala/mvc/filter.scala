package scalaeye.mvc

import javax.servlet.{ Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

class WebFilter extends Filter {

	def init(filterConfig: FilterConfig) {}

	def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		println("Hello, filter")
	}

	def destroy() {}
}

