package consumer

import kotlin.test.Test
import kotlin.test.assertTrue

class StrumMacrosConsumerTest {
    @Test
    fun publicTokenStreamCountCrossesPublicationBoundary() {
        val output = countTokens().toString()
        assertTrue(output.contains("EnumCount"))
        assertTrue(output.contains("COUNT"))
        assertTrue(output.contains("3"))
    }

    @Test
    fun variantNamesCrossPublicationBoundary() {
        val output = nameTokens().toString()
        assertTrue(output.contains("VariantNames"))
        for (name in listOf("Red", "Green", "Blue")) assertTrue(output.contains(name))
    }

    @Test
    fun parserTokensCrossPublicationBoundary() {
        val output = parserTokens().toString()
        assertTrue(output.contains("FromStr"))
        assertTrue(output.contains("from_str"))
    }
}
