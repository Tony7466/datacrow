package plugins;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.datacrow.client.console.GUI;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

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
