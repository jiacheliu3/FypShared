import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
 
class UsageTrackingFilters {
 
	private static final Log LOG = LogFactory.getLog('usagetracking')
 
	def filters = {
		all(controller:'*', action:'*') {
			before = {
				LOG.info("Access to $controllerName/$actionName")
			}
			after = {
			}
			afterView = {
			}
		}
	}
}

