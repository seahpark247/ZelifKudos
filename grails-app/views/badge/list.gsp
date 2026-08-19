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
        <g:if test="${earned.isEmpty()}">
            Nothing yet. Send a kudo to a teammate and the first one is yours.<br/>
        </g:if>
        <span class="win-badge-progress">${earned.size()} of ${catalog.size()} collected</span>
    </p>

    <div class="win-badge-grid">
        <g:each in="${catalog}" var="b">
            <g:set var="has" value="${earned.contains(b.code)}"/>
            <g:set var="p" value="${progress[b.code]}"/>
            <div class="win-badge ${has ? 'win-badge-earned' : 'win-badge-locked'}"
                 title="${has ? 'Earned ' + (earnedDates[b.code]?.format('MMM d, yyyy') ?: '') : b.description}">
                <img src="${createLink(controller: 'pwa', action: 'badge', params: [filename: b.icon])}"
                     alt="${b.name}" class="win-badge-icon" width="64" height="64"/>
                <div class="win-badge-name">${has ? b.name : '? ? ?'}</div>
                <div class="win-badge-desc">${b.description}</div>

<%-- A gauge earns its space only when there is a journey to show. A target of
     1 is binary — the greyed icon already says it — and an empty bar reads as
     "you have done nothing" rather than "here is the goal", so the number
     carries it until there is progress to draw. --%>
                <g:if test="${!has && p && p.target > 1}">
                    <div class="win-badge-meter">
                        <g:if test="${p.current > 0}">
                            <div class="win-gauge">
                                <div class="win-gauge-fill" style="width:${(int) Math.round(p.current * 100 / p.target)}%"></div>
                            </div>
                        </g:if>
                        <div class="win-gauge-label">${p.current} / ${p.target}</div>
                    </div>
                </g:if>
            </div>
        </g:each>
    </div>
</div>

</body>
</html>
