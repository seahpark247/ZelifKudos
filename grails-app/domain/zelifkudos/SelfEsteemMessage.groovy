package zelifkudos

class SelfEsteemMessage {

    int sortOrder
    String message

    static constraints = {
        sortOrder unique: true
        message maxSize: 500
    }

    static mapping = {
        sort 'sortOrder'
    }
}
