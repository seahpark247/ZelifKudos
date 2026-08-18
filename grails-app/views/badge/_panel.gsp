<div class="win-window win-badge-panel">
    <div class="win-titlebar">
        <span class="win-titlebar-text">Badges</span>
        <span class="win-badge-panel-count">${earned.size()}/${catalog.size()}</span>
    </div>
    <div class="win-badge-panel-body">
        <g:each in="${catalog}" var="b">
            <g:set var="has" value="${earned.contains(b.code)}"/>
            <div class="win-badge-chip ${has ? 'win-badge-earned' : 'win-badge-locked'}"
                 title="${has ? b.name : b.description}">
                <img src="${createLink(controller: 'pwa', action: 'badge', params: [filename: b.icon])}"
                     alt="${b.name}" width="32" height="32" class="win-badge-icon"/>
                <span class="win-badge-chip-name">${has ? b.name : 'Locked'}</span>
            </div>
        </g:each>
    </div>
</div>
