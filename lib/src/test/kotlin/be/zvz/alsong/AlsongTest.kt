package be.zvz.alsong

import kotlin.test.Test

class AlsongTest {
    @Test fun testGetResembleLyricList() {
        val classUnderTest = Alsong()
        println(
            classUnderTest.getResembleLyricList(
                artist = "IU",
                title = "이런 엔딩"
            )
        )
    }
}
