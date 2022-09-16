package plugins;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.datacrow.client.console.GUI;
import org.datacrow.client.console.MainFrame;
import org.datacrow.client.console.windows.DcDialog;
import org.datacrow.client.console.windows.DcFrame;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcTemplate;
import org.datacrow.core.plugin.Plugin;
import org.datacrow.core.resources.DcResources;

public class CloseWindow extends Plugin {

	private static final long serialVersionUID = -4302814739124486217L;

	public CloseWindow(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
    
    @Override
    public boolean isAuthorizable() {
        return false;
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object window = e.getSource();
        if (window instanceof JComponent) {
            while (!(window instanceof JDialog) && !(window instanceof JFrame)) {
                window = ((Component) window).getParent();
                if (window == null) break;
            }
        }
        
        if (window == null && !(GUI.getInstance().getRootFrame() instanceof MainFrame))
            window = GUI.getInstance().getRootFrame();
        
        if (window instanceof DcFrame)
            ((DcFrame) window).close();
        else if (window instanceof DcDialog)
            ((DcDialog) window).close();
    }
    
    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoClose;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblClose");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpClose");
    }     
}