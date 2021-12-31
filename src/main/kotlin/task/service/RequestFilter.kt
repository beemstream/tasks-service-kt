package task.service

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@Component
class RequestFilter : Filter {
  @Throws(IOException::class, ServletException::class)
  override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse?, filterChain: FilterChain) {
    val uniqueId = UUID.randomUUID().toString()
    MDC.put("requestId", uniqueId)
    val httpServletResponse = servletResponse as HttpServletResponse?
    val responseWrapper = ContentCachingResponseWrapper(
      httpServletResponse!!
    )
    filterChain.doFilter(servletRequest, responseWrapper)
    responseWrapper.setHeader("requestId", uniqueId)
    responseWrapper.copyBodyToResponse()
  }
}