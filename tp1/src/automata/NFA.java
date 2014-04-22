package automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import utils.Triple;

public class NFA extends FA {

    /*
     *  Construction
     */
    // Constructor
    public NFA(
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
        if (!rep_ok()){
            throw new  IllegalArgumentException();
        }
        System.out.println("Is a NFA");

    }

    /*
     *	State querying 
     */
    @Override
    public Set<State> states() {
        // TODO
        return null;
    }

    @Override
    public Set<Character> alphabet() {
        // TODO
        return null;
    }

    @Override
    public State initial_state() {
        // TODO
        return null;
    }

    @Override
    public Set<State> final_states() {
        // TODO
        return null;
    }

    @Override
    public Set<State> delta(State from, Character c) {
        assert states().contains(from);
        assert alphabet().contains(c);
        Iterator i=_transitions.iterator();
        Triple<State, Character, State> aux;
        Set<State> result=new HashSet();
        while (i.hasNext()){
            aux=(Triple<State, Character, State>) i.next();
            if (c.equals(aux.second()) && aux.first().equals(from)) //PROBLEMA CON ; DE FROM
                {
                result.add(aux.third()) ;     
            }
        }
        return result;
    }   


    @Override
    public String to_dot() {
        assert rep_ok();
        String aux;
        aux = "digraph{\n";
        aux = aux + "inic[shape=ponit];\n" + "inic->" + this._initial.name() + ";\n";
        while (this._transitions.iterator().hasNext()) {
           Triple triupla = this._transitions.iterator().next();
           aux = aux + triupla.first().toString() + "->" + triupla.third().toString() + " [label=" + triupla.second().toString() + "];\n";
        }
        aux = "\n";
        while (this._final_states.iterator().hasNext()){
            State estado = this._final_states.iterator().next();
            aux = aux + estado.name() + "[shape=doublecircle];\n";
        }
        aux = aux + "}";
        return aux;
    }

    /*
     *  Automata methods
     */
    @Override
    public boolean accepts(String string) {
        assert rep_ok();
        assert string != null;
        assert verify_string(string);
        return accepts2(_initial,string);
        }
            
    public boolean accepts2 (State estado, String string){
        if (string.isEmpty())
            return _final_states.contains(estado);
        Set<State> siguientes = new HashSet();
        siguientes= delta(estado,string.charAt(0));
        boolean res = false;
        Iterator i= siguientes.iterator();
        State aux;
        while (i.hasNext()){
            aux= (State)i.next();
            res = res || accepts2(aux,string.substring(1));
        }
        return res;        
    }
    

    /**
     * Converts the automaton to a DFA.
     *
     * @return DFA recognizing the same language.
     */
    public DFA toDFA() {
        assert rep_ok();
        // TODO
        return null;
    }

    @Override
    public boolean rep_ok() {
        boolean containLambda= false;
        boolean statesOK=true;
        boolean transitionOK= true;
        //Check that the alphabet does not contains lambda.
        for(Character c: _alphabet){
            if (c== Lambda){
                containLambda=true;
            }                                      
        }
        //Check that final states are included in 'states'.
        for(State s:_final_states){
            statesOK= _states.contains(s) && statesOK;
        }
        //Check that all transitions are correct. All states and characters should be part of the automaton set of states and alphabet.
        for(Triple<State,Character,State> t:_transitions){
            transitionOK= _states.contains(t.first()) && _states.contains(t.third()) && _alphabet.contains(t.second()) && transitionOK;
        }    
        return _states.contains(_initial) && !containLambda && transitionOK && statesOK;
    }
}
        
