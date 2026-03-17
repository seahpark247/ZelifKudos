<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Waiting | ZelifKudos</title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Waiting for verification</span>

    <div class="win-waiting-box">
        <p class="win-hint">
            Login link sent to:<br/>
            <span class="win-highlight">${email}</span>
        </p>

        <div class="win-hourglass">⏳</div>

        <div class="win-progress">
            <div class="win-progress-bar"></div>
        </div>

        <p class="win-waiting-status" id="poll-status">
            Checking mailbox...
        </p>

        <a href="https://outlook.cloud.microsoft/mail/inbox/" target="_blank" class="win-btn win-btn-primary" style="margin-top: 10px;">Open Inbox</a>
    </div>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * Open your email and click the link.<br/>
    * This page will update automatically.<br/>
    * The link expires in 15 minutes.
</p>

<script>
    var pollInterval = setInterval(function() {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '${createLink(action: "checkToken")}');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
            if (xhr.status === 200) {
                var res = JSON.parse(xhr.responseText);
                if (res.status === 'verified') {
                    clearInterval(pollInterval);
                    document.getElementById('poll-status').textContent = 'Access granted!';
                    setTimeout(function() {
                        window.location.href = '${createLink(controller: "user", action: "list")}';
                    }, 1000);
                }
            }
        };
        xhr.send();
    }, 3000);
</script>

</body>
</html>
