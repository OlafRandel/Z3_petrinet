import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.microsoft.z3.Context;

import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.BadFileFormatException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.InvocationFailedException;
import fr.lip6.move.pnml.framework.utils.exception.OCLValidationFailed;
import fr.lip6.move.pnml.framework.utils.exception.OtherException;
import fr.lip6.move.pnml.framework.utils.exception.UnhandledNetType;
import fr.lip6.move.pnml.framework.utils.exception.ValidationFailedException;
import fr.lip6.move.pnml.pnmlcoremodel.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.PTMarking;
import fr.lip6.move.pnml.ptnet.PtnetFactory;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.impl.PetriNetDocImpl;
import fr.lip6.move.pnml.ptnet.impl.PtnetFactoryImpl;

public class customTests {
	
	long placeIsFull = 1;
	long placeIsEmpty = 0;
	Context ctx = null;
	
	@org.junit.Test
	public void simpleTest() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //a transition between
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place2); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);
        
        
       
        try {
        	System.out.println("simple test");
        	interpretCheck.check(p,ctx, 0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}

	@org.junit.Test
	public void run_interpreter() {
		try {
			System.out.println("inlees test");
			interpreter.interpret("example.pnml", 0);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@org.junit.Test
	public void simpleTest2() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsFull);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //a transition between
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place2); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);
        
        
       
        try {
        	System.out.println("simple test 2");
        	interpretCheck.check(p,ctx, 0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}

	@org.junit.Test
	public void unboundedTest() {
		

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name);
        place2.setId("two");

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsFull);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);

        //third place
        
        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText((long) 0);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //first transition
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place3); //to place3
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);

        //second transition
        
        fr.lip6.move.pnml.ptnet.Transition TranB = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcB1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB2 = fac.createArc();
        
        TranB.setId("TB");
        name.setText("B");
        TranB.setName(name);
        
        arcB1.setTarget(TranB); //the transition connects from place2
        arcB1.setSource(place2);
        arcB1.setId("B1");
        
        arcB2.setTarget(place3); //to place3
        arcB2.setSource(TranB);
        arcB2.setId("B2");
        
        p.getObjects().add(arcB1);
        p.getObjects().add(arcB2);
        p.getObjects().add(TranB);

       
        
        try {
        	System.out.println("unbounded test");
        	interpretCheck.check(p,ctx, 0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void LivenessTest() {
		

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name);
        place2.setId("two");

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsFull);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);

        //third place
        
        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsEmpty);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //first transition
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA3 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA4 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place3); //to place3
        arcA2.setSource(TranA);
        arcA2.setId("A2");

        arcA3.setTarget(TranA); //and both from and to place2
        arcA3.setSource(place2);
        arcA3.setId("A1");
        
        arcA4.setTarget(place2);
        arcA4.setSource(TranA);
        arcA4.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(arcA3);
        p.getObjects().add(arcA4);
        p.getObjects().add(TranA);
        
       
        try {
        	System.out.println("liveness test");
        	interpretCheck.check(p,ctx, 1);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void LivenessTest2() {
		

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name);
        place2.setId("two");

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);

        //third place
        
        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsEmpty);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //first transition
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA3 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA4 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place3); //to place3
        arcA2.setSource(TranA);
        arcA2.setId("A2");

        arcA3.setTarget(TranA); //and both from and to place2
        arcA3.setSource(place2);
        arcA3.setId("A1");
        
        arcA4.setTarget(place2);
        arcA4.setSource(TranA);
        arcA4.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(arcA3);
        p.getObjects().add(arcA4);
        p.getObjects().add(TranA);
        
       
        try {
        	System.out.println("liveness test 2");
        	interpretCheck.check(p,ctx, 1);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void complexTest() {
		

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name);
        place2.setId("two");

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsFull);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);

        //third place
        
        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsFull);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //fourth place
        
        fr.lip6.move.pnml.ptnet.Place place4 = fac.createPlace();

        name.setText("four");

        place4.setName(name);
        place4.setId("four");

        PTMarking mark4 = fac.createPTMarking();
        mark4.setText(placeIsEmpty);
        place4.setInitialMarking(mark4);

        p.getObjects().add(place4);
        
        //fifth place
        
        fr.lip6.move.pnml.ptnet.Place place5 = fac.createPlace();

        name.setText("five");

        place5.setName(name);
        place5.setId("five");

        PTMarking mark5 = fac.createPTMarking();
        mark5.setText(placeIsEmpty);
        place5.setInitialMarking(mark5);

        p.getObjects().add(place5);
        
        
        //first Transition
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA3 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(TranA); //and from place2
        arcA2.setSource(place2);
        arcA2.setId("A2");

        arcA3.setTarget(place4); //to place4
        arcA3.setSource(TranA);
        arcA3.setId("A3");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(arcA3);
        p.getObjects().add(TranA);
        

        //second transition
        
        fr.lip6.move.pnml.ptnet.Transition TranB = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcB1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB3 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB4 = fac.createArc();
        
        TranB.setId("TB");
        name.setText("B");
        TranB.setName(name);
        
        arcB1.setTarget(TranB); //the transition connects from place2
        arcB1.setSource(place2);
        arcB1.setId("B1");
        
        arcB2.setTarget(TranB); //and from place3
        arcB2.setSource(place3);
        arcB2.setId("B2");

        arcB3.setTarget(place2); //to place2
        arcB3.setSource(TranB);
        arcB3.setId("B3");
        
        arcB4.setTarget(place5); //and to place5
        arcB4.setSource(TranB);
        arcB4.setId("B4");
        
        p.getObjects().add(arcB1);
        p.getObjects().add(arcB2);
        p.getObjects().add(arcB3);
        p.getObjects().add(arcB4);
        p.getObjects().add(TranB);
        
        //third transition
        
        fr.lip6.move.pnml.ptnet.Transition TranC = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcC1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcC2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcC3 = fac.createArc();
        
        TranC.setId("TC");
        name.setText("C");
        TranC.setName(name);
        
        arcC1.setTarget(TranC); //the transition connects from place2
        arcC1.setSource(place2);
        arcC1.setId("C1");
        
        arcC2.setTarget(TranC); //and from place5
        arcC2.setSource(place5);
        arcC2.setId("C2");

        arcC3.setTarget(place4); //to place4
        arcC3.setSource(TranC);
        arcC3.setId("C3");
        
        p.getObjects().add(arcC1);
        p.getObjects().add(arcC2);
        p.getObjects().add(arcC3);
        p.getObjects().add(TranC);
       
        
        try {
        	System.out.println("complex test");
        	interpretCheck.check(p,ctx, 0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void longTest() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //third place

        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");
        

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsFull);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //fourth place
        
        fr.lip6.move.pnml.ptnet.Place place4 = fac.createPlace();

        name.setText("four");

        place4.setName(name); 
        place4.setId("four");
        

        PTMarking mark4 = fac.createPTMarking();
        mark4.setText(placeIsEmpty);
        place4.setInitialMarking(mark4);

        p.getObjects().add(place4);
        
        
        //first transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place2); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);
        

        
        //second transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranB = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcB1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB2 = fac.createArc();
        
        TranB.setId("TB");
        name.setText("B");
        TranB.setName(name);
        
        arcB1.setTarget(TranB); //the transition connects from place1
        arcB1.setSource(place2);
        arcB1.setId("B1");
        
        arcB2.setTarget(place3); //to place2
        arcB2.setSource(TranB);
        arcB2.setId("B2");
        
        p.getObjects().add(arcB1);
        p.getObjects().add(arcB2);
        p.getObjects().add(TranB);
        
        //third transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranC = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcC1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcC2 = fac.createArc();
        
        TranC.setId("TC");
        name.setText("C");
        TranC.setName(name);
        
        arcC1.setTarget(TranC); //the transition connects from place1
        arcC1.setSource(place3);
        arcC1.setId("C1");
        
        arcC2.setTarget(place4); //to place2
        arcC2.setSource(TranC);
        arcC2.setId("C2");
        
        p.getObjects().add(arcC1);
        p.getObjects().add(arcC2);
        p.getObjects().add(TranC);
       
        try {
        	System.out.println("long test");
        	interpretCheck.check(p,ctx, 0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void loopTest() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //a transition between
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place2); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);
        
        //a transition back
        
        
        fr.lip6.move.pnml.ptnet.Transition TranB = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcB1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB2 = fac.createArc();
        
        TranB.setId("TB");
        name.setText("B");
        TranB.setName(name);
        
        arcB1.setTarget(TranB); //the transition connects from place1
        arcB1.setSource(place2);
        arcB1.setId("B1");
        
        arcB2.setTarget(place1); //to place2
        arcB2.setSource(TranB);
        arcB2.setId("B2");
        
        p.getObjects().add(arcB1);
        p.getObjects().add(arcB2);
        p.getObjects().add(TranB);
        
        
       
        try {
        	System.out.println("loop test");
        	interpretCheck.check(p,ctx,0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void branchTest() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //third place

        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");
        

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsFull);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //fourth place
        
        fr.lip6.move.pnml.ptnet.Place place4 = fac.createPlace();

        name.setText("four");

        place4.setName(name); 
        place4.setId("four");
        

        PTMarking mark4 = fac.createPTMarking();
        mark4.setText(placeIsEmpty);
        place4.setInitialMarking(mark4);

        p.getObjects().add(place4);
        
        //fifth place
        
        fr.lip6.move.pnml.ptnet.Place place5 = fac.createPlace();

        name.setText("five");

        place5.setName(name); 
        place5.setId("five");
        

        PTMarking mark5 = fac.createPTMarking();
        mark5.setText(placeIsEmpty);
        place5.setInitialMarking(mark5);

        p.getObjects().add(place5);
        
        
        //first transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA); //the transition connects from place1
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place2); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(TranA);
        

        
        //second transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranB = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcB1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcB2 = fac.createArc();
        
        TranB.setId("TB");
        name.setText("B");
        TranB.setName(name);
        
        arcB1.setTarget(TranB); //the transition connects from place1
        arcB1.setSource(place2);
        arcB1.setId("B1");
        
        arcB2.setTarget(place3); //to place2
        arcB2.setSource(TranB);
        arcB2.setId("B2");
        
        p.getObjects().add(arcB1);
        p.getObjects().add(arcB2);
        p.getObjects().add(TranB);
        
        //third transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranC = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcC1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcC2 = fac.createArc();
        
        TranC.setId("TC");
        name.setText("C");
        TranC.setName(name);
        
        arcC1.setTarget(TranC); //the transition connects from place1
        arcC1.setSource(place1);
        arcC1.setId("C1");
        
        arcC2.setTarget(place4); //to place2
        arcC2.setSource(TranC);
        arcC2.setId("C2");
        
        p.getObjects().add(arcC1);
        p.getObjects().add(arcC2);
        p.getObjects().add(TranC);
        
        //fourth transition
        
        
        fr.lip6.move.pnml.ptnet.Transition TranD = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcD1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcD2 = fac.createArc();
        
        TranD.setId("TD");
        name.setText("D");
        TranD.setName(name);
        
        arcD1.setTarget(TranD); //the transition connects from place1
        arcD1.setSource(place4);
        arcD1.setId("D1");
        
        arcD2.setTarget(place5); //to place2
        arcD2.setSource(TranD);
        arcD2.setId("D2");
        
        p.getObjects().add(arcD1);
        p.getObjects().add(arcD2);
        p.getObjects().add(TranD);
       
        try {
        	System.out.println("branch test");
        	interpretCheck.check(p,ctx,0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	@org.junit.Test
	public void starfishTest() {

		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
		
		
		PtnetFactory fac;
		fac = PtnetFactoryImpl.eINSTANCE;

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
        place1.setId("one");
        

        PTMarking mark1 = fac.createPTMarking();
        mark1.setText(placeIsFull);
        place1.setInitialMarking(mark1);

        p.getObjects().add(place1);
        
        //second place
        
        fr.lip6.move.pnml.ptnet.Place place2 = fac.createPlace();

        name.setText("two");

        place2.setName(name); 
        place2.setId("two");
        

        PTMarking mark2 = fac.createPTMarking();
        mark2.setText(placeIsEmpty);
        place2.setInitialMarking(mark2);

        p.getObjects().add(place2);
        
        //third place

        fr.lip6.move.pnml.ptnet.Place place3 = fac.createPlace();

        name.setText("three");

        place3.setName(name);
        place3.setId("three");
        

        PTMarking mark3 = fac.createPTMarking();
        mark3.setText(placeIsFull);
        place3.setInitialMarking(mark3);

        p.getObjects().add(place3);
        
        //fourth place
        
        fr.lip6.move.pnml.ptnet.Place place4 = fac.createPlace();

        name.setText("four");

        place4.setName(name); 
        place4.setId("four");
        

        PTMarking mark4 = fac.createPTMarking();
        mark4.setText(placeIsEmpty);
        place4.setInitialMarking(mark4);

        p.getObjects().add(place4);
        
        //a transition connecting all
        
        
        fr.lip6.move.pnml.ptnet.Transition TranA = fac.createTransition();
        fr.lip6.move.pnml.ptnet.Arc arcA1 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA2 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA3 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA4 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA5 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA6 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA7 = fac.createArc();
        fr.lip6.move.pnml.ptnet.Arc arcA8 = fac.createArc();
        
        TranA.setId("TA");
        name.setText("A");
        TranA.setName(name);
        
        arcA1.setTarget(TranA);
        arcA1.setSource(place1);
        arcA1.setId("A1");
        
        arcA2.setTarget(place1); //to place2
        arcA2.setSource(TranA);
        arcA2.setId("A2");
        
        arcA3.setTarget(TranA);
        arcA3.setSource(place2);
        arcA3.setId("A3");
        
        arcA4.setTarget(place2); //to place2
        arcA4.setSource(TranA);
        arcA4.setId("A4");
        
        arcA5.setTarget(TranA);
        arcA5.setSource(place3);
        arcA5.setId("A5");
        
        arcA6.setTarget(place3); //to place2
        arcA6.setSource(TranA);
        arcA6.setId("A6");
        
        arcA7.setTarget(TranA);
        arcA7.setSource(place4);
        arcA7.setId("A7");
        
        arcA8.setTarget(place4); //to place2
        arcA8.setSource(TranA);
        arcA8.setId("A8");
        
        p.getObjects().add(arcA1);
        p.getObjects().add(arcA2);
        p.getObjects().add(arcA3);
        p.getObjects().add(arcA4);
        p.getObjects().add(arcA5);
        p.getObjects().add(arcA6);
        p.getObjects().add(arcA7);
        p.getObjects().add(arcA8);
        p.getObjects().add(TranA);
        
        
       
        try {
        	System.out.println("starfish test");
        	interpretCheck.check(p,ctx,0);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
}
