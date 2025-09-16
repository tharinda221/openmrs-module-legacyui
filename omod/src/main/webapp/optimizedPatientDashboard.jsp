<%@ include file="/WEB-INF/view/module/legacyui/template/include.jsp" %>

<script>
// Lazy load portlets after page renders
$(document).ready(function() {
    // Load overview tab content only when clicked
    $('#patientOverviewTab').click(function() {
        if (!$('#patientOverview').hasClass('loaded')) {
            $('#patientOverview').load('/openmrs/portlet/patientOverview.portlet?patientId=${patient.patientId}');
            $('#patientOverview').addClass('loaded');
        }
    });
    
    // Similar lazy loading for other tabs
    $('#patientProgramsTab').click(function() {
        if (!$('#patientPrograms').hasClass('loaded')) {
            $('#patientPrograms').load('/openmrs/portlet/patientPrograms.portlet?patientId=${patient.patientId}');
            $('#patientPrograms').addClass('loaded');
        }
    });
});
</script>

<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${patient.patientId}"/>

<div id="patientTabs">
    <ul>
        <li><a id="patientOverviewTab" href="#" onclick="return changeTab(this);">Overview</a></li>
        <li><a id="patientProgramsTab" href="#" onclick="return changeTab(this);">Programs</a></li>
    </ul>
</div>

<div id="patientSections">
    <div id="patientOverview" style="display:none;">
        <div class="loading">Loading...</div>
    </div>
    <div id="patientPrograms" style="display:none;">
        <div class="loading">Loading...</div>
    </div>
</div>