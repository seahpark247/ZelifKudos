<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Waiting | <app:name/></title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Waiting for verification</span>

    <div class="win-waiting-box">
        <p class="win-hint">
            Login link sent to:<br/>
            <span class="win-highlight">${email}</span>
        </p>

        <div class="win-progress">
            <div class="win-progress-bar"></div>
        </div>

        <%-- An instruction, not a status: the app cannot see anyone's mailbox,
             it is waiting on a click. Saying otherwise leaves people watching
             the bar for something that is never going to happen here. --%>
        <p class="win-waiting-status" id="poll-status">
            Check your inbox and click the link...
        </p>

    </div>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * This page will update automatically.<br/>
    * The link expires in 15 minutes.
</p>

<script>
    var pollInterval = setInterval(function() {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '${createLink(action: "checkToken")}');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
            if (xhr.status !== 200) { return; }
            var res = JSON.parse(xhr.responseText);
            var status = document.getElementById('poll-status');
            if (res.status === 'verified') {
                clearInterval(pollInterval);
                status.textContent = 'Access granted!';
                setTimeout(function() {
                    window.location.href = '${createLink(controller: "user", action: "list")}';
                }, 1000);
            } else if (res.status === 'expired' || res.status === 'no_token') {
                // Never leave the progress bar running on a link that can no
                // longer verify — say so and send them back to request another.
                clearInterval(pollInterval);
                status.textContent = 'That link expired. Taking you back...';
                setTimeout(function() {
                    window.location.href = '${createLink(controller: "login", action: "index")}';
                }, 2500);
            }
        };
        xhr.send();
    }, 3000);
</script>

</body>
</html>
