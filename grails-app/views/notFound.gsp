<!doctype html>
<html>
    <head>
        <title>404 - Not Found</title>
        <meta name="layout" content="main">
    </head>
    <body>
        <div class="win-msgbox win-msgbox-error">
            <span class="win-msgbox-icon">⚠</span>
            <span><strong>Error 404 - Page not found</strong></span>
        </div>
        <p class="win-error-text" style="margin-top:8px;">
            The path <span class="win-highlight">${request.forwardURI}</span> could not be found.
        </p>
        <hr class="win-divider"/>
        <div class="win-nav">
            <a href="${request.contextPath}/">Return to main</a>
        </div>
    </body>
</html>
