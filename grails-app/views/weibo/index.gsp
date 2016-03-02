
<%@ page import="codebigbrosub.Weibo" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'weibo.label', default: 'Weibo')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-weibo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-weibo" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="weiboId" title="${message(code: 'weibo.weiboId.label', default: 'Weibo Id')}" />
					
						<g:sortableColumn property="subId" title="${message(code: 'weibo.subId.label', default: 'Sub Id')}" />
					
						<g:sortableColumn property="ownerName" title="${message(code: 'weibo.ownerName.label', default: 'Owner Name')}" />
					
						<g:sortableColumn property="createdTime" title="${message(code: 'weibo.createdTime.label', default: 'Created Time')}" />
					
						<g:sortableColumn property="content" title="${message(code: 'weibo.content.label', default: 'Content')}" />
					
						<g:sortableColumn property="url" title="${message(code: 'weibo.url.label', default: 'Url')}" />
					
						<g:sortableColumn property="imageUrl" title="${message(code: 'weibo.imageUrl.label', default: 'Image Url')}" />
					
						<th><g:message code="weibo.tag.label" default="Tag" /></th>
					
						<g:sortableColumn property="terminal" title="${message(code: 'weibo.terminal.label', default: 'Terminal')}" />
					
						<g:sortableColumn property="orgOwnerName" title="${message(code: 'weibo.orgOwnerName.label', default: 'Org Owner Name')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${weiboInstanceList}" status="i" var="weiboInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${weiboInstance.id}">${fieldValue(bean: weiboInstance, field: "weiboId")}</g:link></td>
					
						<td>${fieldValue(bean: weiboInstance, field: "subId")}</td>
					
						<td>${fieldValue(bean: weiboInstance, field: "ownerName")}</td>
					
						<td><g:formatDate date="${weiboInstance.createdTime}" /></td>
					
						<td>${fieldValue(bean: weiboInstance, field: "content")}</td>
					
						<td>${fieldValue(bean: weiboInstance, field: "url")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${weiboInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
