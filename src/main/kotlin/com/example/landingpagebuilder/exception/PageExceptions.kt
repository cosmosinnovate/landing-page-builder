package com.example.landingpagebuilder.exception

class PageNotFoundException(message: String) : RuntimeException(message)

class PageSlugConflictException(message: String) : RuntimeException(message)

class InvalidPageException(message: String) : RuntimeException(message)

