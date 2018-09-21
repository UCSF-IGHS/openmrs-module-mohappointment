<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Search Appointments" otherwise="/login.htm" redirect="/module/mohappointment/findAppointment.form"/>

<openmrs:htmlInclude file="/moduleResources/mohappointment/style/appointment.css" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/style/listing.css" />
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/jquery.bigframe.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.core.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.dialog.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.draggable.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.resizable.js" />

<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/ui.all.css" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/demo.css" />

<script type="text/javascript">
		var $j = jQuery.noConflict();
</script>

<h2 style="display: inline;"><spring:message code="mohappointment.appointment"/></h2> : <span class="boldTitle"><spring:message code="mohappointment.search.title"/></span>
<br/><br/>

<%@ include file="templates/searchParameters.jsp"%>

<div class="searchParameterBox box">
	<div class="list_container" style="width: 99%">
		<div class="list_title">
			<div class="list_title_msg"><spring:message code="mohappointment.search.result"/></div>
			<div class="list_title_bts">

				<form style="display: inline;" action="#" method="post">
					<input onclick="showExportDialog();" type="button" class="list_exportBt" value="<spring:message code="mohappointment.general.export"/>"/>
				</form>

			</div>
			<div style="clear:both;"></div>
		</div>
		<table class="list_data">
			<tr>
				<th class="columnHeader"><spring:message code="mohappointment.general.appointmentdate"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.number"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.identifier"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.names"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.provider"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.reasonofappointment"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.clinicalareatosee"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.location"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.state"/></th>
			</tr>
			<c:if test="${empty appointments}">
				<tr>
					<td colspan="9" style="text-align: center;"><spring:message code="mohappointment.general.empty"/></td>
				</tr>
			</c:if>
			<c:set value="0" var="index"/>
			<c:forEach items="${appointments}" var="appointment" varStatus="status">
				<tr>
					<c:choose>
					  <c:when test="${appointment.appointmentDate == currentDate}">
					   	<td class="rowValue" <c:if test="${index%2!=0}">style="background-color: whitesmoke;"</c:if>><c:if test="${appointment.appointmentDate!=currentDate}"><openmrs:formatDate date="${appointment.appointmentDate}" type="medium"/><c:set value="${appointment.appointmentDate}" var="currentDate"/></c:if></td>
					  </c:when>
					  <c:otherwise>
					  	<c:set value="${index+1}" var="index"/>
					   	<td class="rowValue" style="border-top: 1px solid cadetblue; <c:if test="${index%2!=0}">background-color: whitesmoke;</c:if>"><c:if test="${appointment.appointmentDate!=currentDate}"><openmrs:formatDate date="${appointment.appointmentDate}" type="medium"/><c:set value="${appointment.appointmentDate}" var="currentDate"/></c:if></td>
					  </c:otherwise>
					</c:choose>
					<td class="rowValue ${status.count%2!=0?'even':''}">${((param.page-1)*pageSize)+status.count}.</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${appointment.patient.patientIdentifier}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="<openmrs:contextPath/>/patientDashboard.form?patientId=${appointment.patient.patientId}">${appointment.patient.personName}</a></td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="#">${appointment.provider.personName}</a></td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="#">${appointment.reason.valueCoded.name}</a></td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="#">${appointment.service.name}</a></td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="#">${appointment.location}</a></td>
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="#">${appointment.appointmentState.description}</a></td>
				</tr>
			</c:forEach>
		</table>
		<div class="list_footer">
			<div class="list_footer_info">&nbsp;&nbsp;&nbsp;</div>
			<div class="list_footer_pages">
				&nbsp;&nbsp;&nbsp;
			</div>
			<div style="clear: both"></div>
		</div>
	</div>

</div>
<div id="divDlg"></div>
<div id="dlgCtnt" style="display: none;">
	<form action="advancedSearch.form?export=true${parameters}" method="post">

		<%@ include file="templates/exportForm.jsp"%>

	</form>
</div>

<script>

	function showExportDialog(){
		//distroyResultDiv();
		showDialog();
	}

	function showDialog(){
		$j("#divDlg").html("<div id='dialog' style='font-size: 0.9em;' title='<spring:message code='mohappointment.export.data'/>'><p><div id='result'>"+$j('#dlgCtnt').html()+"</div></p></div>");
		$j("#dialog").dialog({
			zIndex: 980,
			bgiframe: true,
			height: 220,
			width: 550,
			modal: true
		});
	}

	function distroyResultDiv(){
		//while(document.getElementById("dialog")){
			//var DIVtoRemove = document.getElementById("dialog");
			//DIVtoRemove.parentNode.removeChild(DIVtoRemove);
		//}
	}

	$j(document).ready(function(){
	});
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>