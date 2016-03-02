import org.springframework.web.context.request.RequestContextHolder
import org.slf4j.MDC
 
class LoggingFilters {
	def filters = {
		all(controller:'*', action:'*') {
			before = {
				String sessionId = RequestContextHolder.getRequestAttributes()?.getSessionId()
				if(sessionId){
					MDC.put('sessionId', sessionId)
				}
			}
			after = {
			}
			afterView = {
				MDC.remove('sessionId')
			}
		}
	}
}