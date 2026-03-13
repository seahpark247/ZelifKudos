<!doctype html>
<html>
    <head>
        <title><g:if env="development">System Error</g:if><g:else>Error</g:else></title>
        <meta name="layout" content="main">
        <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
    </head>
    <body>
        <div class="win-msgbox win-msgbox-error">
            <span class="win-msgbox-icon">⚠</span>
            <span><strong>An error has occurred.</strong></span>
        </div>
        <g:if env="development">
            <g:if test="${Throwable.isInstance(exception)}">
                <g:renderException exception="${exception}" />
            </g:if>
            <g:elseif test="${request.getAttribute('javax.servlet.error.exception')}">
                <g:renderException exception="${request.getAttribute('javax.servlet.error.exception')}" />
            </g:elseif>
            <g:else>
                <div class="win-sunken" style="margin-top:8px;">
                    <p class="win-error-text">
                        Exception: ${exception}<br/>
                        Message: ${message}<br/>
                        Path: ${path}
                    </p>
                </div>
            </g:else>
        </g:if>
        <g:else>
            <p class="win-error-text" style="margin-top:8px;">
                An unexpected error has occurred. Please try again.
            </p>
        </g:else>
        <hr class="win-divider"/>
        <div class="win-nav">
            <a href="${request.contextPath}/">Return to main</a>
        </div>
    </body>
</html>
