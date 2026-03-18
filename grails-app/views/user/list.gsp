<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Users | ZelifKudos</title>
    <script>
        function confirmKudos(name, formName) {
            if(confirm('Send kudos to ' + name + '?')) {
                document.getElementsByName(formName)[0].submit()
            }
        }
    </script>
</head>

<body>

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

<g:if test="${myKudosCount > 0}">
    <div class="win-msgbox" style="margin-bottom:8px;">
        <span class="win-msgbox-icon">i</span>
        <span>You received <b>${myKudosCount}</b> kudos this week!</span>
    </div>
    <g:if test="${recentMessages?.any { it.message }}">
        <div class="win-sunken" style="margin-bottom:8px; padding:8px 12px;">
            <g:each in="${recentMessages}" var="k">
                <g:if test="${k.message}">
                    <div style="font-size:12px; color:#555; margin:2px 0;">&bull; &ldquo;${k.message.encodeAsHTML()}&rdquo;</div>
                </g:if>
            </g:each>
        </div>
    </g:if>
</g:if>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Employee Roster (${users?.size() ?: 0})</span>
    <p style="font-size:12px; color:#808080; margin:0 0 8px 0; font-style:italic;">Send kudos to climb the ranks!</p>

    <g:if test="${isAdmin}">
        <div style="text-align:right; margin-bottom:8px;">
            <g:form controller="kudos" action="reset" method="POST" class="win-inline-form">
                <button type="button" class="win-btn win-btn-sm"
                        onclick="var total = ${kudosCounts.values().sum() ?: 0}; if(total === 0) { alert('Nothing to reset, no kudos since the last reset.'); } else if(confirm('Reset all kudos counts to 0? This action cannot be undone, but history will be preserved.')) { this.parentNode.submit(); }">
                    Reset All Kudos
                </button>
            </g:form>
        </div>
    </g:if>

    <ul class="win-listview">
        <g:each in="${users}" var="u" status="i">
            <li class="${session.userId == u.id ? 'win-me' : ''}">
                <span>
                    <span class="win-index">${i+1}.</span>
                    <g:encodeAs codec="HTML">${u.name.capitalize()}</g:encodeAs><g:if test="${isAdmin}"> (${kudosCounts[u.id] ?: 0})</g:if>
                </span>
                <g:if test="${session.userId != u.id}">
                    <g:form controller="kudos" action="send" method="POST" class="win-inline-form" name="kudos-form-${u.id}">
                        <input type="hidden" name="id" value="${u.id}" />
                        <input type="text" name="message" placeholder="Why..." maxlength="200" class="win-input-sm" />
                        <button type="button"
                                class="win-btn win-btn-sm"
                                onclick="confirmKudos('${u.name.capitalize().encodeAsJavaScript()}', 'kudos-form-${u.id}')">
                            Kudos!
                        </button>
                    </g:form>
                </g:if>
                <g:else>
                    <span class="win-tag">(You)</span>
                </g:else>
            </li>
        </g:each>
    </ul>
</div>

</body>
</html>
