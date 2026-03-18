<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>My Kudos | ZelifKudos</title>
</head>

<body>

<div class="win-groupbox" style="margin-top:0;">
    <span class="win-groupbox-title">My Kudos (${total ?: 0} total)</span>

    <g:if test="${kudosList}">
        <div class="win-sunken">
            <g:each in="${kudosList}" var="k" status="i">
                <g:each in="${resetDates}" var="rd">
                    <g:if test="${i > 0 && kudosList[i-1].dateCreated > rd && k.dateCreated <= rd}">
                        <div class="win-reset-divider">
                            <span>&#9632; RESET — <g:formatDate date="${rd}" format="yyyy-MM-dd HH:mm"/> &#9632;</span>
                        </div>
                    </g:if>
                    <g:if test="${i == 0 && k.dateCreated <= rd}">
                        <div class="win-reset-divider">
                            <span>&#9632; RESET — <g:formatDate date="${rd}" format="yyyy-MM-dd HH:mm"/> &#9632;</span>
                        </div>
                    </g:if>
                </g:each>
                <div class="win-log-entry">
                    <span class="win-index">${String.format('%03d', offset + i + 1)}</span>
                    <span style="color:#808080;">Anonymous</span>
                    <g:if test="${k.message}">
                        <span class="win-log-message">"${k.message.encodeAsHTML()}"</span>
                    </g:if>
                    <span class="win-log-date">
                        <g:formatDate date="${k.dateCreated}" format="yyyy-MM-dd HH:mm"/>
                    </span>
                </div>
            </g:each>
        </div>

        <g:if test="${totalPages > 1}">
            <div class="win-pager">
                <g:if test="${offset > 0}">
                    <a href="${createLink(action:'myKudos', params:[offset: offset - max])}" class="win-btn win-btn-sm">&lt; Prev</a>
                </g:if>
                <g:else>
                    <button class="win-btn win-btn-sm" disabled>&lt; Prev</button>
                </g:else>

                <span class="win-pager-text">Page ${currentPage} of ${totalPages}</span>

                <g:if test="${offset + max < total}">
                    <a href="${createLink(action:'myKudos', params:[offset: offset + max])}" class="win-btn win-btn-sm">Next &gt;</a>
                </g:if>
                <g:else>
                    <button class="win-btn win-btn-sm" disabled>Next &gt;</button>
                </g:else>
            </div>
        </g:if>
    </g:if>
    <g:else>
        <p class="win-empty">
            No kudos received yet. Your time will come!
        </p>
    </g:else>
</div>

</body>
</html>
