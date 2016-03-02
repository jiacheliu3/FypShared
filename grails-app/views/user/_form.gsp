<%@ page import="codebigbrosub.User" %>



<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="user.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${userInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'phone', 'error')} ">
	<label for="phone">
		<g:message code="user.phone.label" default="Phone" />
		
	</label>
	<g:textField name="phone" value="${userInstance?.phone}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email', 'error')} ">
	<label for="email">
		<g:message code="user.email.label" default="Email" />
		
	</label>
	<g:textField name="email" value="${userInstance?.email}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'weiboId', 'error')} required">
	<label for="weiboId">
		<g:message code="user.weiboId.label" default="Weibo Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="weiboId" required="" value="${userInstance?.weiboId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'weiboName', 'error')} required">
	<label for="weiboName">
		<g:message code="user.weiboName.label" default="Weibo Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="weiboName" required="" value="${userInstance?.weiboName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'url', 'error')} ">
	<label for="url">
		<g:message code="user.url.label" default="Url" />
		
	</label>
	<g:textField name="url" value="${userInstance?.url}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'faceUrl', 'error')} ">
	<label for="faceUrl">
		<g:message code="user.faceUrl.label" default="Face Url" />
		
	</label>
	<g:textField name="faceUrl" value="${userInstance?.faceUrl}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'terminals', 'error')} ">
	<label for="terminals">
		<g:message code="user.terminals.label" default="Terminals" />
		
	</label>
	

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'tags', 'error')} ">
	<label for="tags">
		<g:message code="user.tags.label" default="Tags" />
		
	</label>
	

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'keywords', 'error')} ">
	<label for="keywords">
		<g:message code="user.keywords.label" default="Keywords" />
		
	</label>
	

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'weibos', 'error')} ">
	<label for="weibos">
		<g:message code="user.weibos.label" default="Weibos" />
		
	</label>
	<g:select name="weibos" from="${codebigbrosub.Weibo.list()}" multiple="multiple" optionKey="id" size="5" value="${userInstance?.weibos*.id}" class="many-to-many"/>

</div>

