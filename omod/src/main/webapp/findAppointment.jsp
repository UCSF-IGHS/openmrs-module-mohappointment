<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Search Appointments" otherwise="/login.htm" redirect="/module/mohappointment/findAppointment.form"/>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/jquery-1.3.2.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/jquery.bigframe.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.core.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.dialog.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.draggable.js" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/scripts/ui/ui.resizable.js" />

<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/ui.all.css" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/demo.css" />

<h2><spring:message code="mohappointment.appointment.find"/></h2>

<script src='<%= request.getContextPath()%>/dwr/interface/MOH_Appointment_DWRUtil.js'></script>

<script type="text/javascript">

	function patientListInTable(item,id){
			if (item.value != null && item.value.length > 2){
				MOH_Appointment_DWRUtil.getPatientListInTable(item.value,id, function(ret){

					var box = document.getElementById("resultOfSearch");
					box.innerHTML = ret;
				});
			}
		 }

	function personValues(personId,personName,id){
		alert("These are values sent : "+personId+" "+personName+" "+id);
	}

	function initializeAppointment(appointmentId,ptName,provName,appDate,reason,bt){
		$app("#form_changeAppointmentState").html("");
		if(bt==1){
			$app("#form_changeAppointmentState").html("<table>"
					+"<tr><td></td><td><input type='hidden' name='appointmentId' value='"+appointmentId+"'/></td></tr>"
					+"<tr><td>Patient Name</td><td> : <b>"+ptName+"</b></td></tr>"
					+"<tr><td>Appointment Date</td><td> : <b>"+appDate+"</b></td></tr>"
					+"<tr><td>Provider</td><td> : <b>"+provName+"</b></td></tr>"
					+"<tr><td>Reason</td><td> : <b>"+reason+"</b></td></tr>"
					+"<tr><td>Appointment State</td><td><select name='appointmentState'>"
					+"<option value='4'>WAITING</option>"
					+"<option value='5'>INADVANCE</option>"
					+"</select></td></tr>"
					+"<tr><td></td><td><input type='submit' value='Save'/></td></tr>"
				+"</table>");
		}if(bt==2){
			$app("#form_changeAppointmentState").html("<table>"
					+"<tr><td></td><td><input type='hidden' name='appointmentId' value='"+appointmentId+"'/></td></tr>"
					+"<tr><td>Patient Name</td><td> : <b>"+ptName+"</b></td></tr>"
					+"<tr><td>Appointment Date</td><td> : <b>"+appDate+"</b></td></tr>"
					+"<tr><td>Provider</td><td> : <b>"+provName+"</b></td></tr>"
					+"<tr><td>Service</td><td>"
					+"<select name='service' id='service'>"
					+"<option value='0'>---</option>"
					+"<c:forEach var='service' items='${services}'>"
					+"<option value='${service.concept.conceptId}'>"
					+"${service.name}</option>"
					+"</c:forEach>"
					+"</select></td></tr>"
					+"<tr><td></td><td><input type='submit' value='Save'/></td></tr>"
				+"</table>");
		}if(bt==3){
			$app("#form_changeAppointmentState").html("<table>"
					+"<tr><td><input type='hidden' name='appointmentId' value='"+appointmentId+"'/></td><td><input type='hidden' name='appointmentState' value='8'/></td></tr>"
					+"<tr><td>Patient Name</td><td> : <b>"+ptName+"</b></td></tr>"
					+"<tr><td>Appointment Date</td><td> : <b>"+appDate+"</b></td></tr>"
					+"<tr><td>Provider</td><td> : <b>"+provName+"</b></td></tr>"
					+"<tr><td>Reason</td><td> : <b>"+reason+"</b></td></tr>"
					+"<tr><td>Postponed to</td><td>   <input type='text' name='postponedDate' autocomplete='off' size='11' onclick='showCalendar(this);'/></td></tr>"
					+"<tr><td></td><td><input type='submit' value='Save'/></td></tr>"
				+"</table>");
		}
	}

	function showDialog(appointmentId,ptName,provName,appDate,reason,bt){
		initializeAppointment(appointmentId,ptName,provName,appDate,reason,bt);
		$app("#divDlg").html("<div id='dialog' style='font-size: 0.9em;' title='<spring:message code='mohappointment.appointment.change'/>'><p><div id='result'>"+$app('#dlgCtnt').html()+"</div></p></div>");
		$app("#dialog").dialog({
			zIndex: 980,
			bgiframe: true,
			height: 200,
			width: 530,
			modal: true
		});
	}
</script>

<b class="boxHeader"><spring:message code="mohappointment.appointment.find"/></b>
<div class="box">
	<table>
		<tr>
			<td><spring:message code="mohappointment.general.patient.identifier"/></td>
			<td><input type="text" style="width:25em" autocomplete="off" value="" onKeyUp='javascript:patientListInTable(this,1);' name='n_1' id='n_1'/></td>
		</tr>
	</table>

	<div id='resultOfSearch' style="background: whitesmoke; max-height: 400px; font-size:1em;"></div>

</div>

<div id="divDlg"></div>
<div id="dlgCtnt" style="display: none;">
	<form action="findAppointment.form?savechanges=true" method="post">

		<div id="form_changeAppointmentState"></div>

	</form>
</div>

<script type="text/javascript">
	var $app = jQuery.noConflict();

	$app(document).ready(function(){
		$app("#n_1").focus();
	});
</script>


<%@ include file="/WEB-INF/template/footer.jsp"%>