package util

fun List<String>.toIntPair(): Pair<Int, Int> {
    require(this.size == 2) {"Given list does not have size 2, cannot create Int-Pair."}
    return Pair(this[0].toInt(), this[1].toInt())
}