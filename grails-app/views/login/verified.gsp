<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Verified | ZelifKudos</title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Verification Complete</span>

    <div class="win-verified-box">
        <div class="win-checkmark">✔</div>
        <p class="win-hint" style="margin-top:12px;">
            <strong>Identity confirmed.</strong><br/>
            Welcome back, <span class="win-highlight">${userName.capitalize()}</span>.
        </p>
    </div>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * You may close this tab.<br/>
    * Your original tab will update automatically.
</p>

</body>
</html>
