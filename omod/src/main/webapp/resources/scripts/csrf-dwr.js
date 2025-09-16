/**
 * CSRF protection for DWR requests
 * Reads XSRF-TOKEN cookie and sets it as X-CSRF-Token header for all DWR calls
 */
(function() {
    function getCookie(name) {
        var value = "; " + document.cookie;
        var parts = value.split("; " + name + "=");
        if (parts.length == 2) return parts.pop().split(";").shift();
        return null;
    }
    
    var token = getCookie('XSRF-TOKEN');
    if (token && typeof dwr !== 'undefined' && dwr.engine) {
        dwr.engine.setHeaders({'X-CSRF-Token': token});
    }
})();