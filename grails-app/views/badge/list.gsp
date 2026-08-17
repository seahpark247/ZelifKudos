<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Badges | <app:name/></title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">Badges</span>

    <p class="win-hint">
        Everything else resets on Friday. These don't.<br/>
        <span class="win-badge-progress">${earned.size()} of ${catalog.size()} collected</span>
    </p>

    <div class="win-badge-grid">
        <g:each in="${catalog}" var="b">
            <g:set var="has" value="${earned.contains(b.code)}"/>
            <div class="win-badge ${has ? 'win-badge-earned' : 'win-badge-locked'}"
                 title="${has ? 'Earned ' + (earnedDates[b.code]?.format('MMM d, yyyy') ?: '') : b.description}">
                <img src="${createLink(controller: 'pwa', action: 'badge', params: [filename: b.icon])}"
                     alt="${b.name}" class="win-badge-icon" width="64" height="64"/>
                <div class="win-badge-name">${has ? b.name : '? ? ?'}</div>
                <div class="win-badge-desc">${b.description}</div>
            </div>
        </g:each>
    </div>
</div>

<hr class="win-divider"/>
<p class="win-note">
    * Badges are permanent — losing a count never takes one back.<br/>
    * New ones are checked whenever you open this page.
</p>

</body>
</html>
