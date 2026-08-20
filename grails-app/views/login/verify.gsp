<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Signing In | <app:name/></title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Signing In</span>

    <div class="win-waiting-box">
        <p class="win-hint">
            Signing in as:<br/>
            <span class="win-highlight">${email}</span>
        </p>

        <div class="win-progress">
            <div class="win-progress-bar"></div>
        </div>

        <%-- Submitted by script, not by hand. A mail scanner takes the HTML and
             stops there, so the token survives its visit; a browser posts within
             a frame of paint and the click still lands straight in the app. The
             button is what someone with scripting off sees instead. --%>
        <g:form controller="login" action="confirm" method="post" name="confirmForm">
            <g:hiddenField name="token" value="${token}"/>
            <noscript>
                <button type="submit" class="win-btn win-btn-primary">Log In</button>
            </noscript>
        </g:form>
    </div>
</div>

<script>
    document.forms.confirmForm.submit();
</script>

</body>
</html>
