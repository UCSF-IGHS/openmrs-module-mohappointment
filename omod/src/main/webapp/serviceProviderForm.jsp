<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Manage Services and Providers" otherwise="/login.htm" redirect="/module/mohappointment/serviceProvider.form"/>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<%@ include file="templates/serviceProviderHeader.jsp"%>

<h2><spring:message code="mohappointment.appointment.service.provider.form"/></h2>
<br/>

<script type="text/javascript">
	var $app = jQuery.noConflict();
</script>

<!-- <b class="boxHeader"><spring:message code="mohappointment.appointment.service.provider.current"/></b> -->
<form action="serviceProvider.form?save=true" method="post" class="box">
	<div id="errorDiv"></div><br/>
	
	<input type="hidden" name="spId" value="${spId}"/>
	<table>
		<tr>
			<td><b><spring:message code="mohappointment.general.provider"/></b></td>
			<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
			<td><openmrs_tag:userField formFieldName="provider" initialValue="${provider.userId}" /></td>
			<td valign="top"><span id="providerError"></span></td>
		</tr>
		<tr>
			<td><b><spring:message code="mohappointment.general.service"/></b></td>
			<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
			<td><select name="service" id="service">
					<option value="">--</option>
					<c:forEach items="${services}" var="service">
						<option value="${service.serviceId}">${service.name}</option>
					</c:forEach>
				</select>
			</td>
			<td valign="top"><span id="serviceError"></span></td>
		</tr>
		<tr>
			<td><b><spring:message code="mohappointment.general.startdate"/></b></td>
			<td><img border="0" src="<openmrs:contextPath/>/moduleResources/mohappointment/images/help.gif" title="?"/></td>
			<td><input type="text" value="${startDate}" name="startDate" id="startDate" size="11" onclick="showCalendar(this);"/></td>
			<td valign="top"><span id="startDateError"></span></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><input type="button" id="btSave" value="<spring:message code='mohappointment.general.save'/>"></td>
			<td></td>
		</tr>
	</table>
</form>

<script>
	$app(document).ready(function(){
		$app("#btSave").click(function(){
			if(validateFormFields()){
				if(confirm("<spring:message code='mohappointment.general.save.confirm'/>"))
					this.form.submit();
			}
		});
	});

	function validateFormFields(){
		var valid=true;
		if(document.getElementsByName("provider")[0].value==''){
			$app("#providerError").html("*");
			$app("#providerError").addClass("error");
			valid=false;
		} else {
			$app("#providerError").html("");
			$app("#providerError").removeClass("error");
		}

		if(document.getElementById("service").value==''){
			$app("#serviceError").html("*");
			$app("#serviceError").addClass("error");
			valid=false;
		} else {
			$app("#serviceError").html("");
			$app("#serviceError").removeClass("error");
		}

		if($app("#startDate").val()==''){
			$app("#startDateError").html("*");
			$app("#startDateError").addClass("error");
			valid=false;
		} else {
			$app("#startDateError").html("");
			$app("#startDateError").removeClass("error");
		}

		if(!valid){
			$app("#errorDiv").html("<spring:message code='mohappointment.general.fillbeforesubmit'/>");
			$app("#errorDiv").addClass("error");
		} else {
			$app("#errorDiv").html("");
			$app("#errorDiv").removeClass("error");
		}
		
		return valid;
	}
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>