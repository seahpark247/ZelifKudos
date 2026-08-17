<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><g:layoutTitle default="${app.name()}"/></title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <link rel="manifest" href="/manifest.json"/>
    <link rel="apple-touch-icon" href="/icons/apple-touch-icon.png"/>
    <meta name="theme-color" content="#c0c0c0"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="default"/>
    <meta name="apple-mobile-web-app-title" content="${app.name()}"/>
    <asset:stylesheet src="application.css"/>
    <style>
        .win-chat-msg { cursor: default; }
    </style>
    <g:layoutHead/>
</head>

<body>

<div class="win-desktop-layout">

    <g:if test="${session.userId || isDemo}">
    <div class="win-window win-chat-window" id="chatWindow">
        <div class="win-titlebar" id="chatTitlebar" style="cursor:grab;">
            <span class="win-titlebar-text">Random Chat</span>
            <button class="win-titlebar-btn" onclick="toggleChat()" title="Minimize">&minus;</button>
        </div>
        <div class="win-chat-body" id="chatBody">
            <div class="win-sunken win-chat-messages" id="chatMessages">
                <g:if test="${isDemo}">
                    <g:each in="${demoChatMessages}" var="m">
                        <div class="win-chat-msg" data-chat-ts="${System.currentTimeMillis() - (m.minutesAgo as long) * 60000}">
                            <span class="win-chat-nick" style="color:${m.color}">${m.nickname.encodeAsHTML()}</span>: ${m.content.encodeAsHTML()}
                        </div>
                    </g:each>
                </g:if>
            </div>
            <div class="win-chat-input-area">
                <g:if test="${isDemo}">
                    <input type="text" id="chatInput" placeholder="Type a message..." maxlength="500"
                           onkeydown="if(event.key==='Enter'&&!event.isComposing)sendDemoMessage()" />
                    <button class="win-btn win-btn-sm" onclick="sendDemoMessage()">Send</button>
                </g:if>
                <g:else>
                    <input type="text" id="chatInput" placeholder="Type a message..." maxlength="500"
                           onkeydown="if(event.key==='Enter'&&!event.isComposing)sendChatMessage()" />
                    <button class="win-btn win-btn-sm" onclick="sendChatMessage()">Send</button>
                </g:else>
            </div>
            <div id="chatNotice" style="display:none; padding:2px 6px; font-size:11px; color:#800000;"></div>
        </div>
    </div>
    </g:if>

    <div class="win-window">
        <div class="win-titlebar">
            <span class="win-titlebar-text"><app:name/> - Employee Recognition System</span>
        </div>
        <div class="win-menubar">
            <g:if test="${isDemo}">
                <a href="${createLink(controller:'demo', action:'list')}" class="${actionName == 'list' ? 'active' : ''}">Users</a>
                <a href="${createLink(controller:'demo', action:'history')}" class="${actionName == 'history' ? 'active' : ''}">History</a>
                <a href="${createLink(controller:'demo', action:'myKudos')}" class="${actionName == 'myKudos' ? 'active' : ''}">My Kudos</a>
            </g:if>
            <g:else>
                <a href="${createLink(controller:'user', action:'list')}" class="${controllerName == 'user' ? 'active' : ''}">Users</a>
                <a href="${createLink(controller:'kudos', action:'list')}" class="${controllerName == 'kudos' && actionName == 'list' ? 'active' : ''}">History</a>
                <a href="${createLink(controller:'kudos', action:'myKudos')}" class="${controllerName == 'kudos' && actionName == 'myKudos' ? 'active' : ''}">My Kudos</a>
            </g:else>
        </div>
        <div class="win-body">
            <g:if test="${isDemo}">
                <div style="background:#fffacd; border:1px solid #d4b800; padding:6px 10px; margin-bottom:8px; font-size:12px; display:flex; align-items:center; justify-content:space-between;">
                    <span><b>Demo Mode</b> &mdash; sandbox data, your changes only persist in this browser session. Refresh to reset.</span>
                </div>
            </g:if>
            <g:layoutBody/>
        </div>
        <div class="win-statusbar">
            <span class="win-statusbar-panel">Ready</span>
            <span class="win-statusbar-panel" style="flex:0; white-space:nowrap;"><app:name/> v<g:meta name="info.app.version"/></span>
        </div>
    </div>

</div>

<div class="win-taskbar">
    <a href="${request.contextPath}/" class="win-start-btn">
        <span class="win-start-icon"></span>
        Start
    </a>
    <span class="win-taskbar-clock" id="win-clock" onclick="onClockClick()"></span>
</div>

<script>
    function onClockClick() {
        <g:if test="${isDemo}">return;</g:if>
        // Silent unless the toggle actually happened. Anyone else — logged out, or
        // logged in without the privilege — sees nothing at all: no reload, no
        // flicker, no hint that the clock is clickable.
        fetch('/user/toggleAdmin', { method: 'POST', credentials: 'same-origin' })
            .then(function(r) { return r.json(); })
            .then(function(res) { if (res && res.toggled) { window.location.reload(); } })
            .catch(function() { /* not logged in: the auth redirect returns HTML */ });
    }

    function updateClock() {
        var now = new Date();
        var h = now.getHours();
        var m = now.getMinutes();
        var ampm = h >= 12 ? 'PM' : 'AM';
        h = h % 12 || 12;
        m = m < 10 ? '0' + m : m;
        document.getElementById('win-clock').textContent = h + ':' + m + ' ' + ampm;
    }
    updateClock();
    setInterval(updateClock, 30000);

    document.querySelectorAll('[data-utc]').forEach(function(el) {
        var d = new Date(parseInt(el.getAttribute('data-utc')));
        var yyyy = d.getFullYear();
        var mm = String(d.getMonth() + 1).padStart(2, '0');
        var dd = String(d.getDate()).padStart(2, '0');
        var hh = String(d.getHours()).padStart(2, '0');
        var min = String(d.getMinutes()).padStart(2, '0');
        el.textContent = mm + '-' + dd + '-' + yyyy + ' ' + hh + ':' + min;
    });
</script>

<g:if test="${isDemo}">
<script>
    (function() {
        var win = document.getElementById('chatWindow');
        var header = document.getElementById('chatTitlebar');
        var isDragging = false, offsetX, offsetY;
        if (header) header.addEventListener('mousedown', function(e) {
            if (e.target.closest('.win-titlebar-btn')) return;
            if (window.innerWidth <= 1100) return;   // stacked layout: no dragging
            isDragging = true;
            var rect = win.getBoundingClientRect();
            offsetX = e.clientX - rect.left;
            offsetY = e.clientY - rect.top;
            win.style.left = rect.left + 'px';
            win.style.top = rect.top + 'px';
            win.style.right = 'auto';
            win.style.bottom = 'auto';
            header.style.cursor = 'grabbing';
        });
        document.addEventListener('mousemove', function(e) {
            if (!isDragging) return;
            var x = e.clientX - offsetX, y = e.clientY - offsetY;
            var rect = win.getBoundingClientRect();
            x = Math.max(0, Math.min(x, window.innerWidth - rect.width));
            y = Math.max(0, Math.min(y, window.innerHeight - rect.height));
            win.style.left = x + 'px'; win.style.top = y + 'px';
        });
        document.addEventListener('mouseup', function() { isDragging = false; if (header) header.style.cursor = 'grab'; });
    })();

    function escapeHtml(text) { var div = document.createElement('div'); div.textContent = text; return div.innerHTML; }

    function toggleChat() {
        var win = document.getElementById('chatWindow');
        var body = document.getElementById('chatBody');
        var btn = document.querySelector('#chatWindow .win-titlebar-btn');
        var collapsed = body.style.display !== 'none';
        body.style.display = collapsed ? 'none' : '';
        win.style.minHeight = collapsed ? '0' : '';
        win.style.height = collapsed ? 'auto' : '';
        win.style.resize = collapsed ? 'none' : '';
        btn.innerHTML = collapsed ? '+' : '&minus;';
        btn.title = collapsed ? 'Restore' : 'Minimize';
    }

    // Demo chat — sessionStorage so user-typed messages persist across page navigation
    var DEMO_NICKNAMES = [
        { name: 'Rabbit',  color: '#F08A5D' },
        { name: 'Penguin', color: '#4D96FF' },
        { name: 'Owl',     color: '#9D75CB' },
        { name: 'Turtle',  color: '#6BCB77' }
    ];
    function getDemoChatId() {
        var raw = sessionStorage.getItem('demoChatId');
        if (raw) { try { return JSON.parse(raw); } catch (e) {} }
        var n = DEMO_NICKNAMES[Math.floor(Math.random() * DEMO_NICKNAMES.length)];
        sessionStorage.setItem('demoChatId', JSON.stringify(n));
        return n;
    }

    function appendDemoChatRow(nickname, color, text, ts) {
        var div = document.createElement('div');
        div.className = 'win-chat-msg';
        if (ts) {
            var d = new Date(ts);
            var pad = function(n){ return String(n).padStart(2,'0'); };
            div.title = pad(d.getMonth()+1)+'-'+pad(d.getDate())+'-'+d.getFullYear()+' '+pad(d.getHours())+':'+pad(d.getMinutes());
        }
        div.innerHTML = '<span class="win-chat-nick" style="color:' + color + '">' + escapeHtml(nickname) + '</span>: ' + escapeHtml(text);
        var el = document.getElementById('chatMessages');
        el.appendChild(div);
        el.scrollTop = el.scrollHeight;
    }

    function sendDemoMessage() {
        var input = document.getElementById('chatInput');
        var text = input.value.trim();
        if (!text) return;
        var me = getDemoChatId();
        var now = Date.now();
        appendDemoChatRow(me.name, me.color, text, now);
        var stored = JSON.parse(sessionStorage.getItem('demoChat') || '[]');
        stored.push({ nickname: me.name, color: me.color, text: text, ts: now });
        sessionStorage.setItem('demoChat', JSON.stringify(stored));
        input.value = '';
    }

    // On load: convert seed chat msgs' data-chat-ts to data-tip, render stored chat
    (function() {
        document.querySelectorAll('.win-chat-msg[data-chat-ts]').forEach(function(el) {
            var d = new Date(parseInt(el.getAttribute('data-chat-ts'), 10));
            var pad = function(n){ return String(n).padStart(2,'0'); };
            el.title = pad(d.getMonth()+1)+'-'+pad(d.getDate())+'-'+d.getFullYear()+' '+pad(d.getHours())+':'+pad(d.getMinutes());
        });
        try {
            var stored = JSON.parse(sessionStorage.getItem('demoChat') || '[]');
            stored.forEach(function(m) { appendDemoChatRow(m.nickname, m.color, m.text, m.ts); });
        } catch (e) {}
        var el = document.getElementById('chatMessages');
        if (el) el.scrollTop = el.scrollHeight;
    })();
</script>
</g:if>
<g:if test="${!isDemo && session.userId}">
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2/lib/stomp.min.js"></script>
<script>
    (function() {
        var win = document.getElementById('chatWindow');
        var header = document.getElementById('chatTitlebar');
        var isDragging = false;
        var offsetX, offsetY;

        header.addEventListener('mousedown', function(e) {
            if (e.target.closest('.win-titlebar-btn')) return;
            if (window.innerWidth <= 1100) return;   // stacked layout: no dragging
            isDragging = true;
            var rect = win.getBoundingClientRect();
            offsetX = e.clientX - rect.left;
            offsetY = e.clientY - rect.top;
            win.style.left = rect.left + 'px';
            win.style.top = rect.top + 'px';
            win.style.right = 'auto';
            win.style.bottom = 'auto';
            header.style.cursor = 'grabbing';
        });

        document.addEventListener('mousemove', function(e) {
            if (!isDragging) return;
            var x = e.clientX - offsetX;
            var y = e.clientY - offsetY;
            var rect = win.getBoundingClientRect();
            x = Math.max(0, Math.min(x, window.innerWidth - rect.width));
            y = Math.max(0, Math.min(y, window.innerHeight - rect.height));
            win.style.left = x + 'px';
            win.style.top = y + 'px';
        });

        document.addEventListener('mouseup', function() {
            isDragging = false;
            header.style.cursor = 'grab';
        });
    })();

    var stompClient = null;

    function escapeHtml(text) {
        var div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function appendMessage(content, timestamp, nickname, color) {
        var el = document.getElementById('chatMessages');
        var d = new Date(timestamp);
        var mm = String(d.getMonth() + 1).padStart(2, '0');
        var dd = String(d.getDate()).padStart(2, '0');
        var yyyy = d.getFullYear();
        var hh = String(d.getHours()).padStart(2, '0');
        var min = String(d.getMinutes()).padStart(2, '0');
        var timeStr = mm + '-' + dd + '-' + yyyy + ' ' + hh + ':' + min;
        var nick = nickname || 'Anonymous';
        var col = color || '#CCCCCC';
        var div = document.createElement('div');
        div.className = 'win-chat-msg';
        div.title = timeStr;
        div.innerHTML = '<span class="win-chat-nick" style="color:' + col + '">' + escapeHtml(nick) + '</span>: '
            + escapeHtml(content);
        el.appendChild(div);
        el.scrollTop = el.scrollHeight;
    }

    function connectChat() {
        var socket = new SockJS('/ws/chat');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;
        stompClient.connect({}, function() {
            stompClient.subscribe('/topic/chat', function(msg) {
                var data = JSON.parse(msg.body);
                appendMessage(data.content, data.timestamp, data.nickname, data.color);
            });
        }, function() {
            setTimeout(connectChat, 5000);
        });
    }

    // Mirrors of the server-side guards in ChatWebSocketController.
    var chatCooldownMs = <app:chatCooldownMs/>;
    var chatDuplicateWindowMs = <app:chatDuplicateWindowMs/>;
    var lastChatSentAt = 0;
    var lastChatText = '';
    var chatNoticeTimer = null;

    function showChatNotice(text) {
        var el = document.getElementById('chatNotice');
        if (!el) return;
        el.textContent = text;
        el.style.display = '';
        clearTimeout(chatNoticeTimer);
        chatNoticeTimer = setTimeout(function() { el.style.display = 'none'; }, 2500);
    }

    function sendChatMessage() {
        var input = document.getElementById('chatInput');
        var text = input.value.trim();
        if (!text || !stompClient || !stompClient.connected) return;

        // The server drops anything inside these windows without replying, so
        // check here first and keep the text in the box instead of losing it.
        var since = Date.now() - lastChatSentAt;
        if (lastChatSentAt && since < chatCooldownMs) {
            showChatNotice('Slow down — ' + Math.ceil((chatCooldownMs - since) / 1000) + 's to go');
            return;
        }
        if (text === lastChatText && lastChatSentAt && since < chatDuplicateWindowMs) {
            showChatNotice('You just sent that.');
            return;
        }

        stompClient.send('/app/chat.send', {}, JSON.stringify({ content: text }));
        lastChatSentAt = Date.now();
        lastChatText = text;
        input.value = '';
    }

    function toggleChat() {
        var win = document.getElementById('chatWindow');
        var body = document.getElementById('chatBody');
        var btn = document.querySelector('#chatWindow .win-titlebar-btn');
        var collapsed = body.style.display !== 'none';
        body.style.display = collapsed ? 'none' : '';
        win.style.minHeight = collapsed ? '0' : '';
        win.style.height = collapsed ? 'auto' : '';
        win.style.resize = collapsed ? 'none' : '';
        btn.innerHTML = collapsed ? '+' : '&minus;';
        btn.title = collapsed ? 'Restore' : 'Minimize';
    }

    fetch('/chat/recent').then(function(r) { return r.json(); }).then(function(msgs) {
        msgs.forEach(function(m) { appendMessage(m.content, m.timestamp, m.nickname, m.color); });
    }).catch(function() {}).finally(function() { connectChat(); });
</script>
</g:if>

<div id="spinner" style="display:none;">Loading...</div>
<asset:javascript src="application.js"/>
<script>
if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/service-worker.js');
}
</script>
</body>
</html>
