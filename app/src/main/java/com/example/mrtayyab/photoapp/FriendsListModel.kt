package com.example.mrtayyab.photoapp

/**
 * Created by MrTayyab on 3/3/2018.
 */
class FriendsListModel {


     var name : String ? = null
    var status : String ? = null
    var image : String? = null

    constructor(){

    }
    constructor(name: String?, statud: String?, image: String?) {
        this.name = name
        this.status = statud
        this.image = image
    }
}