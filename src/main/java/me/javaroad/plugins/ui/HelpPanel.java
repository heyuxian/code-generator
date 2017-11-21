package me.javaroad.plugins.ui;

import com.intellij.ide.BrowserUtil;
import java.net.URISyntaxException;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent.EventType;
import lombok.Getter;

/**
 * @author heyx
 */
public class HelpPanel {

    @Getter
    private JPanel mainPanel;
    private JTextPane infoPanel;
    private JTextPane supportPanel;

    public HelpPanel() {
        infoPanel.setText(""
            + "**********************************************************************\n"
            + "** You can use the following predefined variables in templates\n"
            + "**********************************************************************\n"
            + "**\n"
            + "**  - ${clazz} the selected class\n"
            + "**      - ${name} the class name\n"
            + "**      - ${packageName} the package name\n"
            + "**      - ${fields} the class fields\n"
            + "**          - ${name} the field name\n"
            + "**          - ${type} the field type\n"
            + "**  - ${fn} String tools\n"
            + "**      - ${fn.pluralize()}    ----- ${fn.pluralize(\"Category\")} = Categories\n"
            + "**      - ${fn.singularize()}  ----- ${fn.singularize(\"Categories\")} = Category\n"
            + "**      - ${fn.decapitalize()} ----- ${fn.decapitalize(\"Category\")} = category\n"
            + "**      - ${fn.capitalize()}   ----- ${fn.capitalize(\"category\")} = Category\n"
            + "**      - ${fn.dcp()} decapitalize and pluralize ------- ${fn.dcp(\"Category\")} = categories\n"
            + "**  - ${BASE_PACKAGE} user selected base package\n"
            + "**  - ${USER} current user login name\n"
            + "**  - ${YEAR} current year\n"
            + "**  - ${MONTH} current month\n"
            + "**  - ${DAY} current day\n"
            + "**  - ${DATE} current system date\n"
            + "**  - ${TIME} current system time\n"
            + "**  - ${DATE_TIME} current system dateTime\n"
            + "**\n"
            + "**********************************************************************");

        supportPanel.addHyperlinkListener(e -> {
            if(e.getEventType() == EventType.ACTIVATED) {
                try {
                    BrowserUtil.browse(e.getURL().toURI());
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        supportPanel.setText(""
            + "1. <a href=\"http://velocity.apache.org/engine/1.7/user-guide.html\">Apache Velocity</a> is used<br/>"
            + "2. Source Code: <a href=\"https://github.com/heyuxian/code-generator\">github</a><br/>"
            + "3. Issues: <a href=\"https://github.com/heyuxian/code-generator/issues/new\">new issue</a>");
    }
}
