package uk.co.barbuzz.appwidget.configure

fun List<String>.toPackageMatchers(): List<String> {
    return flatMap {
        var currentPackage = it

        it.split('.')
            .foldRight(setOf(it)) { part, acc ->
                currentPackage = currentPackage.removeSuffix(".$part")
                acc + "$currentPackage.*"
            }
            .reversed()
    }.distinct()
}
