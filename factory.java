import fr.lip6.move.pnml.pnmlcoremodel.Name;
import fr.lip6.move.pnml.pnmlcoremodel.Page;
import fr.lip6.move.pnml.pnmlcoremodel.PetriNet;
import fr.lip6.move.pnml.pnmlcoremodel.PetriNetDoc;
import fr.lip6.move.pnml.pnmlcoremodel.Place;
import fr.lip6.move.pnml.ptnet.PTMarking;
import fr.lip6.move.pnml.ptnet.PtnetFactory;
import fr.lip6.move.pnml.ptnet.impl.PetriNetDocImpl;
import fr.lip6.move.pnml.ptnet.impl.PetriNetImpl;
import fr.lip6.move.pnml.ptnet.impl.PtnetFactoryImpl;

public class factory {

	public static void main(String[] args) {
		long initial = 1;
		long uninitial = 0;
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

        PTMarking mark = fac.createPTMarking();

        fr.lip6.move.pnml.ptnet.PetriNet pn =  fac.createPetriNet();
        pn.setId("simpleNet");

        PetriNetDocImpl doc = (PetriNetDocImpl) fac.createPetriNetDoc();

        doc.getNets().add((fr.lip6.move.pnml.ptnet.PetriNet) pn);

        fr.lip6.move.pnml.ptnet.Page p  = fac.createPage();
        p.setId("simplePage");

        pn.getPages().add(p);

        fr.lip6.move.pnml.ptnet.Name name = fac.createName();
        
        //first place

        fr.lip6.move.pnml.ptnet.Place place1 = fac.createPlace();

        name.setText("one");

        place1.setName(name);
        place1.setId("1");
        
        mark.setText(initial);
        place1.setInitialMarking(mark);
        System.out.println(place1.getInitialMarking());

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("one");

        place2.setName(name);
        place2.setId("2");
        
        mark.setText(uninitial);
        place2.setInitialMarking(mark);
        System.out.println(place2.getInitialMarking());

        p.getObjects().add(place2);
        
        //an arc between
        
        fr.lip6.move.pnml.ptnet.Arc arcA = fac.createArc();
        
        arcA.setTarget(place2);
        arcA.setSource(place1);
        arcA.setId("A");
        p.getObjects().add(arcA);
        
        //laat even zien
        
        System.out.println(p.toString());
        System.out.println(p.getObjects().toString());
        
	}
	

}
