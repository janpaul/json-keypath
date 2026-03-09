package net.elidon.jsonkeypath

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import java.awt.Component
import java.awt.event.MouseEvent

class JsonKeypathWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = "JsonKeypathWidget"
    override fun getDisplayName() = "JSON Keypath"
    override fun createWidget(project: Project) = JsonKeypathWidget(project)
    override fun disposeWidget(widget: StatusBarWidget) = widget.dispose()
    override fun isAvailable(project: Project) = true
    override fun canBeEnabledOn(statusBar: StatusBar) = true
}

class JsonKeypathWidget(private val project: Project) :
    StatusBarWidget, StatusBarWidget.TextPresentation {

    private var statusBar: StatusBar? = null
    private var currentKeypath: String = ""
    private var caretListener: CaretListener? = null

    override fun ID() = "JsonKeypathWidget"
    override fun getPresentation() = this
    override fun getText() = currentKeypath
    override fun getTooltipText() = "JSON Keypath at cursor"
    override fun getAlignment() = Component.LEFT_ALIGNMENT
    override fun getClickConsumer() = null

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar

        val listener = object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) = onCaretMoved(event)
        }
        caretListener = listener

        // Listen to editor tab switches
        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    updateFromCurrentEditor()
                }
            }
        )

        updateFromCurrentEditor()
    }

    private fun onCaretMoved(event: CaretEvent) {
        val editor = event.editor
        val psiFile = PsiDocumentManager.getInstance(project)
            .getPsiFile(editor.document) as? JsonFile ?: run {
            currentKeypath = ""
            statusBar?.updateWidget(ID())
            return
        }
        val offset = event.caret?.offset ?: return
        currentKeypath = resolveKeypath(psiFile, offset)
        statusBar?.updateWidget(ID())
    }

    private fun updateFromCurrentEditor() {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: run {
            currentKeypath = ""
            statusBar?.updateWidget(ID())
            return
        }

        // Attach caret listener to current editor
        caretListener?.let { editor.caretModel.addCaretListener(it) }

        val psiFile = PsiDocumentManager.getInstance(project)
            .getPsiFile(editor.document) as? JsonFile ?: run {
            currentKeypath = ""
            statusBar?.updateWidget(ID())
            return
        }

        currentKeypath = resolveKeypath(psiFile, editor.caretModel.offset)
        statusBar?.updateWidget(ID())
    }

    private fun resolveKeypath(file: JsonFile, offset: Int): String {
        val element = file.findElementAt(offset) ?: return ""
        return buildKeypath(element)
    }

    private fun buildKeypath(element: PsiElement): String {
        val parts = mutableListOf<String>()
        var current: PsiElement? = element

        while (current != null && current !is JsonFile) {
            when (val parent = current.parent) {
                is JsonProperty -> {
                    if (current == parent.nameElement || current == parent.value) {
                        parts.add(0, parent.name)
                    }
                    current = parent.parent
                }
                is JsonArray -> {
                    val index = parent.valueList.indexOf(current)
                    if (index >= 0) parts.add(0, index.toString())
                    current = parent.parent
                }
                is JsonObject -> current = parent.parent
                else -> current = parent
            }
        }

        return parts.joinToString(".")
    }

    override fun dispose() {
        caretListener?.let {
            FileEditorManager.getInstance(project).selectedTextEditor
                ?.caretModel?.removeCaretListener(it)
        }
        statusBar = null
    }
}