package kudos

class DemoController {

    static final long DEMO_ME_ID = -1L

    /**
     * Caps on what one session may accumulate. /demo takes no login, so without
     * them a crawler grows its session row without bound — and Spring Session
     * keeps that row in Postgres for thirty days. Both sit far above what the
     * paged views ever show, so nobody demoing the app reaches them.
     */
    static final int MAX_SENT = 50
    static final int MAX_RESETS = 20

    static final List<Map> DEMO_USERS = [
        [id: DEMO_ME_ID, name: 'you'],
        [id: -2L, name: 'alice'],
        [id: -3L, name: 'bob'],
        [id: -4L, name: 'charlie'],
        [id: -5L, name: 'diana'],
        [id: -6L, name: 'eve'],
        [id: -7L, name: 'frank'],
    ]
    static final Map<Long, String> SEED_NAME = DEMO_USERS.collectEntries { [(it.id): it.name] }

    static final Map<Long, Integer> SEED_RECEIVED = [
        (-2L): 12, (-3L): 8, (-4L): 5, (-5L): 5,
        (DEMO_ME_ID): 2, (-6L): 2, (-7L): 0
    ]
    static final Map<Long, Integer> SEED_SENT = [
        (-2L): 9, (-3L): 7, (-4L): 6, (-5L): 4,
        (DEMO_ME_ID): 3, (-6L): 2, (-7L): 1
    ]
    static final Map<Long, String> SEED_FEELINGS = [
        (-2L): 'caffeinated and ready',
        (-4L): 'friday vibes',
    ]

    static final List<List> SEED_KUDOS_LOG = [
        // [senderId, receiverId, message, minutesAgo]
        [-2L, -3L, 'Stellar onboarding doc.', 5],
        [-3L, -2L, 'Saved my deploy yesterday.', 18],
        [-4L, -2L, 'Best PR review of the week.', 33],
        [-5L, -2L, null, 60],
        [-2L, -4L, 'Thanks for the design feedback!', 90],
        [-6L, -2L, null, 120],
        [-2L, -5L, 'Quick fix on prod, thank you.', 180],
        [-4L, DEMO_ME_ID, 'Thanks for helping with the deploy.', 240],
        [-3L, -4L, 'Loved your demo today.', 360],
        [-5L, DEMO_ME_ID, 'Great PR review yesterday!', 480],
        [DEMO_ME_ID, -2L, 'You crushed it on the migration.', 600],
        [-2L, -5L, 'Cleanest commit history I have ever seen.', 720],
        [-7L, -2L, null, 1440],
        [-3L, -5L, 'Patient debugging buddy.', 1800],
        [DEMO_ME_ID, -3L, 'Always helpful in standup.', 2160],
        [-4L, -3L, 'You crushed that bug fix.', 2880],
        [-2L, -3L, null, 3600],
    ]



    def index() { redirect(action: 'list') }

    private Map getDemoState() {
        Map state = session.demoState as Map
        if (!state) {
            state = [
                kudosCountDelta: [:],
                sentCountDelta: [:],
                feelingOverrides: [:],
                sentKudos: [],
                resetAts: [],
            ]
            session.demoState = state
        }
        state
    }

    private List<Long> getResetAts() {
        (demoState.resetAts as List<Long>) ?: []
    }

    private Map<Long, Integer> mergedReceived() {
        Map<Long, Integer> out = SEED_RECEIVED.collectEntries { k, v -> [(k): v] }
        if (resetAts) {
            out = out.collectEntries { k, v -> [(k): 0] }
        }
        (demoState.kudosCountDelta as Map<Long, Integer>).each { k, v ->
            out[k] = (out[k] ?: 0) + v
        }
        out
    }

    private Map<Long, Integer> mergedSent() {
        Map<Long, Integer> out = SEED_SENT.collectEntries { k, v -> [(k): v] }
        if (resetAts) {
            out = out.collectEntries { k, v -> [(k): 0] }
        }
        (demoState.sentCountDelta as Map<Long, Integer>).each { k, v ->
            out[k] = (out[k] ?: 0) + v
        }
        out
    }

    private Map<Long, String> mergedFeelings() {
        Map<Long, String> out = SEED_FEELINGS.collectEntries { k, v -> [(k): v] }
        (demoState.feelingOverrides as Map<Long, String>).each { k, v ->
            if (v == null) {
                out.remove(k)
            } else {
                out[k] = v
            }
        }
        out
    }

    private List<Map> mergedKudosLog() {
        // Anchor seed timestamps just before the earliest reset so all reset markers fall after seed
        Long earliestReset = resetAts ? resetAts.min() : null
        long anchor = earliestReset ? (earliestReset - 60_000L) : System.currentTimeMillis()
        List<Map> seed = SEED_KUDOS_LOG.collect { row ->
            [
                sender: [name: SEED_NAME[row[0] as Long]],
                receiver: [name: SEED_NAME[row[1] as Long]],
                message: row[2],
                dateCreated: new Date(anchor - ((row[3] as Long) * 60_000L)),
                _senderId: row[0] as Long,
                _receiverId: row[1] as Long,
            ]
        }
        List<Map> session_ = (demoState.sentKudos as List<Map>).collect { entry ->
            [
                sender: [name: SEED_NAME[entry.senderId as Long]],
                receiver: [name: SEED_NAME[entry.receiverId as Long]],
                message: entry.message,
                dateCreated: new Date(entry.ts as Long),
                _senderId: entry.senderId as Long,
                _receiverId: entry.receiverId as Long,
            ]
        }
        (seed + session_).sort { -(it.dateCreated.time) }
    }

    def list() {
        Map<Long, Integer> received = mergedReceived()
        Map<Long, Integer> sent = mergedSent()
        Map<Long, String> feelings = mergedFeelings()
        List<Map> sortedUsers = DEMO_USERS.sort(false) { -(sent[it.id] ?: 0) }

        List<Map> recentMessages = mergedKudosLog()
            .findAll { it._receiverId == DEMO_ME_ID && it.message }
            .take(3)

        render(view: '/user/list', model: [
            users: sortedUsers,
            kudosCounts: received,
            isAdmin: true,
            currentUserId: DEMO_ME_ID,
            myKudosCount: received[DEMO_ME_ID] ?: 0,
            recentMessages: recentMessages,
            feelings: feelings,
            isDemo: true,
        ])
    }

    def history() {
        int max = 15
        int offset = Math.max(0, params.int('offset', 0))
        List<Map> all = mergedKudosLog()
        int total = all.size()
        int totalPages = total ? (int) Math.ceil((double) total / max) : 0
        int currentPage = (int)(offset / max) + 1
        List<Map> paged = all.drop(offset).take(max)

        render(view: '/kudos/list', model: [
            kudosList: paged,
            total: total,
            resetDates: resetAts.collect { new Date(it as Long) }.sort(false) { -it.time },
            max: max,
            offset: offset,
            totalPages: totalPages,
            currentPage: currentPage,
            currentUser: [name: 'you', admin: true],
            isDemo: true,
        ])
    }

    def myKudos() {
        int max = 15
        int offset = Math.max(0, params.int('offset', 0))
        List<Map> mine = mergedKudosLog().findAll { it._receiverId == DEMO_ME_ID }
        int total = mine.size()
        int totalPages = total ? (int) Math.ceil((double) total / max) : 0
        int currentPage = (int)(offset / max) + 1
        List<Map> paged = mine.drop(offset).take(max)

        render(view: '/kudos/myKudos', model: [
            kudosList: paged,
            total: total,
            resetDates: resetAts.collect { new Date(it as Long) }.sort(false) { -it.time },
            max: max,
            offset: offset,
            totalPages: totalPages,
            currentPage: currentPage,
            isDemo: true,
        ])
    }

    def send() {
        Long receiverId = params.long('id')
        String message = params.message?.trim()
        if (receiverId && receiverId != DEMO_ME_ID && SEED_NAME.containsKey(receiverId)) {
            Map state = demoState
            ((Map<Long, Integer>) state.kudosCountDelta)[receiverId] = ((state.kudosCountDelta as Map<Long, Integer>)[receiverId] ?: 0) + 1
            ((Map<Long, Integer>) state.sentCountDelta)[DEMO_ME_ID] = ((state.sentCountDelta as Map<Long, Integer>)[DEMO_ME_ID] ?: 0) + 1
            List<Map> sent = state.sentKudos as List<Map>
            sent << [
                senderId: DEMO_ME_ID,
                receiverId: receiverId,
                message: message ?: null,
                ts: System.currentTimeMillis(),
            ]
            // Oldest first, so dropping from the front keeps the newest.
            if (sent.size() > MAX_SENT) state.sentKudos = sent.drop(sent.size() - MAX_SENT)
            session.demoState = state
            flash.message = "Kudos sent to ${SEED_NAME[receiverId].capitalize()}!"
        }
        redirect(action: 'list')
    }

    def updateFeeling() {
        String message = params.feeling?.trim()
        Map state = demoState
        Map<Long, String> overrides = state.feelingOverrides as Map<Long, String>
        if (message) {
            overrides[DEMO_ME_ID] = message
        } else {
            overrides[DEMO_ME_ID] = null
        }
        session.demoState = state
        redirect(action: 'list')
    }

    def reset() {
        Map state = demoState
        if (state.resetAts == null) state.resetAts = []
        List<Long> resets = state.resetAts as List<Long>
        resets << System.currentTimeMillis()
        if (resets.size() > MAX_RESETS) state.resetAts = resets.drop(resets.size() - MAX_RESETS)
        // Counts reset to 0 (since last reset) — but keep sentKudos so history is preserved
        ((Map) state.kudosCountDelta).clear()
        ((Map) state.sentCountDelta).clear()
        session.demoState = state
        flash.message = "All kudos have been reset."
        redirect(action: 'list')
    }
}
