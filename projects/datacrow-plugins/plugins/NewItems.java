package plugins;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import net.datacrow.console.windows.CreateMultipleItemsDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

public class NewItems extends Plugin {

	private static final long serialVersionUID = 4988354026124603222L;

	public NewItems(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
        super(dco, template, viewIdx, moduleIdx, viewType);
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
    
    @Override
    public boolean isAuthorizable() {
        return true;
    }
    
    @Override
	public boolean isEnabled() {
    	Connector connector = DcConfig.getInstance().getConnector();
		return  getModule().hasInsertView() && 
		        connector.getUser().isEditingAllowed(DcModules.getCurrent());
	}    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        CreateMultipleItemsDialog dlg = new CreateMultipleItemsDialog(getModuleIdx());
        dlg.setVisible(true);
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoItemsNew;        
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblAddMultiple");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpCreateMultiple");
    }
}
