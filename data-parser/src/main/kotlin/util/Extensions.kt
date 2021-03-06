package util

/**
 * Returns an int pair based on this list.
 * Must comprise at least two elements, non digit characters are dropped and additional elements
 */
fun List<String>.toIntPair(): Pair<Int, Int> {
    require(this.size >= 2) {"Given list does not have size 2, cannot create Int-Pair."}
    return Pair(this[0].filter { it.isDigit() }.toInt(), this[1].toInt())
}