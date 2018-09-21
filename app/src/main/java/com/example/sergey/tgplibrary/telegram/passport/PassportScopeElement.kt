package com.example.sergey.tgplibrary.telegram.passport

interface PassportScopeElement {
    fun toJson(): Any?
    fun validate()
}