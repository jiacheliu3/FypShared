<%@ page import="codebigbrosub.Weibo" %>



<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'weiboId', 'error')} required">
	<label for="weiboId">
		<g:message code="weibo.weiboId.label" default="Weibo Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="weiboId" required="" value="${weiboInstance?.weiboId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'subId', 'error')} ">
	<label for="subId">
		<g:message code="weibo.subId.label" default="Sub Id" />
		
	</label>
	<g:textField name="subId" value="${weiboInstance?.subId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'ownerName', 'error')} required">
	<label for="ownerName">
		<g:message code="weibo.ownerName.label" default="Owner Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="ownerName" required="" value="${weiboInstance?.ownerName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'createdTime', 'error')} ">
	<label for="createdTime">
		<g:message code="weibo.createdTime.label" default="Created Time" />
		
	</label>
	<g:datePicker name="createdTime" precision="day"  value="${weiboInstance?.createdTime}" default="none" noSelection="['': '']" />

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'content', 'error')} ">
	<label for="content">
		<g:message code="weibo.content.label" default="Content" />
		
	</label>
	<g:textField name="content" value="${weiboInstance?.content}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'url', 'error')} ">
	<label for="url">
		<g:message code="weibo.url.label" default="Url" />
		
	</label>
	<g:textField name="url" value="${weiboInstance?.url}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'imageUrl', 'error')} ">
	<label for="imageUrl">
		<g:message code="weibo.imageUrl.label" default="Image Url" />
		
	</label>
	<g:textField name="imageUrl" value="${weiboInstance?.imageUrl}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'tag', 'error')} ">
	<label for="tag">
		<g:message code="weibo.tag.label" default="Tag" />
		
	</label>
	<g:select id="tag" name="tag.id" from="${codebigbrosub.Tag.list()}" optionKey="id" value="${weiboInstance?.tag?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'terminal', 'error')} ">
	<label for="terminal">
		<g:message code="weibo.terminal.label" default="Terminal" />
		
	</label>
	<g:textField name="terminal" value="${weiboInstance?.terminal}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'orgOwnerName', 'error')} ">
	<label for="orgOwnerName">
		<g:message code="weibo.orgOwnerName.label" default="Org Owner Name" />
		
	</label>
	<g:textField name="orgOwnerName" value="${weiboInstance?.orgOwnerName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'orgContent', 'error')} ">
	<label for="orgContent">
		<g:message code="weibo.orgContent.label" default="Org Content" />
		
	</label>
	<g:textField name="orgContent" value="${weiboInstance?.orgContent}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'commentCount', 'error')} required">
	<label for="commentCount">
		<g:message code="weibo.commentCount.label" default="Comment Count" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="commentCount" type="number" value="${weiboInstance.commentCount}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'forwardCount', 'error')} required">
	<label for="forwardCount">
		<g:message code="weibo.forwardCount.label" default="Forward Count" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="forwardCount" type="number" value="${weiboInstance.forwardCount}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'isForwarded', 'error')} ">
	<label for="isForwarded">
		<g:message code="weibo.isForwarded.label" default="Is Forwarded" />
		
	</label>
	<g:checkBox name="isForwarded" value="${weiboInstance?.isForwarded}" />

</div>

<div class="fieldcontain ${hasErrors(bean: weiboInstance, field: 'links', 'error')} ">
	<label for="links">
		<g:message code="weibo.links.label" default="Links" />
		
	</label>
	

</div>

