package com.example.furnitureapp.data

data class Categories(
    val name:String,
    val products:Int?,
    val rank:Int,
    val image:String
){
    constructor() : this("",0,0,"")
}