package plugins;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.datacrow.client.console.GUI;
import org.datacrow.core.DcConfig;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.data.DataFilter;
import org.datacrow.core.modules.DcModule;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcTemplate;
import org.datacrow.core.plugin.Plugin;
import org.datacrow.core.resources.DcResources;
import org.datacrow.core.server.Connector;

public class UndoFilter extends Plugin {
    
	private static final long serialVersionUID = -1189380365258964567L;

	public UndoFilter(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
    public void actionPerformed(ActionEvent ae) {
        GUI.getInstance().getMainFrame().clearQuickFilterBar();
        DcModule m = getModule();
        
        Connector connector = DcConfig.getInstance().getConnector();
        GUI.getInstance().getSearchView(getModuleIdx()).add(
        		connector.getKeys(new DataFilter(m.getIndex())));
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoClose;
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
    }    
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblUndoSearch");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpUndoSearch");
    }     
}
