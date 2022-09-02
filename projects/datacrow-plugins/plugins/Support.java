package plugins;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import net.datacrow.console.windows.SupportDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;

public class Support extends Plugin {

	private static final long serialVersionUID = 1L;

	public Support(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
        SupportDialog dlg = new SupportDialog();
        dlg.setVisible(true);
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoHelp;        
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblCreateSupportPackage");
    }
}
