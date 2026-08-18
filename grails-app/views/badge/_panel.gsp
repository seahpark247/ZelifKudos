<g:if test="${fresh}">
    <div class="win-msgbox win-msgbox-badge">
        <span class="win-msgbox-icon">&#9733;</span>
        <span>
            <b>${fresh.size() > 1 ? 'New badges' : 'New badge'}:</b> ${fresh.join(', ')}
            &mdash; <g:link controller="badge" action="list">have a look</g:link>
        </span>
    </div>
</g:if>

<div class="win-window win-badge-panel">
    <div class="win-titlebar">
        <span class="win-titlebar-text">Badges</span>
    </div>
    <div class="win-badge-panel-body">
        <g:each in="${catalog}" var="b">
            <div class="win-badge-chip win-badge-earned"
                 title="${earnedDates[b.code] ? 'Earned ' + earnedDates[b.code].format('MMM d, yyyy') : ''}">
                <img src="${createLink(controller: 'pwa', action: 'badge', params: [filename: b.icon])}"
                     alt="${b.name}" width="32" height="32" class="win-badge-icon"/>
                <span class="win-badge-chip-name">${b.name}</span>
            </div>
        </g:each>
    </div>
</div>
