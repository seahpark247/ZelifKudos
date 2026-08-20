<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Confirm Login | <app:name/></title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Confirm Login</span>

    <p class="win-hint">
        Signing in as:<br/>
        <span class="win-highlight">${email}</span>
    </p>

    <g:form controller="login" action="confirm" method="post">
        <g:hiddenField name="token" value="${token}"/>
        <button type="submit" class="win-btn win-btn-primary">
            Log In
        </button>
    </g:form>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * The link expires 15 minutes after you asked for it.
</p>

</body>
</html>
