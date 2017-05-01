import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.html.Option;
import java.awt.*;
import java.util.Optional;

/**
 * Created by jojoldu@gmail.com on 2017. 4. 24.
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

public class TextChanger extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        String message = getSelectedMessage(e);
        showPopup(message, e);
    }

    private void showPopup(String message, AnActionEvent e){

        JComponent jComponent = getCurrentComponent(e);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Point point = extractPoint(editor);

        if(jComponent != null && point != null){
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(message, MessageType.INFO, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(new RelativePoint(jComponent, point),
                            Balloon.Position.below);
        }
    }

    @Nullable
    private Point extractPoint(Editor editor) {
        Point point = null;

        if (editor != null) {
            point = editor.visualPositionToXY(makeSelectionPosition(editor));
        }

        return point;
    }

    private static final int LINE_INTERVAL = 1;

    private VisualPosition makeSelectionPosition(Editor editor) {
        VisualPosition start = editor.getSelectionModel().getSelectionStartPosition();
        VisualPosition end = editor.getSelectionModel().getSelectionEndPosition();
        int line = 0;
        int column = 0;

        if (end != null && start != null) {
            line = end.getLine() + LINE_INTERVAL;
            column = start.getColumn() + ((end.column - start.getColumn())/2);
        }

        return new VisualPosition(line, column);
    }

    private JComponent getCurrentComponent(AnActionEvent e){
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());

        if (editor != null) {
            return editor.getContentComponent();
        }

        return null;
    }

    private String getSelectedMessage(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        final SelectionModel selectionModel;

        if (editor != null) {
            selectionModel = editor.getSelectionModel();

            return selectionModel.getSelectedText();
        }

        return "";
    }

    private int getCurrentCursor(AnActionEvent e){
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());

        int offset = 0;
        if (editor != null) {
            offset = editor.getCaretModel().getOffset();
        }

        return offset;
    }


    private void showFromStatusBar(AnActionEvent e, String message) {
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(PlatformDataKeys.PROJECT.getData(e.getDataContext()));

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

}
