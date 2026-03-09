package net.elidon.jsonkeypath

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class GoToJsonKeypathAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? JsonFile ?: return
        JsonKeypathDialog(psiFile, editor).show()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.PSI_FILE) is JsonFile
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}