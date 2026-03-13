<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><g:layoutTitle default="ZelifKudos"/></title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>

<body>

<div class="win-window">
    <div class="win-titlebar">
        <span class="win-titlebar-text">ZelifKudos - Employee Recognition System</span>
    </div>
    <div class="win-menubar">
        <a href="${createLink(controller:'user', action:'list')}">Users</a>
        <a href="${createLink(controller:'kudos', action:'list')}">Kudos</a>
    </div>
    <div class="win-body">
        <g:layoutBody/>
    </div>
    <div class="win-statusbar">
        <span class="win-statusbar-panel">Ready</span>
        <span class="win-statusbar-panel" style="flex:0; white-space:nowrap;">ZelifKudos v1.0</span>
    </div>
</div>

<div class="win-taskbar">
    <a href="${request.contextPath}/" class="win-start-btn">
        <span class="win-start-icon"></span>
        Start
    </a>
    <span class="win-taskbar-clock" id="win-clock"></span>
</div>

<script>
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
</script>

<div id="spinner" style="display:none;">Loading...</div>
<asset:javascript src="application.js"/>
</body>
</html>
