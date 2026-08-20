<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><g:layoutTitle default="${app.name()}"/></title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <link rel="manifest" href="/manifest.json"/>
    <link rel="apple-touch-icon" href="/icons/apple-touch-icon.png"/>
    <meta name="theme-color" content="#c0c0c0"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default"/>
    <meta name="apple-mobile-web-app-title" content="${app.name()}"/>
    <asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>

<body>

<div class="win-desktop-layout">

    <g:if test="${session.userId && !isDemo}"><badge:panel/></g:if>

    <div class="win-window">
        <div class="win-titlebar">
            <span class="win-titlebar-text"><app:name/> - Employee Recognition System</span>
        </div>
        <div class="win-menubar">
            <g:if test="${isDemo}">
                <a href="${createLink(controller:'demo', action:'list')}" class="${actionName == 'list' ? 'active' : ''}">Users</a>
                <a href="${createLink(controller:'demo', action:'history')}" class="${actionName == 'history' ? 'active' : ''}">History</a>
                <a href="${createLink(controller:'demo', action:'myKudos')}" class="${actionName == 'myKudos' ? 'active' : ''}">My Kudos</a>
                <a href="${createLink(controller:'demo', action:'badges')}" class="${actionName == 'badges' ? 'active' : ''}">Badges</a>
            </g:if>
            <g:else>
                <a href="${createLink(controller:'user', action:'list')}" class="${controllerName == 'user' ? 'active' : ''}">Users</a>
                <a href="${createLink(controller:'kudos', action:'list')}" class="${controllerName == 'kudos' && actionName == 'list' ? 'active' : ''}">History</a>
                <a href="${createLink(controller:'kudos', action:'myKudos')}" class="${controllerName == 'kudos' && actionName == 'myKudos' ? 'active' : ''}">My Kudos</a>
                <a href="${createLink(controller:'badge', action:'list')}" class="${controllerName == 'badge' ? 'active' : ''}">Badges</a>
            </g:else>
        </div>
        <div class="win-body">
            <g:if test="${isDemo}">
                <div style="background:#fffacd; border:1px solid #d4b800; padding:6px 10px; margin-bottom:8px; font-size:12px; display:flex; align-items:center; justify-content:space-between;">
                    <span><b>Demo Mode</b> &mdash; sandbox data, your changes only persist in this browser session. Refresh to reset.</span>
                </div>
            </g:if>
            <g:layoutBody/>
        </div>
        <div class="win-statusbar">
            <span class="win-statusbar-panel">Ready</span>
            <span class="win-statusbar-panel" style="flex:0; white-space:nowrap;"><app:name/> v<g:meta name="info.app.version"/></span>
        </div>
    </div>

</div>

<div class="win-taskbar">
    <a href="${request.contextPath}/" class="win-start-btn">
        <span class="win-start-icon"></span>
        Start
    </a>
    <span class="win-taskbar-clock" id="win-clock" onclick="onClockClick()"></span>
</div>

<script>
    function onClockClick() {
        <g:if test="${isDemo}">return;</g:if>
        // Silent unless the toggle actually happened. Anyone else — logged out, or
        // logged in without the privilege — sees nothing at all: no reload, no
        // flicker, no hint that the clock is clickable.
        fetch('/user/toggleAdmin', { method: 'POST', credentials: 'same-origin' })
            .then(function(r) { return r.json(); })
            .then(function(res) { if (res && res.toggled) { window.location.reload(); } })
            .catch(function() { /* not logged in: the auth redirect returns HTML */ });
    }

    function updateClock() {
        var now = new Date();
        var h = now.getHours();
        var m = now.getMinutes();
        var ampm = h >= 12 ? 'PM' : 'AM';
        h = h % 12 || 12;
        m = m < 10 ? '0' + m : m;
        document.getElementById('win-clock').textContent = h + ':' + m + ' ' + ampm;
    }
    updateClock();
    setInterval(updateClock, 30000);

    document.querySelectorAll('[data-utc]').forEach(function(el) {
        var d = new Date(parseInt(el.getAttribute('data-utc')));
        var yyyy = d.getFullYear();
        var mm = String(d.getMonth() + 1).padStart(2, '0');
        var dd = String(d.getDate()).padStart(2, '0');
        var hh = String(d.getHours()).padStart(2, '0');
        var min = String(d.getMinutes()).padStart(2, '0');
        el.textContent = mm + '-' + dd + '-' + yyyy + ' ' + hh + ':' + min;
    });
</script>


<asset:javascript src="application.js"/>
<script>
if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/service-worker.js');
}
</script>
</body>
</html>
