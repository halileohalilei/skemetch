package util;

import java.util.ArrayList;
import java.util.List;

import model.Sketch;
import model.Symbol;

public class SymbolContainer {

	public static SymbolContainer instance = new SymbolContainer();

	private List<Symbol> allSymbols;

	private List<Sketch> allSketches;

	private SymbolContainer() {
		allSymbols = new ArrayList<Symbol>();
		allSketches = new ArrayList<Sketch>();
	}

	public List<Symbol> getAllSymbols() {
		return this.allSymbols;
	}

	public List<Sketch> getAllSketches() {
		return this.allSketches;
	}

	public Symbol removeLast() {
		this.allSketches.remove(this.allSketches.size() - 1);
		Symbol s = this.allSymbols.remove(this.allSymbols.size() - 1);
		s.detach();
		return s;
	}

	public void clearAllSymbols() {
		this.allSymbols.clear();
	}
	
	public void clearAllSketches(){
		this.allSketches.clear();
	}

	public void addNewSymbol(Symbol s) {
		this.allSymbols.add(s);
	}

	public void addNewSketch(Sketch s) {
		this.allSketches.add(s);
	}

	public int getSize() {
		return this.allSymbols.size();
	}

	public String generateSchemeCode() {
		if (this.allSymbols.size() - 1 > 0) {
			return this.allSymbols.get(0).generateSchemeCode();
		} else {
			return "";
		}
	}
}
