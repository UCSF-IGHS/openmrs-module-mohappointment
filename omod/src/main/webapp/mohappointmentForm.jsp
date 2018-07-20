<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<openmrs:htmlInclude file="/moduleResources/mohappointment/style/listing.css" />
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />


<script type="text/javascript">
	var $app = jQuery.noConflict();

	$app(document).ready(function(){

		$app("#providers").hide();

		if($app("#displayAppointments").val()=="displayAppointments"){
			$app("#displayAppointments").show();
		}else{
			//$app("#displayAppointments").hide();
		}

		$app("#clinicalareatosee").change(function(){

			if($app("span#patient_selection span input[type=hidden]").val() != ""){
    			$app('#displayAppointments').show();

    			// I want to submit the form: Patient_ID + Service_ID
				document.getElementById("add_appointment_form").submit();
			}
    	 });

		$app("#reasonofappointment").change(function(){
			var reason = $app("#reasonofappointment option:selected").text() + " : ";
			$app("#note").val(reason);
			$app("#note").focus();
		});

		if($app("#displayProviders").val()=="displayProviders"){
			$app("#providers").show();
		}else{
			$app("#providers").hide();
		}
	});
</script>

<h2>
	<spring:message code="mohappointment.title" />
</h2>
<br/>
<!-- ADD A NEW APPOINTMENT -->

<form action="addAppointment.form" method="post" id="add_appointment_form">
	<input type="hidden" name="displayProviders" value="${displayProviders}" />
	<input type="hidden" name="providerId" value="${providerId}"/>

	<b class="boxHeader"><spring:message code="mohappointment.appointment.add"/></b>
	<div class="searchParameterBox box">
		<div style="float: left; width: 50;">
			<table>
				<tr>
					<td><b><spring:message code="mohappointment.general.patient"/></b></td>
					<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
					<td><openmrs_tag:patientField formFieldName="patient" initialValue="${param.patient}" /></td>
				</tr>
				<tr>
				<tr>
					<td><b><spring:message code="mohappointment.general.clinicalareatosee"/></b></td>
					<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
					<td><select id="clinicalareatosee" name="clinicalareatosee">
							<option value="">--</option>
							<c:forEach items="${areasToSee}" var="area">
								<option value="${area.serviceId}" <c:if test="${area.serviceId==param.clinicalareatosee}">selected='selected'</c:if>>${area.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td><b><spring:message code="mohappointment.general.appointmentdate"/></b></td>
					<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
					<td><input id="appointmentDate" value="${appointmentDate}" type="text" name="appointmentDate" size="11" onclick="showCalendar(this);"/></td>
				</tr>
				<tr>
					<td><b><spring:message code="mohappointment.general.reasonofappointment"/></b></td>
					<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
					<td><select id="reasonofappointment" name="reasonofappointment">
							<option value="">--</option>
							<c:forEach items="${reasonForAppointmentOptions}" var="appointmentReason">
								<option value="${appointmentReason.key}" <c:if test="${appointmentReason.key==param.reasonofappointment}">selected='selected'</c:if>>${appointmentReason.value}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td><b><spring:message code="mohappointment.general.note"/></b></td>
					<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
					<td><textarea id="note" rows="3" cols="30" name="note">${note}</textarea></td>
				</tr>
			</table>
		</div>

		<div id="providers" style="float: right; width: 35%;">
			<table>
				<tr>
					<th class="columnHeader"><spring:message code="mohappointment.general.provider"/></th>
					<th class="columnHeader"><spring:message code="mohappointment.appointment.chosen"/></th>
				</tr>
				<c:forEach items="${providers}" var="provider" varStatus="status">
					<tr>
						<td>${provider.personName}</td>
						<td><a href="addAppointment.form?providerId=${provider.personId}"/>This one</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>

		<div style="clear: both;"></div>

		<div class="divBox">
			<input type="submit" name="saveAppointment" value="<spring:message code='mohappointment.general.save'/>"/>
		</div>
	</div>

</form>

<!-- END of ADD A NEW APPOINTMENT -->

<br />
<div class="searchParameterBox box" id="displayAppointments">
	<div class="list_container" style="width: 99%">
		<div class="list_title">
			<div class="list_title_msg"><spring:message code="mohappointment.appointment.patient"/></div>
			<div class="list_title_bts">

				<!-- <form style="display: inline;" action="#" method="post">
					<input type="submit" class="list_exportBt" value="<spring:message code="mohappointment.general.export"/>"/>
				</form> -->

			</div>
			<div style="clear:both;"></div>
		</div>
		<table class="list_data">
			<tr>
				<th class="columnHeader"><spring:message code="mohappointment.general.appointmentdate"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.number"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.provider"/></th>
				<!-- <th class="columnHeader"><spring:message code="mohappointment.general.reasonofappointment"/></th> -->
				<th class="columnHeader"><spring:message code="mohappointment.general.clinicalareatosee"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.location"/></th>
				<th class="columnHeader"><spring:message code="mohappointment.general.state"/></th>
				<th class="columnHeader"></th>
			</tr>
			<c:if test="${empty appointments}">
				<tr>
					<td colspan="7" style="text-align: center;"><spring:message code="mohappointment.general.empty"/></td>
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
					<td class="rowValue ${status.count%2!=0?'even':''}">${appointment.provider.personName}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${appointment.service.concept.name.name}</td>
					<!-- <td class="rowValue ${status.count%2!=0?'even':''}">-</td> -->
					<td class="rowValue ${status.count%2!=0?'even':''}">${appointment.location}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${appointment.appointmentState.description}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">
					<a href="<openmrs:contextPath/>/patientDashboard.form?patientId=${appointment.patient.patientId}&attended=true&appointmentId=${appointment.appointmentId}">
					<spring:message code="mohappointment.general.attended"/></a> &nbsp;
					<a href="<openmrs:contextPath/>/patientDashboard.form?patientId=${appointment.patient.patientId}&cancel=true&appointmentId=${appointment.appointmentId}"><spring:message code="mohappointment.general.cancel"/></a></td>
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

<%@ include file="/WEB-INF/template/footer.jsp"%>