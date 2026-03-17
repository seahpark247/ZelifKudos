<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Login | ZelifKudos</title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Log In</span>

    <p class="win-hint">
        Enter your ZelifCam email address.<br/>
        A login link will be sent to your inbox.
    </p>

    <g:if test="${flash.message}">
        <div class="win-msgbox">
            <span class="win-msgbox-icon">i</span>
            <span>${flash.message}</span>
        </div>
    </g:if>

    <g:if test="${flash.warning}">
        <div class="win-msgbox">
            <span class="win-msgbox-icon">!</span>
            <span>${flash.warning}</span>
        </div>
    </g:if>

    <g:if test="${flash.error}">
        <div class="win-msgbox">
            <span class="win-msgbox-icon">✕</span>
            <span>${flash.error}</span>
        </div>
    </g:if>

    <g:form controller="login" action="sendLink" method="post">
        <div class="win-field">
            <label class="win-label">Email address:</label>
            <g:textField name="email" required="true" placeholder="you@zelifcam.net" class="win-input"/>
        </div>
        <button type="submit" class="win-btn win-btn-primary">
            Send Login Link
        </button>
    </g:form>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * A magic link will be sent to your email.<br/>
    * The link expires in 15 minutes.
</p>

</body>
</html>
