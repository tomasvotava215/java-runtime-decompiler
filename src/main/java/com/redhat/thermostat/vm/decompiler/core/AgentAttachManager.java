package com.redhat.thermostat.vm.decompiler.core;

import com.redhat.thermostat.vm.decompiler.data.VmManager;
import workers.VmId;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Attach manager for agent contains utility methods and information about 
 * attach.
 */
public class AgentAttachManager {
 
    //private static final Logger logger = LoggingUtils.getLogger(AgentAttachManager.class); 
    private AgentLoader loader;
    private VmManager vmManager;

      
    public AgentAttachManager(VmManager vmManager){
        this.vmManager = vmManager;
        
    }
    
     void setAttacher(AgentLoader attacher) {
        this.loader = attacher;
    }

    void setVmManager(VmManager vmManager) {
        this.vmManager = vmManager;
    }
  

    VmDecompilerStatus attachAgentToVm(VmId vmId, int vmPid)  {
        //logger.fine("Attaching agent to VM '" + vmPid + "'");
         int attachedPort = AgentLoader.INVALID_PORT;
        try {
            attachedPort = loader.attach(vmId.get(), vmPid);
        } catch (Exception ex) {
            Logger.getLogger(AgentAttachManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (attachedPort == AgentLoader.INVALID_PORT) {
            //logger.warning("Failed to attach agent for VM '" + vmPid);
            return null;
        }
        VmDecompilerStatus status = new VmDecompilerStatus();
        status.setListenPort(attachedPort);
        status.setVmId(vmId.get());
        status.setTimeStamp(System.currentTimeMillis());
        vmManager.replaceVmDecompilerStatus(vmId, status);
        return status;
    }
}

    

