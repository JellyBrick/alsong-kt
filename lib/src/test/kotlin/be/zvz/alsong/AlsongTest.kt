package be.zvz.alsong

import kotlin.test.Test
import kotlin.test.assertTrue

class AlsongTest {
    @Test fun testGetResembleLyricList() {
        val classUnderTest = Alsong()
        val lyricList = classUnderTest.getResembleLyricList(
            artist = "IU",
            title = "이런 엔딩"
        )
        assertTrue(lyricList.isNotEmpty(), "lyricList is empty")
        println(
            lyricList
        )
    }

    @Test fun testGetLyricById() {
        val classUnderTest = Alsong()
        val lyricList = classUnderTest.getResembleLyricList(
            artist = "IU",
            title = "Love Poem"
        )
        assertTrue(lyricList.isNotEmpty(), "lyricList is empty")
        println(
            classUnderTest.getLyricById(lyricList.first().lyricId)
        )
    }

    @Test fun testGetLyricByHash() {
        val classUnderTest = Alsong()
        println(
            classUnderTest.getLyricByHash("6ab8bfe86f2755774dc8986e8bdff2f0")
        )
    }

    @Test fun testGetLyricByMurekaId() {
        val classUnderTest = Alsong()
        println(
            classUnderTest.getLyricByMurekaId(101547527)
        )
    }
}
