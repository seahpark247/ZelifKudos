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

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Employee Roster (${users?.size() ?: 0})</span>

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
            <li>
                <span>
                    <span class="win-index">${i+1}.</span>
                    <g:encodeAs codec="HTML">${u.name}</g:encodeAs><g:if test="${isAdmin}"> (${kudosCounts[u.id] ?: 0})</g:if>
                </span>
                <g:if test="${session.userId != u.id}">
                    <g:form controller="kudos" action="send" method="POST" class="win-inline-form" name="kudos-form-${u.id}">
                        <input type="hidden" name="id" value="${u.id}" />
                        <button type="button"
                                class="win-btn win-btn-sm"
                                onclick="confirmKudos('${u.name.encodeAsJavaScript()}', 'kudos-form-${u.id}')">
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
