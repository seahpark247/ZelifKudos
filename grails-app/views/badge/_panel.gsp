<g:if test="${fresh}">
<%-- Shown once, so it has to be unmissable: the inline note it replaced could be
     scrolled past and never seen again. Reuses the window chrome; only the
     backdrop and centring are new. --%>
<div class="win-modal-backdrop" id="badgeModal">
    <div class="win-window win-modal">
        <div class="win-titlebar">
            <span class="win-titlebar-text">${fresh.size() > 1 ? 'New Badges' : 'New Badge'}</span>
            <button class="win-titlebar-btn" onclick="closeBadgeModal()" title="Close">&times;</button>
        </div>
        <div class="win-modal-body">
            <g:each in="${fresh}" var="b">
                <div class="win-modal-badge">
                    <img src="${createLink(controller: 'pwa', action: 'badge', params: [filename: b.icon])}"
                         alt="${b.name}" width="64" height="64" class="win-badge-icon"/>
                    <div class="win-modal-badge-name">${b.name}</div>
                    <div class="win-modal-badge-desc">${b.description}</div>
                </div>
            </g:each>
            <div class="win-modal-actions">
                <button type="button" class="win-btn win-btn-primary" id="badgeModalOk" onclick="closeBadgeModal()">OK</button>
            </div>
        </div>
    </div>
</div>
<script>
    function closeBadgeModal() {
        var m = document.getElementById('badgeModal');
        if (m) m.remove();
    }
    document.getElementById('badgeModal').addEventListener('click', function(e) {
        if (e.target === this) closeBadgeModal();
    });
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') closeBadgeModal();
    });
    // Focus rather than a key handler of our own: a focused button already
    // answers to Enter and to Space, and it puts the keyboard where the only
    // action on the dialog is.
    document.getElementById('badgeModalOk').focus();
</script>
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
