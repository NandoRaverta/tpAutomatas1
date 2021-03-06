/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author cesar
 */
public class Quintupla <A,B,C,D,E> {
    
        // Private attributes
    private A _first;
    private B _second;
    private C _third;
    private D _fourth;
    private E _fifth;
    
    // Construction
    public Quintupla(A fst, B snd, C trd, D frt, E fif) {
        _first = fst;
        _second = snd;
        _third = trd;
        _fourth = frt;
        _fifth = fif;
    }

    // Getters
    public A first() {
        return _first;
    }

    public B second() {
        return _second;
    }

    public C third() {
        return _third;
    }
    
    public D fourth() {
        return _fourth;
    }
    
    public E fifth() {
        return _fifth;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_first == null) ? 0 : _first.hashCode());
        result = prime * result + ((_second == null) ? 0 : _second.hashCode());
        result = prime * result + ((_third == null) ? 0 : _third.hashCode());
        result = prime * result + ((_fourth == null) ? 0 : _fourth.hashCode());
        result = prime * result + ((_fifth == null) ? 0 : _fifth.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Quintupla other = (Quintupla) obj;
        if (_first == null) {
            if (other._first != null) {
                return false;
            }
        } else if (!_first.equals(other._first)) {
            return false;
        }
        if (_second == null) {
            if (other._second != null) {
                return false;
            } else {
            }
        } else if (!_second.equals(other._second)) {
            return false;
        }
        if (_third == null) {
            if (other._third != null) {
                return false;
            }
        } else if (!_third.equals(other._third)) {
            return false;
        }if (_fourth == null) {
            if (other._fourth != null) {
                return false;
            }
        } else if (!_fourth.equals(other._fourth)) {
            return false;
        }if (_fifth == null) {
            if (other._fifth != null) {
                return false;
            }
        } else if (!_fifth.equals(other._fifth)) {
            return false;
        }
        return true;
    }
    
    public String toString(){
        return "("+ _first.toString() + "," + _second.toString() + ","+ _third.toString() +")";
    }
    
}
