
<%@ page import="codebigbrosub.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
			</ul>
		</div>
		<div id="show-user" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list user">
			
				<g:if test="${userInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="user.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${userInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.phone}">
				<li class="fieldcontain">
					<span id="phone-label" class="property-label"><g:message code="user.phone.label" default="Phone" /></span>
					
						<span class="property-value" aria-labelledby="phone-label"><g:fieldValue bean="${userInstance}" field="phone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label"><g:message code="user.email.label" default="Email" /></span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${userInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.weiboId}">
				<li class="fieldcontain">
					<span id="weiboId-label" class="property-label"><g:message code="user.weiboId.label" default="Weibo Id" /></span>
					
						<span class="property-value" aria-labelledby="weiboId-label"><g:fieldValue bean="${userInstance}" field="weiboId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.weiboName}">
				<li class="fieldcontain">
					<span id="weiboName-label" class="property-label"><g:message code="user.weiboName.label" default="Weibo Name" /></span>
					
						<span class="property-value" aria-labelledby="weiboName-label"><g:fieldValue bean="${userInstance}" field="weiboName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="user.url.label" default="Url" /></span>
					
						<span class="property-value" aria-labelledby="url-label"><g:fieldValue bean="${userInstance}" field="url"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.faceUrl}">
				<li class="fieldcontain">
					<span id="faceUrl-label" class="property-label"><g:message code="user.faceUrl.label" default="Face Url" /></span>
					
						<span class="property-value" aria-labelledby="faceUrl-label"><g:fieldValue bean="${userInstance}" field="faceUrl"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.terminals}">
				<li class="fieldcontain">
					<span id="terminals-label" class="property-label"><g:message code="user.terminals.label" default="Terminals" /></span>
					
						<span class="property-value" aria-labelledby="terminals-label"><g:fieldValue bean="${userInstance}" field="terminals"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.tags}">
				<li class="fieldcontain">
					<span id="tags-label" class="property-label"><g:message code="user.tags.label" default="Tags" /></span>
					
						<span class="property-value" aria-labelledby="tags-label"><g:fieldValue bean="${userInstance}" field="tags"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.keywords}">
				<li class="fieldcontain">
					<span id="keywords-label" class="property-label"><g:message code="user.keywords.label" default="Keywords" /></span>
					
						<span class="property-value" aria-labelledby="keywords-label"><g:fieldValue bean="${userInstance}" field="keywords"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.weibos}">
				<li class="fieldcontain">
					<span id="weibos-label" class="property-label"><g:message code="user.weibos.label" default="Weibos" /></span>
					
						<g:each in="${userInstance.weibos}" var="w">
						<span class="property-value" aria-labelledby="weibos-label"><g:link controller="weibo" action="show" id="${w.id}">${w?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:userInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${userInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
