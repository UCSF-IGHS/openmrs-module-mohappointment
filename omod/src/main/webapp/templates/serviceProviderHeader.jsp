<h2><spring:message code="mohappointment.appointment"/></h2>
<ul id="menu">
		<li class="first">
			<a href="<openmrs:contextPath/>/admin/index.htm"><spring:message code="admin.title.short"/></a>
		</li>
		
		<li class="<c:if test='<%= request.getRequestURI().contains("serviceProviderList") || request.getRequestURI().contains("serviceProviderForm")%>'> active</c:if>">
			<a href="serviceProvider.list"><spring:message code="mohappointment.appointment.service.provider.manage"/></a>
		</li>
		
		<li class="<c:if test='<%= request.getRequestURI().contains("serviceList") || request.getRequestURI().contains("serviceForm")%>'> active</c:if>">
			<a href="service.list"><spring:message code="mohappointment.appointment.service.manage"/></a>
		</li>
	
</ul>