package plugins;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.datacrow.client.console.windows.security.ChangePasswordDialog;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcTemplate;
import org.datacrow.core.plugin.Plugin;
import org.datacrow.core.resources.DcResources;

public class ChangePassword extends Plugin {

	private static final long serialVersionUID = 2635330634177063234L;

	public ChangePassword(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
         ChangePasswordDialog dlg = new ChangePasswordDialog();
         dlg.setVisible(true);
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoSettings16;
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblChangePassword");
    } 
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpChangePassword");
    }        
}
