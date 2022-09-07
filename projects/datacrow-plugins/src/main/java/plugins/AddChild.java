package plugins;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.datacrow.client.console.GUI;
import net.datacrow.client.console.views.View;
import net.datacrow.client.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.console.IView;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.plugin.Plugin;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

public class AddChild extends Plugin {

	private static final long serialVersionUID = 1647446110383897403L;

	public AddChild(DcObject dco, DcTemplate template, int viewIdx, int moduleIdx, int viewType) {
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
	public boolean isEnabled() {
    	Connector connector = DcConfig.getInstance().getConnector();
		return connector.getUser().isEditingAllowed(getModule());
	}    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        DcModule module = getModule().getChild();
        IView view = 
                getViewType() == _VIEWTYPE_SEARCH ? 
                		GUI.getInstance().getSearchView(module.getIndex()).getCurrent() : 
                		GUI.getInstance().getInsertView(module.getIndex()).getCurrent();
            
        String parentID = view.getParentID();
        if (parentID != null) {
            DcObject dco = module.getItem();
            dco.setIDs();
            dco.setValue(dco.getParentReferenceFieldIndex(), parentID);
            if (view.getType() == View._TYPE_SEARCH) {
                ItemForm frm = new ItemForm(false, false, dco, true);
                frm.setVisible(true);
            } else {
                List<DcObject> children = new ArrayList<DcObject>();
                children.add(dco);
                view.add(children);
                view.loadChildren();
            }
        } else {
            GUI.getInstance().displayWarningMessage("msgAddSelectParent");
        }
    }
    
    @Override
    public boolean isSystemPlugin() {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return getModule().getIcon16();        
    }

    @Override
    public String getLabel() {
        return DcResources.getText("lblAddChild", getModule().getObjectName());
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("tpAddChild");
    }
}
