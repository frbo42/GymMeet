package org.fb.gym.meet

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform