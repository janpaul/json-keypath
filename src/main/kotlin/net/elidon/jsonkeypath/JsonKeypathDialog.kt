package net.elidon.jsonkeypath

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JsonKeypathDialog(
    private val file: JsonFile,
    private val editor: Editor
) : DialogWrapper(editor.project) {

    private val project = editor.project!!
    private val history = KeypathHistory(project)
    private val allKeypaths = mutableListOf<KeypathEntry>()
    private val listModel = DefaultListModel<String>()
    private val list = JBList(listModel)
    private val searchField = JBTextField()

    data class KeypathEntry(val keypath: String, val offset: Int)

    init {
        title = "Go to JSON Keypath"
        buildKeypaths()
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(0, 8))
        panel.preferredSize = Dimension(500, 300)

        // Search field
        searchField.emptyText.text = "Type keypath, e.g. foo.bar.baz"
        panel.add(searchField, BorderLayout.NORTH)

        // List
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.visibleRowCount = 10
        refreshList("")
        if (listModel.size() > 0) list.selectedIndex = 0

        val scrollPane = JBScrollPane(list)
        panel.add(scrollPane, BorderLayout.CENTER)

        // Live filter while typing
        searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = refreshList(searchField.text)
            override fun removeUpdate(e: DocumentEvent) = refreshList(searchField.text)
            override fun changedUpdate(e: DocumentEvent) = refreshList(searchField.text)
        })

        // Keyboard navigation from search field into list
        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_DOWN -> {
                        if (listModel.size() > 0) {
                            list.requestFocus()
                            list.selectedIndex = 0
                        }
                    }
                    KeyEvent.VK_ENTER -> navigateToSelected()
                    KeyEvent.VK_ESCAPE -> doCancelAction()
                }
            }
        })

        // Enter in list navigates
        list.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> navigateToSelected()
                    KeyEvent.VK_ESCAPE -> doCancelAction()
                }
            }
        })

        // Double click navigates
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) navigateToSelected()
            }
        })

        return panel
    }

    override fun getPreferredFocusedComponent() = searchField

    private fun refreshList(filter: String) {
        listModel.clear()
        if (filter.isEmpty()) {
            // History bovenaan
            history.getAll().forEach { listModel.addElement("▸ $it") }
            // Dan alle keypaden
            allKeypaths.forEach { listModel.addElement(it.keypath) }
        } else {
            allKeypaths
                .filter { it.keypath.contains(filter, ignoreCase = true) }
                .forEach { listModel.addElement(it.keypath) }
        }
        if (listModel.size() > 0) list.selectedIndex = 0
    }

    private fun navigateToSelected() {
        val selected = list.selectedValue ?: return
        val keypath = selected.removePrefix("▸ ")
        val entry = allKeypaths.find { it.keypath == keypath } ?: return
        history.add(keypath)
        editor.caretModel.moveToOffset(entry.offset)
        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
        close(OK_EXIT_CODE)
    }

    private fun buildKeypaths() {
        val root = file.topLevelValue as? JsonObject ?: return
        collectKeypaths(root, "")
    }

    private fun collectKeypaths(obj: JsonObject, prefix: String) {
        for (property in obj.propertyList) {
            val keypath = if (prefix.isEmpty()) property.name else "$prefix.${property.name}"
            allKeypaths.add(KeypathEntry(keypath, property.textOffset))
            when (val value = property.value) {
                is JsonObject -> collectKeypaths(value, keypath)
                is JsonArray -> collectArrayKeypaths(value, keypath)
                else -> {}
            }
        }
    }

    private fun collectArrayKeypaths(array: JsonArray, prefix: String) {
        array.valueList.forEachIndexed { index, value ->
            val keypath = "$prefix.$index"
            allKeypaths.add(KeypathEntry(keypath, value.textOffset))
            when (value) {
                is JsonObject -> collectKeypaths(value, keypath)
                is JsonArray -> collectArrayKeypaths(value, keypath)
                else -> {}
            }
        }
    }
}