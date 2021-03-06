/********************************NFALambda.java*****************************************/
/* Archivo que implementa un Automata Finito Deterministico                            */
/*                                                                                     */
/*Cornejo, Politano, Raverta                                                           */
/***************************************************************************************/
package automata;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import utils.DisjointSet;

import utils.Triple;

/* Implements a DFA (Deterministic Finite Atomaton).
 */
public class DFA extends FA {

    /*	
     * 	Construction
     */
    // Constructor
       
    private   Object _nroStates[] ; //array used to give a number to each state
    public DFA(
            Set<State> states,
            Set<Character> alphabet,
            Set<Triple<State, Character, State>> transitions,
            State initial,
            Set<State> final_states)
            throws IllegalArgumentException 
    {
        _states= states;
        _alphabet=alphabet;
        _transitions=transitions;
        _initial=initial;
        _final_states= final_states;
        _nroStates=  _states.toArray();
        if (!rep_ok()){
            throw new  IllegalArgumentException();
        }
        System.out.println("Is a DFA");
    }
   
    /*
     *	State querying 
     */
    

    @Override
    public State delta(State from, Character c) {
        assert states().contains(from);
        assert alphabet().contains(c);
        Iterator i=_transitions.iterator();
        Triple<State, Character, State> aux;
        State result=null;
        while (i.hasNext()){
            aux=(Triple<State, Character, State>) i.next();
            if (c.equals(aux.second()) && aux.first().equals(from)) //PROBLEMA CON ; DE FROM
                {
                result=aux.third();      
            }
        }
        return result;
    }   

  /*
     *  Automata methods
     */
    @Override
    public boolean accepts(String string) {
        assert rep_ok();
        assert string != null;
        assert verify_string(string);
        State actual = _initial;
        //int lenght = string.length(); 
	//int index = 0;
	/*while (lenght != 0){   
            Character caracterActual = string.charAt(index);        
            actual = delta(actual,caracterActual);
            lenght--; 
            index ++;     
            if (actual == null)
                return false;
        }*/
        for(Character c: string.toCharArray()){
            actual= delta(actual,c);
            if (actual==null){
                return false;
            }
        }
        return _final_states.contains(actual);       
    }

    /**
     * Converts the automaton to a NFA.
     *
     * @return NFA recognizingthe same language.
     */
    public NFA toNFA() {
        assert rep_ok();
        String name="ficticio";
        State ficticio = new State(name);
        _states.add(ficticio);
        Triple<State,Character,State> t = new Triple(_initial,_alphabet.iterator().next(),ficticio);
        _transitions.add(t);
        for (Character c:_alphabet){
            //Character c = this.alphabet().iterator().next();
            t = new Triple(ficticio, c, ficticio);
           _transitions.add(t);
        }
        NFA noDet = new NFA(_states,_alphabet,_transitions,_initial,_final_states);
        return noDet;
    }

    /**
     * Converts the automaton to a NFALambda.
     *
     * @return NFALambda recognizing the same language.
     */
    public NFALambda toNFALambda() {
        assert rep_ok();
        NFALambda noDetLambda = new NFALambda(_states,_alphabet,_transitions,_initial,_final_states);
        String name="ficticio";
        State ficticio = new State(name);
        noDetLambda._states.add(ficticio);
        Triple<State,Character,State> t = new Triple(noDetLambda._initial,FA.Lambda,ficticio);
        noDetLambda._transitions.add(t);
        for (Character c:_alphabet){
            t = new Triple(ficticio, c, ficticio);
            noDetLambda._transitions.add(t);
        }
        return noDetLambda;
    }

    /**
     * Checks the automaton for language emptiness.
     *
     * @returns True iff the automaton's language is empty.
     */
public boolean is_empty() {
        assert rep_ok();
        int [][] matriz= this.clausuraTransitiva();
        int numInicial=findIndex(_initial);
        Iterator i= _final_states.iterator();
        while (i.hasNext()){
            State fin= (State)i.next();
            int numFinal=findIndex(fin);
            String nameFinal= fin.name();
            if (matriz[0][numFinal]==0){ //If there is a path from the initial to some end
                    return true;
            }
        }
        return false;
    }

    //method that returns the index where is a given state
    private int findIndex(State S){
        for (int i=0; i< _nroStates.length; i++){
            if (_nroStates[i].equals(S)){
                return i;
            }
        }
        return -1;
    }
    
    //Method that builds a matrix corresponding to the relation.
    public int[][] matrizRelation (int size){
        int [][] matriz= new int[size][size];
        State a;
        for (State b:_states) {
            int numB=findIndex(b); 
               for(Character alpha:_alphabet){
                a=delta(b,alpha);
                    if (a!=null){
                        int numA=findIndex(a); 
                        matriz[numB][numA]=1;
                    } 
                }
        }
        return matriz;
  }

    //Method based in Wharsall that computes the transitive closure of a relation
        public int[][] clausuraTransitiva() {
        int[][] clausura = this.matrizRelation(_states.size());
        for (int m = 0; m < _states.size(); m++) {
            for (int i = 0; i < _states.size(); i++) {
                for (int j = 0; j < _states.size(); j++) {
                    if (clausura[i][j] == 1) {
                        for (int k = 0; k < _states.size(); k++) {
                            if (clausura[j][k] == 1) {
                                clausura[i][k] = 1;
                            }
                        }
                    }
                }
            }
        } 
        
        return clausura;
    }
    /**
     * Checks the automaton for language infinity.
     *
     * @returns True iff the automaton's language is finite.
     */
    public boolean is_finite() {
        assert rep_ok();
        int [][] relaciones = clausuraTransitiva();
        for(State fin:_final_states){
            int columnaFinal = findIndex(fin); 
            for(int j=0; j<_states.size(); j++){    
                if((relaciones[j][columnaFinal]==1)){  //las transciones que lleguen a un final
                    int nroEstadoInicial= findIndex(_initial);  
                    if (relaciones[nroEstadoInicial][j]==1){ //transiciones que desde el inicial pueden llegar a aquellos que llegan al final
                        if(relaciones[j][j]==1){ //Si hay ciclo.
                            return false; 
                        }
                    }               
                }
            } 
        }
        return true;
    }

    /**
     * Returns a new automaton which recognizes the complementary language.
     *
     * @returns a new DFA accepting the language's complement.
     */
public DFA complement() {
        //assert rep_ok();
        State f= new State("sf" + _initial.name());
        HashSet<State> final_states=new HashSet();
        HashSet<State> states=new HashSet();
        states.add(f);
        states.addAll(_states);
        HashSet<Triple<State,Character,State>> transitions= new HashSet();
        final_states.add(f);
        transitions.addAll(_transitions);
        Triple<State,Character,State> t; 
        for(Character c: _alphabet){
            t= new Triple(f,c,f);
            transitions.add(t); //make a transitios to final states for each caracter in alphabet
        }
        //Iterator i=_states.iterator();
        HashSet<Character> notLabel;
        HashSet<Character> label;
        for(State s:states){
            if (FA.getElemFromSet(_final_states,s)==null){ //if states isn't final, so it will be a complemen's final state
                final_states.add(s);
            } 
            notLabel= new HashSet(_alphabet);
            label=new HashSet();
            for(Triple<State,Character,State> l: _transitions){
                if (l.first().equals(s)){
                    label.add(l.second());
                }
            }
            notLabel.removeAll(label);
            for(Character o: notLabel){ //create a transitions from state s to "trampa final state" f.
                t=new Triple(s,o,f);
                transitions.add(t);
            }
        }
        /*while(i.hasNext()){
            aux= (State) i.next();
            if (!_final_states.contains(aux)){
                complement._final_states.add(aux);
            }
        }*/
        DFA complement = new DFA(states,_alphabet,transitions,_initial,final_states);
        return complement;
    }

    /**
     * Returns a new automaton which recognizes the kleene closure of language.
     *
     * @returns a new DFA accepting the language's complement.
     */
    public DFA star() {
        assert rep_ok();
            Set<State> statesK= _states;
            Set<Character> alphabetK= _alphabet;
            Set<Triple<State, Character, State>> transitionsK=_transitions;
            State initialK=_initial;
            Set<State> final_statesK=_final_states;
            final_statesK.add(initialK);
            for(State estado:final_statesK){
                    Triple<State,Character,State> t = new Triple (estado, FA.Lambda, _initial);
                    transitionsK.add(t);
            }
            
            NFALambda kleene = new NFALambda(statesK,alphabetK,transitionsK,initialK,final_statesK);
            return kleene.toDFA();
       
    }

    /**
     * Returns a new automaton which recognizes the union of both languages, the
     * one accepted by 'this' and the one represented by 'other'.
     *
     * @returns a new DFA accepting the union of both languages.
     */
    //this=t and other=0
public DFA union(DFA other) {
        assert rep_ok();
        assert other.rep_ok();
        FA union;
        Set<State> states=new HashSet();
        Set<State> final_states= new HashSet();
        Set<Triple<State,Character,State>> transitions=new HashSet();
        Set<Character> alphabet= new HashSet();
        State initial= new State("U" + this._initial.name());
        states.add(initial);
        states.addAll(this._states);
        if (this._final_states.contains(this._initial) ||other._final_states.contains(other._initial)){
            final_states.add(initial);
        }
        for(State s: other._states){
            s.rename("B"+s.name()); //remember that JAVA has a passage of parameters by value
            states.add(s);
        }
        final_states.addAll(this._final_states);
        final_states.addAll(other._final_states);
        /* for(State s: other._final_states){
            s.rename("B"+s.name()); //remember that JAVA has a passage of parameters by value
            final_states.add(s);
        }  */      
        for(Triple<State,Character,State> t: this._transitions){
            transitions.add(t);
            if (t.first().equals(this._initial)){
                transitions.add(new Triple(initial,t.second(),t.third()));
            } 
        }
        for(Triple<State,Character,State> t: other._transitions){
           /* if (!t.first().name().startsWith("B")){
                t.first().rename("B"+t.first().name());
            }
            if (!t.third().name().startsWith("B")){
                t.third().rename("B"+t.third().name());
            }*/
            transitions.add(t);
            if (t.first().equals(other._initial)){
                transitions.add(new Triple(initial,t.second(),t.third()));
            } 
        }
        
        alphabet.addAll(this._alphabet);
        alphabet.addAll(other._alphabet);
        try{
            union= new DFA(states,alphabet,transitions,initial,final_states);
        }catch(IllegalArgumentException e){
            union= new NFA(states,alphabet,transitions,initial,final_states);
            union= ((NFA) union).toDFA(); //OJO! PUEDE NO SER UN DFA DEBERIA VER 
        }
        return (DFA) union;                
    }

    /**
     * Returns a new automaton which recognizes the intersection of both
     * languages, the one accepted by 'this' and the one represented by 'other'.
     *
     * @returns a new DFA accepting the intersection of both languages.
     */
    public DFA intersection(DFA other) {
       //assert rep_ok();
       //assert other.rep_ok();
        for(State s: this.states()){
            s.rename("A"+s.name());
        }
        for(State s: other.states()){
            s.rename("B"+s.name());
        }
        
        System.out.println("\n \n "+this.toString()+"\n \n ");
        System.out.println(other.toString());
        DFA cThis= this.complement();
        
        //System.out.println("\n cTHIS \n "+ cThis.to_dot());
        DFA cOther= other.complement();
        //System.out.println("cOther \n "+ cOther.to_dot());
        
        DFA union= cThis.union(cOther);
        return (union.complement());
    }
    @Override
    public boolean rep_ok() {
        boolean containLambda= false;
        boolean statesOK=true;
        boolean transitionOK= true;
        boolean nonDeterministic= false;
        //Check that the alphabet does not contains lambda.
        for(Character c: _alphabet){
            if (c== Lambda){
                containLambda=true;
                System.out.println("ContainLambda");
            }                                      
        }
        //Check that final states are included in 'states'.
        for(State s:_final_states){
            statesOK= _states.contains(s) && statesOK;
        }
        //Check that all transitions are correct. All states and characters should be part of the automaton set of states and alphabet.
        for(Triple<State,Character,State> t:_transitions){
            transitionOK= _states.contains(t.first()) && _states.contains(t.third()) && _alphabet.contains(t.second()) && transitionOK;
            //Check that the transition relation is deterministic.
            for(Triple<State,Character,State> l: _transitions){       
                //Is non deterministic if have more 1 transition from Qi to any node within the same label
                if ( t.first().equals(l.first()) && !t.third().equals(l.third())  && t.second()==l.second()){ 
                    nonDeterministic=true;
                }
            }
        }
        //Check that the transition relation is deterministic.
        System.out.println("Contain initial: "+ _states.contains(_initial));
        System.out.println("Contain lamda: "+ containLambda);
        System.out.println("nonDeterministic: "+ nonDeterministic);
        System.out.println("transitionsOK"+ transitionOK);
        System.out.println("statesOK"+ statesOK);
        
        
        
        return _states.contains(_initial) && !containLambda && !nonDeterministic && transitionOK && statesOK;
    }

//Algotithm that return the smaller DFA equivalent to this    
    public DFA minimizer(){
        DisjointSet<State> disjointSet= new DisjointSet(this.states());
        //first separation by final and not final. 
        State finalAgent= this.final_states().iterator().next(); //Take agent for final states
        State notFinalAgent=null; 
        for(State s: this.states()){//take agent for not final stage
            if (!this.final_states().contains(s)){
                notFinalAgent= s;
                break;
            }
        }
        for(State s: this.states()){//take agent for not final stage
            if (this.final_states().contains(s)){
                disjointSet.union(finalAgent,s);
            }else{
                disjointSet.union(notFinalAgent, s);
            }
         }
       //Main loop Alghoritm 
        boolean isSon=true;
        int lastSize=0;
        while ( lastSize!=disjointSet.size()){
            DisjointSet oldDisjointSet= disjointSet.copy(); //Make a copy of last classes.     
            lastSize= disjointSet.size(); //update lastSize
            for(Set<State> set: disjointSet.toSet()){// take each class
                List<State> newClasees= new LinkedList();
                for(State s: set){//take each state 
                  if (newClasees.isEmpty()){
                      disjointSet.disUnite(s);
                      newClasees.add(s);
                  }else{
                      for(State father: newClasees){ //search parent for s(busqueda de clase de equivalencia de s)
                          isSon=true;
                          for(Character c: this.alphabet()){
                              if (delta(father,c)!=null){
                                  if (delta(s,c)!=null){
                                    isSon= isSon && oldDisjointSet.father(delta(father,c)).equals(oldDisjointSet.father(delta(s,c)));        
                                  }else{
                                      isSon=false;
                                      break;
                                  }   
                              }else{
                                  isSon= isSon && null==delta(s,c);
                              }      
                          }
                          if (isSon){
                              disjointSet.disUnite(s); //State S goes out from his last class. 
                              disjointSet.union(father,s); // State S entry to new class 
                              break; //No need more iteration, S has class now. 
                          }
                     }
                     if (!isSon){ //Si s no coincide con ningun estado de la clase pasa a formar una clase solo
                         disjointSet.disUnite(s); //State S goes out  from his last class 
                         newClasees.add(s); //State S is a new class's father now.
                    }                                        
                  } 
               }
            }                 
        }
        //create a new DFA minimized
        State mInitial=disjointSet.father(this._initial) ;
        Set<State> mStates= new HashSet();
        Set<State> mFinalStates= new HashSet();
        for (Set<State> s: disjointSet.toSet()){
            State agent=disjointSet.father(s.iterator().next());
            mStates.add(agent); //put a agent for each class
            if (this.final_states().contains(agent)){
                mFinalStates.add(agent);
            }
        }
        
        Set<Triple<State,Character,State>>mTransitions= new HashSet();
        for(State s: mStates){
            for(Character c: _alphabet){
                if (delta(s,c)!=null){
                    mTransitions.add(new Triple<State,Character,State>(s,c,disjointSet.father(delta(s,c))) );
                }
            }
        }
        System.out.println("Final States: " + mFinalStates.toString());
        return new DFA(mStates,_alphabet, mTransitions,mInitial,mFinalStates);
    }
    
   
    
    
    //Method that take a Set<States> and return a string that contain all state's names concatenate     
//for example getStateName([q0,q1,q2]) return q0q1q2
    String getStateName(Set<State> set){
        //String name="{";
        String name="";
        for(State s:set){          
            /*if (name.length()>1){
                name=name+",";
            }  */  
            name=name+s.name();
        }
        //name=name+"}";                    
        return name;        
    }
    
    //Method that return if 2 AFD's are equivalent (recognize the same language)
    public boolean areEquivalent(DFA dfa2){
        Set<State> auxStates= new HashSet();
        auxStates.addAll(this.states());
        auxStates.addAll(dfa2.states());
        Set<Triple<State,Character,State>> auxTransitions= new HashSet();
        auxTransitions.addAll(this._transitions);
        auxTransitions.addAll(dfa2._transitions);
        Set<State> auxFinalState= new HashSet();
        auxFinalState.addAll(this.final_states());
        auxFinalState.addAll(dfa2.final_states());
        Set<Character> auxAlphabet=new HashSet();
        auxAlphabet.addAll(this._alphabet);
        auxAlphabet.addAll(dfa2._alphabet);
        
        DFA aux= new DFA(auxStates,auxAlphabet,auxTransitions,_initial,auxFinalState);
        DFA auxMinimum= aux.minimizer();
        
        return (auxMinimum.states().contains(_initial) && !auxMinimum.states().contains(dfa2._initial)) || (!auxMinimum.states().contains(_initial) && auxMinimum.states().contains(dfa2._initial));
    }
}