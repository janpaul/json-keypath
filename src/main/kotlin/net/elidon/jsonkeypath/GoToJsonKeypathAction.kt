package net.elidon.jsonkeypath

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class GoToJsonKeypathAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) as? JsonFile ?: return
        JsonKeypathDialog(psiFile, editor).show()
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = event.getData(CommonDataKeys.PSI_FILE) is JsonFile
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}