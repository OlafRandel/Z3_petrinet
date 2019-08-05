import static org.junit.Assert.*;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import fr.lip6.move.pnml.framework.hlapi.HLAPIRootClass;
import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InternalException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidFileException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidFileTypeException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.pnmlcoremodel.Page;
import fr.lip6.move.pnml.pnmlcoremodel.PetriNet;
import fr.lip6.move.pnml.pnmlcoremodel.PnObject;
//import fr.lip6.move.pnml.pnmlcoremodel.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.Arc;
import fr.lip6.move.pnml.ptnet.PTArcAnnotation;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.PtnetFactory;
import fr.lip6.move.pnml.ptnet.Transition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.microsoft.z3.*;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.impl.PetriNetDocImpl;
import fr.lip6.move.pnml.ptnet.impl.PtnetFactoryImpl;



public class Test {


	
	
	
	@org.junit.Test
	public void INTERPRET() {
        
		 HashMap<String, String> cfg = new HashMap<String, String>();
	     ctx = new Context(cfg);
	     cfg.put("model", "true");
	     
	     //read in the pnml file as a PTNetDocument
	     HLAPIRootClass rc = null;
	     try {
				File f = new File("example.pnml");
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
			 
			 //set the initial marking and list the places
			 Map<String, BoolExpr> vars = new HashMap<String, BoolExpr>(); //places
			 Map<String, BoolExpr> primevars = new HashMap<String, BoolExpr>(); //prime expressio of the places
			 BoolExpr I = ctx.mkBool(true);
			 
			for (fr.lip6.move.pnml.ptnet.PetriNet pn : pnd.getNets()) {
				for (fr.lip6.move.pnml.ptnet.Page page : pn.getPages()) {
					for (fr.lip6.move.pnml.ptnet.PnObject o : page.getObjects()) {
						if (o instanceof Place) {
							Place p = (Place) o;
							
							String n = p.getId();
							BoolExpr place = ctx.mkBoolConst(n);
							vars.put(n, place);
							primevars.put(n, (BoolExpr)toPrime(place,ctx));
							
							if(p.getInitialMarking().getText() == 1){
								ctx.mkAnd(I,ctx.mkBoolConst(n));
							}else {
								ctx.mkAnd(I,n(ctx.mkBoolConst(n)));
							}
							
						}
					}
				}
			}

			//System.out.println(vars.toString());
			//System.out.println(primevars.toString());
			//System.out.println(I.getSExpr());

			//get the transitions
			BoolExpr T = ctx.mkBool(false);
			BoolExpr hulpT;
			String varsKey;
			for (fr.lip6.move.pnml.ptnet.PetriNet pn : pnd.getNets()) {
				for (fr.lip6.move.pnml.ptnet.Page page : pn.getPages()) {
					for (fr.lip6.move.pnml.ptnet.PnObject o : page.getObjects()) {
						if (o instanceof Transition) {
							Transition t = (Transition) o;
							PTArcAnnotation m;
							String name = t.getId();
							//System.out.println("transition: " + name);
							hulpT = ctx.mkBool(true);
							
							for (Arc out : t.getOutArcs()) {
								m = out.getInscription();
								varsKey = m.getContainerArc().getTarget().getId();
								//System.out.println("in: " + varsKey);
								ctx.mkAnd(
										hulpT,
										ctx.mkAnd(
												primevars.get(varsKey), n(vars.get(varsKey))
												)
										);
								
							}
							
							for (Arc in : t.getInArcs()) {
								m = in.getInscription();
								varsKey = m.getContainerArc().getSource().getId();
								//System.out.println("out: " + varsKey);
								ctx.mkAnd(
										hulpT,
										ctx.mkAnd(
												vars.get(varsKey), n(primevars.get(varsKey))
												)
										);

							}
							
							boolean skipTheConsistencyEnforcement = false;
							for (Entry<String, BoolExpr> plek: vars.entrySet()) { //voor elke plek
								for (Arc out : t.getOutArcs()) {
									if(out.getInscription().getContainerArc().getTarget().getId() == plek.getKey()) { //als de plek genoemd wordt in de transitie
										skipTheConsistencyEnforcement = true;
										continue;
									}
								}
								for (Arc in : t.getInArcs()) {
									if(in.getInscription().getContainerArc().getSource().getId() == plek.getKey()) { //als de plek genoemd wordt in de transitie
										skipTheConsistencyEnforcement = true;
										continue;
									}
								}
								if(!skipTheConsistencyEnforcement) {
									ctx.mkAnd(hulpT, ctx.mkEq(plek.getValue(), primevars.get(plek.getKey())));
								}
								
							}
							
							ctx.mkOr(T,hulpT); //All possible transitions are mutually exclusive possibilities in the overall transition set T
						}
					}
				}
			}
			System.out.println(T.toString());
			
			//define the property of not being 1-safe
			BoolExpr P = ctx.mkBool(false);
			BoolExpr hulpP;
			Boolean both_in_and_and_out = false;
			for (fr.lip6.move.pnml.ptnet.PetriNet pn : pnd.getNets()) {
				for (fr.lip6.move.pnml.ptnet.Page page : pn.getPages()) {
					for (fr.lip6.move.pnml.ptnet.PnObject o : page.getObjects()) {
						if (o instanceof Transition) { //for every transition:
							Transition t = (Transition) o;
							PTArcAnnotation m;
							PTArcAnnotation hulpm;
							for (Arc out : t.getOutArcs()) {
								m = out.getInscription();
								
								for (Arc in : t.getInArcs()) {
									hulpm = in.getInscription();
									if (vars.get(m.getContainerArc().getTarget().getId()) == vars.get(hulpm.getContainerArc().getSource().getId())) {
										both_in_and_and_out = true; //given that the target isn't also a source
									}
								}
								
								if(!both_in_and_and_out) {
									ctx.mkOr(P,vars.get(m.getContainerArc().getTarget().getId())); //at least one of the targets is already true
								}
								both_in_and_and_out = false;
							}
							hulpP = ctx.mkBool(true);
							for (Arc in : t.getInArcs()) {
								m = in.getInscription();
								ctx.mkAnd(hulpP,vars.get(m.getContainerArc().getSource().getId())); //all of the sources making it possible are true
							}
							ctx.mkAnd(P,hulpP);
						}
					}
				}
			}
			
			//check for 1-safety
			check(I,T,n(P), ctx);
			
	}
	
	/*	UNSAFE
	 *  test1:
	 *  	I = (-a)(-b)
	 *  	T = (-a',-a)(a',a)(b',a,-b)(b',-a,b)(-b',a,b)(-b',-a,-b) : a'=-a, b'=a xor b
	 *  	P = (-a,-b)
	 */
	@org.junit.Test
	public void test1() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        
        BoolExpr a = ctx.mkBoolConst("a");
        BoolExpr b = ctx.mkBoolConst("b");
        
        BoolExpr I = ctx.mkNot(ctx.mkOr(a, b));
        
        BoolExpr Ta = ctx.mkEq(toPrime(a, ctx), ctx.mkNot(a));
        BoolExpr Tb = ctx.mkEq(toPrime(b, ctx), ctx.mkXor(b,a));
        BoolExpr T = ctx.mkAnd(Ta, Tb);
        
        BoolExpr P = ctx.mkNot(ctx.mkAnd(a, b));
        System.out.println("Test1:");
        check(I,T, P, ctx);
	}
	
	/*	SAFE
	 *  test2:
	 *  	I = (a)(b)(-c)
	 *  	T = (-a,c')(a,-c')(b,-b')(-a,-b,-a')(-c,a',a)(-c,a',b)
	 *  	P = (-a,-b,-c)
	 */
	@org.junit.Test
	public void test2() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        
        BoolExpr a = ctx.mkBoolConst("a");
        BoolExpr b = ctx.mkBoolConst("b");
        BoolExpr c = ctx.mkBoolConst("c");
        
        BoolExpr I = ctx.mkNot(ctx.mkOr(ctx.mkNot(a), ctx.mkNot(b), c));
        
        BoolExpr T1 = ctx.mkOr(ctx.mkNot(a), (BoolExpr)toPrime(c, ctx));
        BoolExpr T2 = ctx.mkOr(a, ctx.mkNot((BoolExpr)toPrime(c, ctx)));
        BoolExpr T3 = ctx.mkOr(b, ctx.mkNot((BoolExpr)toPrime(b, ctx)));
        BoolExpr T4 = ctx.mkOr(ctx.mkNot(a),ctx.mkNot(b), ctx.mkNot((BoolExpr)toPrime(a, ctx)));
        BoolExpr T5 = ctx.mkOr(ctx.mkNot(c),a,(BoolExpr) toPrime(a, ctx));
        BoolExpr T6 = ctx.mkOr(ctx.mkNot(c),b, (BoolExpr)toPrime(a, ctx));
        BoolExpr T = ctx.mkAnd(T1,T2,T3,T4,T5,T6);
        
        BoolExpr P = ctx.mkNot(ctx.mkAnd(a, b, c));
        System.out.println("Test2:");
        check(I,T, P, ctx);
	}
	
	/*
	 * 	UNSAFE
	 *  test3:
	 *  	I = (00000001)
	 *  	T : T(b_i) = b_{i-1} xor b_{i+1}
	 *  	P = -(00000000)
	 */
	@org.junit.Test
	public void test3() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        int length = 8;
        BoolExpr[] b = new BoolExpr[8];   
        
        BoolExpr I = ctx.mkTrue();
        for(int i = 0; i < 8; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == length-1)
        	I = ctx.mkAnd(I, b[i]);
        	else
        		I = ctx.mkAnd(I,ctx.mkNot(b[i]));
        }
        
        BoolExpr[] Targs = new BoolExpr[length];
        for(int i = 0; i < 8; i ++){
        	int pre = (i+9)%length;
        	int post = (i+1)%length;
        	Targs[i] = ctx.mkEq(toPrime(b[i],ctx), ctx.mkXor(b[post], b[pre]));
        }
        BoolExpr T = ctx.mkAnd(Targs);
        
        BoolExpr P = ctx.mkTrue();
        for(int i = 0; i < 8; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	P = ctx.mkAnd(P,ctx.mkNot(b[i]));
        }
        P = ctx.mkNot(P);
        
        System.out.println("Test3:");
        check(I, T, P, ctx);
	}
	
	/*
	 *  UNSAFE
	 *  test4:
	 *  	I = (a)(-b)(-c)
	 *  	T = (-a,c')(a,-c')(b,-a')(-b,a')(-c,b')(-c,b)
	 *  		: a' = b; b' = c; c' = a;
	 *  	P = (-b)
	 */
	@org.junit.Test
	public void test4() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        
        BoolExpr a = ctx.mkBoolConst("a");
        BoolExpr b = ctx.mkBoolConst("b");
        BoolExpr c = ctx.mkBoolConst("c");
        
        BoolExpr I = ctx.mkAnd(a, ctx.mkNot(b), ctx.mkNot(c));
        
        BoolExpr T1 = ctx.mkEq(a, toPrime(c,ctx));
        BoolExpr T2 = ctx.mkEq(b, toPrime(a,ctx));
        BoolExpr T3 = ctx.mkEq(c, toPrime(b,ctx));
        BoolExpr T = ctx.mkAnd(T1,T2,T3);
        
        BoolExpr P = ctx.mkNot(b);
        System.out.println("Test4:");
        check(I, T, P, ctx);
	}
	
	/*
	 * 	SAFE
	 *  test5:
	 *  	I = (00000001)
	 *  	T : T(b_i) = b_{i-1} xor b_{i+1}
	 *  	P = -(10000000)
	 */
	@org.junit.Test
	public void test5() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        int length = 8;
        BoolExpr[] b = new BoolExpr[8];   
        
        BoolExpr I = ctx.mkTrue();
        for(int i = 0; i < 8; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == length-1)
        	I = ctx.mkAnd(I, b[i]);
        	else
        		I = ctx.mkAnd(I,ctx.mkNot(b[i]));
        }
        
        BoolExpr[] Targs = new BoolExpr[length];
        for(int i = 0; i < 8; i ++){
        	int pre = (i+9)%length;
        	int post = (i+1)%length;
        	Targs[i] = ctx.mkEq(toPrime(b[i],ctx), ctx.mkXor(b[post], b[pre]));
        }
        BoolExpr T = ctx.mkAnd(Targs);
        
        BoolExpr P = ctx.mkTrue();
        for(int i = 0; i < 8; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == 0)
        	P = ctx.mkAnd(P, b[i]);
        	else
        		P = ctx.mkAnd(P,ctx.mkNot(b[i]));
        }
        P = ctx.mkNot(P);
        
        System.out.println("Test5:");
        check(I, T, P, ctx);
	}
	
	/*
	 *  SAFE
	 *  test6:
	 *  	I = (0000000001)
	 *  	T : T(I) = I + 2
	 *  	P = -(1000000000)
	 *
	@org.junit.Test
	public void test6() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        int length = 10;
        BoolExpr[] b = new BoolExpr[length];   
        
        BoolExpr I = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == length-1)
        	I = ctx.mkAnd(I, b[i]);
        	else
        		I = ctx.mkAnd(I,ctx.mkNot(b[i]));
        }
        
    	int carry = 0;
        BoolExpr[] Targs = new BoolExpr[length]; 
        Targs[length - 1] = ctx.mkEq(toPrime(b[length - 1],ctx), b[length-1]);
        for(int i = length - 1; i >= 0; i--){
        	if(i != length - 1){
    			BoolExpr alltrue = ctx.mkTrue();
        		for(int j = i + 1; j < length - 1; j++){
        			alltrue = ctx.mkAnd(alltrue,b[j]);
        		}
        		Targs[i] = ctx.mkAnd(ctx.mkImplies(ctx.mkNot(alltrue), ctx.mkEq(toPrime(b[i],ctx), b[i])),
        							ctx.mkImplies(alltrue, ctx.mkEq(toPrime(b[i],ctx), ctx.mkNot(b[i]))));
        	}
        }
        BoolExpr T = ctx.mkAnd(Targs);
        
        BoolExpr P = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == 0)
        	P = ctx.mkAnd(P, b[i]);
        	else
        		P = ctx.mkAnd(P,ctx.mkNot(b[i]));
        }
        P = ctx.mkNot(P);
        
        System.out.println("Test6:");
        check(I, T, P, ctx);
	}
	
	/*
	 *  UNSAFE
	 *  test7:
	 *  	I = (0000000000)
	 *  	T : T(I) = I + 2
	 *  	P = -(1000000000)
	 *
	@org.junit.Test
	public void test7() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        int length = 10;
        BoolExpr[] b = new BoolExpr[length];   
        
        BoolExpr I = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	//if(i == length-1)
        	//I = ctx.mkAnd(I, b[i]);
        	//else
        		I = ctx.mkAnd(I,ctx.mkNot(b[i]));
        }
        
    	int carry = 0;
        BoolExpr[] Targs = new BoolExpr[length]; 
        Targs[length - 1] = ctx.mkEq(toPrime(b[length - 1],ctx), b[length-1]);
        for(int i = length - 1; i >= 0; i--){
        	if(i != length - 1){
    			BoolExpr alltrue = ctx.mkTrue();
        		for(int j = i + 1; j < length - 1; j++){
        			alltrue = ctx.mkAnd(alltrue,b[j]);
        		}
        		Targs[i] = ctx.mkAnd(ctx.mkImplies(ctx.mkNot(alltrue), ctx.mkEq(toPrime(b[i],ctx), b[i])),
        							ctx.mkImplies(alltrue, ctx.mkEq(toPrime(b[i],ctx), ctx.mkNot(b[i]))));
        	}
        }
        BoolExpr T = ctx.mkAnd(Targs);
        
        BoolExpr P = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i == 0)
        	P = ctx.mkAnd(P, b[i]);
        	else
        		P = ctx.mkAnd(P,ctx.mkNot(b[i]));
        }
        P = ctx.mkNot(P);

        System.out.println("Test7:");
        check(I, T, P, ctx);
	}
	
	/*
	 *  UNSAFE
	 *  test8:
	 *  	I = (1111111110)
	 *  	T : T(b_i) = b_{i-1} and b_{i-2}
	 *  	P = -(0000000000)
	 *
	@org.junit.Test
	public void test8() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        int length = 10;
        BoolExpr[] b = new BoolExpr[length];   
        
        BoolExpr I = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	if(i != length-1)
        	I = ctx.mkAnd(I, b[i]);
        	else
        		I = ctx.mkAnd(I,ctx.mkNot(b[i]));
        }
        
        BoolExpr[] Targs = new BoolExpr[length];
        for(int i = 0; i < length; i ++){
        	int pre = (i+9)%length;
        	int post = (i+10)%length;
        	Targs[i] = ctx.mkEq(toPrime(b[i],ctx), ctx.mkAnd(b[post], b[pre]));
        }
        BoolExpr T = ctx.mkAnd(Targs);
        
        BoolExpr P = ctx.mkTrue();
        for(int i = 0; i < length; i ++){
        	b[i] = ctx.mkBoolConst("b"+i);
        	P = ctx.mkAnd(P,ctx.mkNot(b[i]));
        }
        P = ctx.mkNot(P);
        System.out.println("Test8:");
        check(I, T, P, ctx);
	}*/

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
	
	static final int L = 0;
	static final int R = 1;
	
	//public enum Dingen {
	static final int M = 0;
	static final int W = 1;
	static final int G = 2;
	static final int K = 3;
	//}
	
	/*
	 * SAFE
	 * testPetri:
	 * 		I = (11110000)
	 * 		T = (a,-e,-a',e')(-a,e,a',-e')(a,b,-e,-f,-a',-b',e',f')(-a,-b,e,f, a',b',-e',-f')(a,c,-e,-g,-a',-c',e',g')(-a,-c,e,g, a',c',-e',-g')(a,d,-e,-h,-a',-d',e',h')(-a,-d,e,h, a',d',-e',-h')
	 * 		P = (00001111)
	 */
	@org.junit.Test
	public void testPetri() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        ctx = new Context(cfg);
        cfg.put("model", "true");
        
        BoolExpr E[][]  = new BoolExpr[2][4];
        	E[L][M] = ctx.mkBoolConst("man_l");
        	E[L][W] = ctx.mkBoolConst("wolf_l");
        	E[L][K] = ctx.mkBoolConst("kool_l");
        	E[L][G] = ctx.mkBoolConst("geit_l"); 
        	E[R][M] = ctx.mkBoolConst("man_r");
        	E[R][W] = ctx.mkBoolConst("wolf_r");
        	E[R][K] = ctx.mkBoolConst("kool_r");
        	E[R][G] = ctx.mkBoolConst("geit_r");
	        				 
       
        BoolExpr P[][] = new BoolExpr[2][4];
        	P[L][M] = (BoolExpr)toPrime(E[L][M],ctx);
        	P[L][W] = (BoolExpr)toPrime(E[L][W],ctx);
        	P[L][K] = (BoolExpr)toPrime(E[L][K],ctx);
        	P[L][G] = (BoolExpr)toPrime(E[L][G],ctx);	
        	P[R][M] = (BoolExpr)toPrime(E[R][M],ctx);
        	P[R][W] = (BoolExpr)toPrime(E[R][W],ctx);
        	P[R][K] = (BoolExpr)toPrime(E[R][K],ctx);
        	P[R][G] = (BoolExpr)toPrime(E[R][G],ctx);
        	
        	
        
        BoolExpr I = ctx.mkAnd(E[L][M], E[L][W], E[L][G], E[L][K],
        					   n(E[R][M]), n(E[R][W]), n(E[R][G]), n(E[R][K]));
        
		BoolExpr T1 = 
        		ctx.mkAnd(
        				 ctx.mkAnd(E[L][M], n(E[R][M])), //als man_links en niet man_rechts
        				 ctx.mkAnd(n(P[L][M]), P[R][M]) //maak het niet man_links wel man_rechts
        		);
        for (int i = 0; i < 4; i++) {
			if (i == M) continue;
			for (int j = 0; j < 2; j++) {
				T1 = and(T1, ctx.mkEq(E[j][i], P[j][i]));
			}
		}
		
		
		BoolExpr T2 = 
				ctx.mkAnd(
       				 ctx.mkAnd(E[R][M], n(E[L][M])), //als man_rechts en niet man_links
       				 ctx.mkAnd(n(P[R][M]), P[L][M]) //maak het niet man_rechts wel man_links
       		);
		for (int i = 0; i < 4; i++) {
			if (i == M) continue;
			for (int j = 0; j < 2; j++) {
				T2 = and(T2, ctx.mkEq(E[j][i], P[j][i]));
			}
		}
       
        
        BoolExpr T3  = 
        		ctx.mkAnd(
       				 ctx.mkAnd(E[L][M], n(E[R][M]), E[L][W], n(E[R][W])), //als man_links en wolf_links en niet man_rechts of wolf_rechts
       				 ctx.mkAnd(n(P[L][M]), P[R][M], n(P[L][W]), P[R][W]) //maak het niet man_links of wolf_links wel man_rechts en wolf_rechts
       		);
       for (int i = 0; i < 4; i++) {
			if (i == M || i == W) continue;
			for (int j = 0; j < 2; j++) {
				T3 = and(T3, ctx.mkEq(E[j][i], P[j][i]));
			}
		}
       
       
        BoolExpr T4 = 
        		ctx.mkAnd(
          				 ctx.mkAnd(E[R][M], n(E[L][M]), E[R][W], n(E[L][W])), //als man_rechts en wolf_rechts en niet man_links of wolf_links
          				 ctx.mkAnd(n(P[R][M]), P[L][M], n(P[R][W]), P[L][W]) //maak het niet man_rechts of wolf_rechts wel man_links en wolf_links
          		);
          for (int i = 0; i < 4; i++) {
   			if (i == M || i == W) continue;
   			for (int j = 0; j < 2; j++) {
   				T4 = and(T4, ctx.mkEq(E[j][i], P[j][i]));
   			}
   		}
          
          
        BoolExpr T5 = 
        		ctx.mkAnd(
          				 ctx.mkAnd(E[L][M], n(E[R][M]), E[L][G], n(E[R][G])), //als man_links en geit_links en niet man_rechts of geit_rechts
          				 ctx.mkAnd(n(P[L][M]), P[R][M], n(P[L][G]), P[R][G]) //maak het niet man_links of geit_links wel man_rechts en geit_rechts
          		);
          for (int i = 0; i < 4; i++) {
   			if (i == M || i == G) continue;
   			for (int j = 0; j < 2; j++) {
   				T5 = and(T5, ctx.mkEq(E[j][i], P[j][i]));
   			}
   		}
          
          
        
        BoolExpr T6 = 
        		ctx.mkAnd(
         				 ctx.mkAnd(E[R][M], n(E[L][M]), E[R][G], n(E[L][G])), //als man_rechts en geit_rechts en niet man_links of geit_links
         				 ctx.mkAnd(n(P[R][M]), P[L][M], n(P[R][G]), P[L][G]) //maak het niet man_rechts of geit_rechts wel man_links en geit_links
         		);
         for (int i = 0; i < 4; i++) {
  			if (i == M || i == G) continue;
  			for (int j = 0; j < 2; j++) {
  				T6 = and(T6, ctx.mkEq(E[j][i], P[j][i]));
  			}
  		}
         
         
        BoolExpr T7  = 
        		ctx.mkAnd(
         				 ctx.mkAnd(E[L][M], n(E[R][M]), E[L][K], n(E[R][K])), //als man_links en kool_links en niet man_rechts of kool_rechts
         				 ctx.mkAnd(n(P[L][M]), P[R][M], n(P[L][K]), P[R][K]) //maak het niet man_links of kool_links wel man_rechts en kool_rechts
         		);
         for (int i = 0; i < 4; i++) {
  			if (i == M || i == K) continue;
  			for (int j = 0; j < 2; j++) {
  				T7 = and(T7, ctx.mkEq(E[j][i], P[j][i]));
  			}
  		}
         
         
        BoolExpr T8 = 
        		ctx.mkAnd(
         				 ctx.mkAnd(E[R][M], n(E[L][M]), E[R][K], n(E[L][K])), //als man_rechts en kool_rechts en niet man_links of kool_links
         				 ctx.mkAnd(n(P[R][M]), P[L][M], n(P[R][K]), P[L][K]) //maak het niet man_rechts of kool_rechts wel man_links en kool_links
         		);
         for (int i = 0; i < 4; i++) {
  			if (i == M || i == K) continue;
  			for (int j = 0; j < 2; j++) {
  				T8 = and(T8, ctx.mkEq(E[j][i], P[j][i]));
  			}
         }
  		
         System.out.println("");
         
         BoolExpr R1 = //verboden uitkomsten
        		 ctx.mkAnd(
	        		 n(ctx.mkAnd(
	        				P[L][G], P[L][W], n(P[L][M])
	        			)),
	        		 
	        		 n(ctx.mkAnd(
		        				P[R][G], P[R][W], n(P[R][M])
		        		)),
	        		 
	        		 n(ctx.mkAnd(
		        				P[L][G], P[L][K], n(P[L][M])
		        		)),
	        		 
	        		 n(ctx.mkAnd(
		        				P[R][G], P[R][K], n(P[R][M])
			        	))
        		 );
         
         
        BoolExpr T = ctx.mkOr(T1,T2,T3,T4,T5,T6,T7,T8);
        T = and(T,R1);
        
        BoolExpr F = ctx.mkAnd(n(E[L][M]), n(E[L][W]), n(E[L][G]), n(E[L][K]), E[R][M], E[R][W], E[R][G], E[R][K]); //een ontkenning van de gewenste situatie
        

        System.out.println("TestPetri: mutual exlusion oevers");
        BoolExpr G1 = ctx.mkBool(true);
        //for (int nti = 0; nti < 4; nti++) {
        	G1 = and(G1, ctx.mkXor(E[L][0], E[R][0])); //dat de paren Xor zijn
        	G1 = and(G1, ctx.mkXor(E[L][1], E[R][1])); //dat de paren Xor zijn
        	G1 = and(G1, ctx.mkXor(E[L][2], E[R][2])); //dat de paren Xor zijn
        	G1 = and(G1, ctx.mkXor(E[L][3], E[R][3])); //dat de paren Xor zijn
        //}
        check(I, T, G1, ctx);

        System.out.println("TestPetri: 1-safety");
        BoolExpr G2 = ctx.mkBool(true);
        for (int i = 0; i < 4; i++) {
        	if (i == M) continue;
        	G2 = and(G2, n(ctx.mkAnd(ctx.mkAnd(E[L][M], E[L][i]), ctx.mkOr(E[R][M], E[R][i])))); //een statement dat het niet is 1-safe
        	G2 = and(G2, n(ctx.mkAnd(ctx.mkAnd(E[R][M], E[R][i]), ctx.mkOr(E[L][M], E[L][i])))); 
        }
        check(I, T, G2, ctx);
        
        

        System.out.println("TestPetri: solution");
        check(I, T, n(F), ctx);
	}
	
	private static void check(BoolExpr I, BoolExpr T, BoolExpr P, Context ctx) {
		long start = System.currentTimeMillis();
		PDR mc = new PDR(I, T, P, ctx);
		for (Interpretation interp : mc.check()) {
			System.out.println(interp);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Time: " + (stop - start) / 1000.0);
		//mc.showFrames();
		System.out.println();
	}
	
	private Expr toPrime(Expr e, Context ctx){
		return ctx.mkConst(e.getFuncDecl().getName().toString() + "\'",e.getSort());
	}
	
	  private static String getPlaceName(Place p) {

          return p.getName().getText().replaceAll("\\.|#", "-");

	  }
}
