package com.example.teta

fun JsonRGBString(red: Int, green: Int, blue: Int): String = """
    {
        "red": $red,
        "green": $green,
        "blue": $blue
    }
""".trimIndent()

fun JsonModeString(mode: Int): String = """
    {
        "mode": $mode
    }
""".trimIndent()