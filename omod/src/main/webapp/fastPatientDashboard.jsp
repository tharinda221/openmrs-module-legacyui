<%@ include file="/WEB-INF/view/module/legacyui/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/patientDashboard.form" />

<c:set var="OPENMRS_VIEWING_PATIENT_ID" scope="request" value="${patient.patientId}"/>
<openmrs:message var="pageTitle" code="patientDashboard.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/legacyui/template/header.jsp" %>

<script>
var startTime = performance.now();
function changeTab(tabObj) {
    if (typeof tabObj == "string") tabObj = document.getElementById(tabObj);
    if (!tabObj) return false;
    
    var tabs = tabObj.parentNode.parentNode.getElementsByTagName('a');
    for (var i=0; i<tabs.length; i++) {
        tabs[i].className = tabs[i].className.replace(' current', '');
        var divId = tabs[i].id.replace('Tab', '');
        var divObj = document.getElementById(divId);
        if (divObj) divObj.style.display = tabs[i].id == tabObj.id ? "block" : "none";
    }
    tabObj.className += ' current';
    return false;
}

document.addEventListener('DOMContentLoaded', () => {
    changeTab('patientOverviewTab');
    var loadTime = (performance.now() - startTime).toFixed(2);
    console.log('Fast Dashboard Load Time: ' + loadTime + 'ms');
    
    // Show performance indicator
    var perfDiv = document.createElement('div');
    perfDiv.style.cssText = 'position:fixed;top:10px;right:10px;background:#4CAF50;color:white;padding:5px 10px;border-radius:3px;font-size:12px;z-index:9999;';
    perfDiv.innerHTML = 'Fast Dashboard: ' + loadTime + 'ms';
    document.body.appendChild(perfDiv);
    setTimeout(() => perfDiv.remove(), 5000);
});
</script>

<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${patient.patientId}"/>

<div id="patientTabs">
    <ul>
        <li><a id="patientOverviewTab" href="#" onclick="return changeTab(this);">Overview</a></li>
        <li><a id="patientVisitsTab" href="#" onclick="return changeTab(this);">Visits</a></li>
        <li><a id="patientDemographicsTab" href="#" onclick="return changeTab(this);">Demographics</a></li>
        <li><a id="patientGraphsTab" href="#" onclick="return changeTab(this);">Graphs</a></li>
        <li><a id="formEntryTab" href="#" onclick="return changeTab(this);">Form Entry</a></li>
    </ul>
</div>

<div id="patientSections">
    <div id="patientOverview" style="display:none;">
        <openmrs:portlet url="patientOverview" id="patientDashboardOverview" patientId="${patient.patientId}"/>
    </div>
    <div id="patientVisits" style="display:none;">
        <openmrs:portlet url="patientVisits" id="patientDashboardVisits" patientId="${patient.patientId}"/>
    </div>
    <div id="patientDemographics" style="display:none;">
        <openmrs:portlet url="patientDemographics" id="patientDashboardDemographics" patientId="${patient.patientId}"/>
    </div>
    <div id="patientGraphs" style="display:none;">
        <openmrs:portlet url="patientGraphs" id="patientGraphsPortlet" patientId="${patient.patientId}"/>
    </div>
    <div id="formEntry" style="display:none;">
        <openmrs:portlet url="personFormEntry" id="formEntryPortlet" personId="${patient.personId}" parameters="showDecoration=true"/>
    </div>
</div>

<%@ include file="/WEB-INF/view/module/legacyui/template/footer.jsp" %>