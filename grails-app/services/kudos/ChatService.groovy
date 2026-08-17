package kudos

import grails.gorm.transactions.Transactional

@Transactional
class ChatService {

    static final List<String> ANIMALS = [
        "Penguin", "Otter", "Fox", "Panda", "Koala",
        "Dolphin", "Owl", "Rabbit", "Hedgehog", "Sloth",
        "Raccoon", "Flamingo", "Seal", "Duckling", "Hamster",
        "Capybara", "Quokka", "Red Panda", "Axolotl", "Narwhal",
        "Alpaca", "Corgi", "Puffin", "Chinchilla", "Orca",
        "Chameleon", "Toucan", "Jellyfish", "Seahorse", "Firefly",
        "Meerkat", "Peacock", "Platypus", "Starfish", "Ladybug",
        "Hummingbird", "Crab", "Frog", "Parrot", "Turtle",
        "Bee", "Butterfly", "Kitten", "Puppy", "Sparrow",
        "Squirrel", "Deer", "Swan", "Pelican", "Octopus"
    ]

    static final List<String> COLORS = [
        "#FF5555", "#55FF55", "#5555FF", "#FFFF55", "#FF55FF",
        "#55FFFF", "#FFA500", "#FF69B4", "#00FF7F", "#00CED1",
        "#FFD700", "#7FFF00", "#FF6347", "#40E0D0", "#EE82EE",
        "#F0E68C", "#87CEEB", "#FFC0CB", "#98FB98", "#DDA0DD"
    ]

    AnimalNickname getOrAssignNickname(Long userId) {
        User user = User.get(userId)
        if (!user) return null

        AnimalNickname existing = AnimalNickname.findByUser(user)
        if (existing) return existing

        // Prefer an animal nobody currently holds. Nicknames are wiped nightly by
        // DailyNicknameResetJob, so "currently" means since the last midnight.
        List<String> taken = AnimalNickname.list()*.animalName
        List<String> available = ANIMALS - taken
        String animal = available ? available[new Random().nextInt(available.size())]
                                  : ANIMALS[new Random().nextInt(ANIMALS.size())]

        // Pick a color — if animal is duplicated, ensure different color
        List<String> takenColors = AnimalNickname.findAllByAnimalName(animal)*.colorHex
        List<String> availableColors = COLORS - takenColors
        String color = availableColors ? availableColors[new Random().nextInt(availableColors.size())]
                                       : COLORS[new Random().nextInt(COLORS.size())]

        new AnimalNickname(user: user, animalName: animal, colorHex: color).save(failOnError: true)
    }

    ChatMessage saveMessage(String content, Long userId = null, String nickname = null, String colorHex = null) {
        new ChatMessage(content: content.trim(), userId: userId, nickname: nickname, colorHex: colorHex).save(failOnError: true)
    }

    @Transactional(readOnly = true)
    List<ChatMessage> getRecentMessages(int max) {
        ChatMessage.executeQuery(
            "from ChatMessage cm order by cm.dateCreated asc",
            [max: max, offset: Math.max(0, ChatMessage.count() - max)]
        )
    }

    void deleteAllMessages() {
        ChatMessage.executeUpdate("delete from ChatMessage")
    }

    void deleteAllNicknames() {
        AnimalNickname.executeUpdate("delete from AnimalNickname")
    }
}
