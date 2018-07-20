<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="Manage Services and Providers" otherwise="/login.htm" redirect="/module/mohappointment/service.list"/>
<openmrs:htmlInclude file="/moduleResources/mohappointment/style/appointment.css" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/style/listing.css" />

<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/ui.all.css" />
<openmrs:htmlInclude file="/moduleResources/mohappointment/theme/demo.css" />

<script type="text/javascript">
		var $app = jQuery.noConflict();
		
		// Checking whether the User wants to delete the data
		function submitData(servId){
			var serv = "#deleteCtrl_"+servId
			var hrefValue = "service.list?deleteService=true&serviceId=" + servId;
			
			if(confirm("<spring:message code='mohappointment.general.delete.confirm'/>"))
				$app(serv).attr("href", hrefValue);
		}
		
		function showExportDialog(){
			showDialog();
		}
		
		function showDialog(){
			$app("#divDlg").html("<div id='dialog' style='font-size: 0.9em;' title='<spring:message code='mohappointment.export.data'/>'><p><div id='result'>"+$app('#dlgCtnt').html()+"</div></p></div>");
			$app("#dialog").dialog({
				zIndex: 980,
				bgiframe: true,
				height: 220,
				width: 550,
				modal: true
			});	
		}
		
		function showEditDialog(pId){
			distroyResultDiv();
			showEditWindow(pId);
		}
		
		function showEditWindow(pId){
			$app("#divWindow").html("<div id='dialog' style='font-size: 0.9em;' title='<spring:message code='mohappointment.appointment.service.form'/>'><p><div id='result'>"+pId+"</div></p></div>");
			$app("#dialog").dialog({
				zIndex: 980,
				bgiframe: true,
				height: 220,
				width: 550,
				modal: true
			});
		}

		var url="editService.form?serviceId="+pId;
		$.get(url, function(data) {
			  $('#result').html(data);
		});	

		function distroyResultDiv(){
			while(document.getElementById("dialog")){
				var DIVtoRemove = document.getElementById("dialog");
				DIVtoRemove.parentNode.removeChild(DIVtoRemove);
			}
		}

		$app(document).ready(function(){
		});
</script>

<%@ include file="templates/serviceProviderHeader.jsp"%>

<h2><spring:message code="mohappointment.appointment.service.management"/></h2>
<br/>
<a href="service.form"><spring:message code="mohappointment.appointment.service.add"/></a>
<br/><br/>


<div class="searchParameterBox box">
	<div class="list_container" style="width: 99%">
		<div class="list_title">
			<div class="list_title_msg"><spring:message code="mohappointment.appointment.service.current"/></div>
			<div class="list_title_bts">
			
				<form style="display: inline;" action="#" method="post">
					<input onclick="showExportDialog();" type="button" class="list_exportBt" value="<spring:message code="mohappointment.general.export"/>"/>
				</form>	
					
			</div>
			<div style="clear:both;"></div>
		</div>
		<table class="list_data">
			<tr>
				<th class="columnHeader"><spring:message code="mohappointment.general.number"/></th>
				<th class="columnHeader">Name</th>
				<th class="columnHeader">Description</th>
				<th class="columnHeader"></th>
			</tr>
			<c:if test="${empty services}">
				<tr>
					<td colspan="3" style="text-align: center;"><spring:message code="mohappointment.general.empty"/></td>
				</tr>
			</c:if>
			<c:forEach items="${services}" var="service" varStatus="status">
				<tr>
					<td class="rowValue ${status.count%2!=0?'even':''}">${((param.page-1)*pageSize)+status.count}.</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${service.name}</td>	
					<td class="rowValue ${status.count%2!=0?'even':''}">${service.description}</td>
					<!--<td class="rowValue ${status.count%2!=0?'even':''}"><input onclick="showEditWindow('${service.serviceId}');" type="button" id="btEdit" value="<spring:message code='mohappointment.general.edit'/>"></td> -->
					<td class="rowValue ${status.count%2!=0?'even':''}"><a href="service.form?editService=true&editServiceId=${service.serviceId}"><spring:message code='mohappointment.general.edit'/></a>
					 | <a id="deleteCtrl_${service.serviceId}" onclick="submitData(${service.serviceId});" href="#"><spring:message code='mohappointment.general.delete'/></a></td>
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
	<form action="service.list?export=true" method="post">
	
		<%@ include file="templates/exportForm.jsp"%>
		
	</form>
</div>

<div id="divWindow"></div>
<div id="editWindow" style="display: none;"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>