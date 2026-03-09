package net.elidon.jsonkeypath

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

class KeypathHistory(private val project: Project) {

    companion object {
        private const val KEY = "net.elidon.jsonkeypath.history"
        private const val MAX_SIZE = 10
        private const val SEPARATOR = "|||"
    }

    fun add(keypath: String) {
        val current = getAll().toMutableList()
        current.remove(keypath) // de-duplicate
        current.add(0, keypath) // most recent item at the top
        val trimmed = current.take(MAX_SIZE)
        PropertiesComponent.getInstance(project)
            .setValue(KEY, trimmed.joinToString(SEPARATOR))
    }

    fun getAll(): List<String> {
        val raw = PropertiesComponent.getInstance(project).getValue(KEY) ?: return emptyList()
        if (raw.isEmpty()) return emptyList()
        return raw.split(SEPARATOR)
    }
}