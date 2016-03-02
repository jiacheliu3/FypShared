
<%@ page import="codebigbrosub.Weibo" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'weibo.label', default: 'Weibo')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-weibo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
			</ul>
		</div>
		<div id="show-weibo" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list weibo">
			
				<g:if test="${weiboInstance?.weiboId}">
				<li class="fieldcontain">
					<span id="weiboId-label" class="property-label"><g:message code="weibo.weiboId.label" default="Weibo Id" /></span>
					
						<span class="property-value" aria-labelledby="weiboId-label"><g:fieldValue bean="${weiboInstance}" field="weiboId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.subId}">
				<li class="fieldcontain">
					<span id="subId-label" class="property-label"><g:message code="weibo.subId.label" default="Sub Id" /></span>
					
						<span class="property-value" aria-labelledby="subId-label"><g:fieldValue bean="${weiboInstance}" field="subId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.ownerName}">
				<li class="fieldcontain">
					<span id="ownerName-label" class="property-label"><g:message code="weibo.ownerName.label" default="Owner Name" /></span>
					
						<span class="property-value" aria-labelledby="ownerName-label"><g:fieldValue bean="${weiboInstance}" field="ownerName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.createdTime}">
				<li class="fieldcontain">
					<span id="createdTime-label" class="property-label"><g:message code="weibo.createdTime.label" default="Created Time" /></span>
					
						<span class="property-value" aria-labelledby="createdTime-label"><g:formatDate date="${weiboInstance?.createdTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.content}">
				<li class="fieldcontain">
					<span id="content-label" class="property-label"><g:message code="weibo.content.label" default="Content" /></span>
					
						<span class="property-value" aria-labelledby="content-label"><g:fieldValue bean="${weiboInstance}" field="content"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="weibo.url.label" default="Url" /></span>
					
						<span class="property-value" aria-labelledby="url-label"><g:fieldValue bean="${weiboInstance}" field="url"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.imageUrl}">
				<li class="fieldcontain">
					<span id="imageUrl-label" class="property-label"><g:message code="weibo.imageUrl.label" default="Image Url" /></span>
					
						<span class="property-value" aria-labelledby="imageUrl-label"><g:fieldValue bean="${weiboInstance}" field="imageUrl"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.tag}">
				<li class="fieldcontain">
					<span id="tag-label" class="property-label"><g:message code="weibo.tag.label" default="Tag" /></span>
					
						<span class="property-value" aria-labelledby="tag-label"><g:link controller="tag" action="show" id="${weiboInstance?.tag?.id}">${weiboInstance?.tag?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.terminal}">
				<li class="fieldcontain">
					<span id="terminal-label" class="property-label"><g:message code="weibo.terminal.label" default="Terminal" /></span>
					
						<span class="property-value" aria-labelledby="terminal-label"><g:fieldValue bean="${weiboInstance}" field="terminal"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.orgOwnerName}">
				<li class="fieldcontain">
					<span id="orgOwnerName-label" class="property-label"><g:message code="weibo.orgOwnerName.label" default="Org Owner Name" /></span>
					
						<span class="property-value" aria-labelledby="orgOwnerName-label"><g:fieldValue bean="${weiboInstance}" field="orgOwnerName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.orgContent}">
				<li class="fieldcontain">
					<span id="orgContent-label" class="property-label"><g:message code="weibo.orgContent.label" default="Org Content" /></span>
					
						<span class="property-value" aria-labelledby="orgContent-label"><g:fieldValue bean="${weiboInstance}" field="orgContent"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.commentCount}">
				<li class="fieldcontain">
					<span id="commentCount-label" class="property-label"><g:message code="weibo.commentCount.label" default="Comment Count" /></span>
					
						<span class="property-value" aria-labelledby="commentCount-label"><g:fieldValue bean="${weiboInstance}" field="commentCount"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.forwardCount}">
				<li class="fieldcontain">
					<span id="forwardCount-label" class="property-label"><g:message code="weibo.forwardCount.label" default="Forward Count" /></span>
					
						<span class="property-value" aria-labelledby="forwardCount-label"><g:fieldValue bean="${weiboInstance}" field="forwardCount"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.isForwarded}">
				<li class="fieldcontain">
					<span id="isForwarded-label" class="property-label"><g:message code="weibo.isForwarded.label" default="Is Forwarded" /></span>
					
						<span class="property-value" aria-labelledby="isForwarded-label"><g:formatBoolean boolean="${weiboInstance?.isForwarded}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${weiboInstance?.links}">
				<li class="fieldcontain">
					<span id="links-label" class="property-label"><g:message code="weibo.links.label" default="Links" /></span>
					
						<span class="property-value" aria-labelledby="links-label"><g:fieldValue bean="${weiboInstance}" field="links"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:weiboInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${weiboInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
