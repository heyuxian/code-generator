package me.javaroad.plugins.ui;

import java.awt.Desktop;
import java.io.IOException;
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
    private JTextPane textPane1;

    public HelpPanel() {
        textPane1.addHyperlinkListener(e -> {
            if(e.getEventType() == EventType.ACTIVATED) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        textPane1.setText("<div style=\"font-family: Consolas;\">\n"
            + "  You can use the following predefined variables in templates\n"
            + "  <ul style=\"list-style-type:none;\">\n"
            + "    <li><b>${clazz}</b> the selected class\n"
            + "      <ul style=\"list-style-type:none;\">\n"
            + "        <li><b>${name}</b> the class name</li>\n"
            + "        <li><b>${packageName}</b> the class name</li>\n"
            + "        <li><b>${fields}</b> the class fields\n"
            + "          <ul style=\"list-style-type:none;\">\n"
            + "            <li><b>${name}</b> the field name</li>\n"
            + "            <li><b>${type}</b> the field type</li>\n"
            + "          </ul>\n"
            + "        </li>\n"
            + "      </ul>\n"
            + "    </li>\n"
            + "    <li><b>${fn}</b> String tools\n"
            + "      <ul style=\"list-style-type:none;\">\n"
            + "        <li><b>${pluralize}</b> </li>\n"
            + "        <li><b>${singularize}</b> </li>\n"
            + "        <li><b>${decapitalize}</b> </li>\n"
            + "        <li><b>${capitalize}</b> </li>\n"
            + "        <li><b>${dcp}</b> decapitalize and pluralize</li>\n"
            + "      </ul>\n"
            + "    </li>\n"
            + "    <li><b>${BASE_PACKAGE}</b> user selected base package</li>\n"
            + "    <li><b>${USER}</b> current user system login name</li>\n"
            + "    <li><b>${YEAR}</b> current year</li>\n"
            + "    <li><b>${MONTH}</b> current month</li>\n"
            + "    <li><b>${DAY}</b> current day</li>\n"
            + "    <li><b>${DATE}</b> current system date</li>\n"
            + "    <li><b>${TIME}</b> current system time</li>\n"
            + "    <li><b>${DATE_TIME}</b> current system dateTime</li>\n"
            + "  </ul>\n"
            + "</div>"
            + "<a href=\"https://github.com/heyuxian/code-generator\">github</a>");
    }
}
