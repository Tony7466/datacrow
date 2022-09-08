package plugins;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.datacrow.client.console.windows.charts.ChartsDialog;
import org.datacrow.core.IconLibrary;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcTemplate;
import org.datacrow.core.plugin.Plugin;
import org.datacrow.core.resources.DcResources;

public class Charts extends Plugin {

	private static final long serialVersionUID = 286815964886972861L;

	public Charts(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
		return !DcModules.getCurrent().isAbstract();
	}    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        ChartsDialog dlg = new ChartsDialog();
        dlg.setVisible(true);
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return IconLibrary._icoChart;        
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblCharts");
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpCharts");
    }
}
