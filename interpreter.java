import java.io.File;
import java.util.HashMap;

import com.microsoft.z3.Context;

import fr.lip6.move.pnml.framework.hlapi.HLAPIRootClass;
import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InternalException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidFileException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidFileTypeException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.ptnet.Page;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;

public class interpreter {

	static Boolean interpret(String Filepath, int choice){
		HashMap<String, String> cfg = new HashMap<String, String>();
	     Context ctx = new Context(cfg);
	     cfg.put("model", "true");
		HLAPIRootClass rc = null;
	     try {
				File f = new File(Filepath);
				PNMLUtils.checkIsPnmlFile(f);
	            rc = PNMLUtils.importPnmlDocument(f, false);
				PNMLUtils.isCoreModelDocument(rc);
		    } catch (ImportException | InvalidIDException e) {
		        e.printStackTrace();
		        System.exit(1);
		    } catch (InvalidFileTypeException e) {
				e.printStackTrace();
			} catch (InvalidFileException e) {
				e.printStackTrace();
			} catch (InternalException e) {
				e.printStackTrace();
			}
		if (!PNMLUtils.isPTNetDocument(rc)) {
			System.exit(1);
		}

		//convert the PTNetDocument to a PetriNetDocHLAPI
		 PetriNetDocHLAPI pnd = new PetriNetDocHLAPI();
			
		 try {
			pnd = (PetriNetDocHLAPI) rc;
		 } catch (Exception e) {
			 System.out.println(e.toString());
			 System.exit(1);
		 }
		 
		 System.out.println(pnd.getNets().get(0).getPages().get(0).getName());
		 for(PetriNet pn : pnd.getNets()) {
			 for( Page p : pn.getPages()) {
				 if(!interpretCheck.check((p), ctx, choice)) {
					 return false;
				 }
				 
			 }
		 }
		 return true;
	}
}
