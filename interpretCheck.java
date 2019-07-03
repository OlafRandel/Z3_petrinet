import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import fr.lip6.move.pnml.ptnet.Arc;
import fr.lip6.move.pnml.ptnet.PTArcAnnotation;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.PnObject;
import fr.lip6.move.pnml.ptnet.Transition;



public class interpretCheck {

	/*
	 * the check method takes a petrinet page and performs a check for a testable quality.
	 * page: the petrinet page that is to be checked
	 * ctx: a persistent Context (model, true)
	 * choice: which quality is tested, 0 = 1-safety, 1 = liveness
	 */
	static Boolean check(fr.lip6.move.pnml.ptnet.Page page, Context ctx, int choice){
		 
		 //set the initial marking and list the places
		 Map<String, BoolExpr> vars = new HashMap<String, BoolExpr>(); //expressions for the places
		 Map<String, BoolExpr> primevars = new HashMap<String, BoolExpr>(); //prime expression of the places
		 Map<String, Boolean> prereqList = new HashMap<String, Boolean>(); //keep track of what is both a source and a target
		 BoolExpr I = ctx.mkBool(true);
		
		for (fr.lip6.move.pnml.ptnet.PnObject o : page.getObjects()) {
				if (o instanceof Place) {
					Place p = (Place) o;
					
					String n = p.getId();
					BoolExpr place = ctx.mkBoolConst(n);
					vars.put(n, place);
					primevars.put(n, (BoolExpr)toPrime(place,ctx));
					prereqList.put(n, false);
					
				     try {
						if(p.getInitialMarking().getText() == 1){
							I = ctx.mkAnd(I,ctx.mkBoolConst(n));
						}else {
							I = ctx.mkAnd(I,ctx.mkNot((ctx.mkBoolConst(n))));
						}
				     }catch(Exception e){
					     //System.out.print(e.getMessage());
						 I = ctx.mkAnd(I,ctx.mkNot((ctx.mkBoolConst(n))));
				     }
				}
		}

		//get the transitions
		BoolExpr T = ctx.mkBool(false);
		BoolExpr hulpT;
		String varsKey;
		for (fr.lip6.move.pnml.ptnet.PnObject o : page.getObjects()) {
			if (o instanceof Transition) {
				Transition t = (Transition) o;
				//System.out.println(t.getId());
				hulpT = ctx.mkBool(true);
				for (Arc out : t.getOutArcs()) {
					varsKey = out.getTarget().getId();
					
					for (Arc in : t.getInArcs()) {
						if(varsKey == in.getSource().getId()) {
							prereqList.put(varsKey, true); //list the place as a prereq
						} else {
							prereqList.put(varsKey, false); //take the place off the list if it was already on from a previous iteration
						}
					}
					
					if(prereqList.get(varsKey)) { //in case of prereq, it should be true before AND after the step
						hulpT = ctx.mkAnd(hulpT, primevars.get(varsKey), vars.get(varsKey));
					} else {
						hulpT = ctx.mkAnd(
								hulpT ,primevars.get(varsKey), ctx.mkNot(vars.get(varsKey)) //in case of no prereq, it should be true after and false before
								);
					}
					
				}
				
				for (Arc in : t.getInArcs()) {
					varsKey = in.getSource().getId();
					
					if(prereqList.get(varsKey)) { //in case of prereq, it should be true before AND after the step
						hulpT = ctx.mkAnd(hulpT, primevars.get(varsKey), vars.get(varsKey));
					}else {
						hulpT = ctx.mkAnd(
								hulpT,vars.get(varsKey), ctx.mkNot(primevars.get(varsKey)) //in case of no prereq, it should be true before and false after the step
								);
					}
				}
				
				boolean skipTheConsistencyEnforcement = false;
				for (Entry<String, BoolExpr> plek: vars.entrySet()) { //voor elke plek
					skipTheConsistencyEnforcement = false;
					for (Arc out : t.getOutArcs()) {
						if(out.getTarget().getId() == plek.getKey()) { //als de plek genoemd wordt in de transitie
							skipTheConsistencyEnforcement = true;
							continue;
						}
					}
					for (Arc in : t.getInArcs()) {
						if(in.getSource().getId() == plek.getKey()) { //als de plek genoemd wordt in de transitie
							skipTheConsistencyEnforcement = true;
							continue;
						}
					}
					if(!skipTheConsistencyEnforcement) {
						hulpT = ctx.mkAnd(hulpT, ctx.mkEq(plek.getValue(), primevars.get(plek.getKey())));
					}
				}
				T = ctx.mkOr(T,hulpT); //All possible transitions are mutually exclusive possibilities in the overall transition set T
			}
		}
		//System.out.println(T.toString());
		
		BoolExpr P = ctx.mkBool(true);
		
		if(choice == 0) {
			//define the property of not being 1-safe
			P = ctx.mkBool(true);
			
			Boolean both_in_and_and_out = false;
			for (PnObject o : page.getObjects()) {
				if (o instanceof Transition) { //for every transition:
					Transition t = (Transition) o;
					BoolExpr hulpP = ctx.mkBool(false);				
					for (Arc out : t.getOutArcs()) {
	
						both_in_and_and_out = false;
						for (Arc in : t.getInArcs()) {
							if (vars.get(out.getTarget().getId()) == vars.get(in.getSource().getId())) {
								both_in_and_and_out = true; //given that the target isn't also a source ->
								break;
							}
						}
						
						if(!both_in_and_and_out) {
							hulpP = ctx.mkOr(hulpP, vars.get(out.getTarget().getId())); //-> at least one of the targets is already true ->
						}
					}
					
					BoolExpr h2 = ctx.mkBool(true);	
					for (Arc in : t.getInArcs()) {
						h2 = ctx.mkAnd(h2,vars.get(in.getSource().getId())); //-> and all of the sources making it possible are true
						System.out.println(h2.toString());
					}
					System.out.println("setP");
					P = ctx.mkAnd(P, ctx.mkOr(ctx.mkNot(h2), ctx.mkNot(hulpP)) );
				}
			}
			System.out.println("check");
			//check for 1-safety
			return check(I, T, P, ctx);
		}
		if(choice == 1) {
			//define the property of not being live
			P = ctx.mkBool(true);
			long start = System.currentTimeMillis();
			long stop = 0;
			
			for (PnObject o : page.getObjects()) {
				if (o instanceof Transition) { //for every transition:
					Transition t = (Transition) o;
					P = ctx.mkBool(true);
					for (Arc in : t.getInArcs()) {
						P = ctx.mkAnd(P, vars.get(in.getSource().getId()));
						if(!silentCheck(I, T, P, ctx)) {
							stop = System.currentTimeMillis();
							System.out.println("false");
							System.out.println("Time: " + (stop - start) / 1000.0);
							System.out.println();
							return false;
						}
					}
				}
			}
			stop = System.currentTimeMillis();
			System.out.println("true");
			System.out.println("Time: " + (stop - start) / 1000.0);
			System.out.println();
			return true;
		}
		return false; //the choice must be 0 or 1
	}



	
	private static Boolean check(BoolExpr I, BoolExpr T, BoolExpr P, Context ctx) {
		long start = System.currentTimeMillis();
		Boolean path = false;
		PDR mc = new PDR(I, T, P, ctx);
		for (Interpretation interp : mc.check()) {
			path = true;
			System.out.println(interp);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Time: " + (stop - start) / 1000.0);
		//mc.showFrames();
		System.out.println();
		return path;
	}
	
	private static Boolean silentCheck(BoolExpr I, BoolExpr T, BoolExpr P, Context ctx) {
		PDR mc = new PDR(I, T, P, ctx);
		for (Interpretation interp : mc.check()) {
			return true;
		}
	return false;
	}
	
	private static Expr toPrime(Expr e, Context ctx){
		return ctx.mkConst(e.getFuncDecl().getName().toString() + "\'",e.getSort());
	}
	
	  private static String getPlaceName(Place p) {

          return p.getName().getText().replaceAll("\\.|#", "-");
	  }

	  static Context ctx = null;
	  static BoolExpr n(BoolExpr in) {
			return ctx.mkNot(in);
		}
	
		static BoolExpr eq(BoolExpr in, BoolExpr in2) {
			return ctx.mkEq(in, in2);
		}
	
		static BoolExpr and(BoolExpr in, BoolExpr in2) {
			return ctx.mkAnd(in, in2);
		}
		
		static BoolExpr or(BoolExpr in, BoolExpr in2) {
			return ctx.mkOr(in, in2);
		}
		
}
          