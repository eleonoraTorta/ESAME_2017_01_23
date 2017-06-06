package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private UndirectedGraph <Country, DefaultEdge> grafo;
	private List <Country> countries ;
	private BordersDAO dao;
	private Map <Integer, Country> countryMap ;
	
	//risultati simulazione
	private List<CountryAndNum> stanziali;
	
	public Model(){
		dao = new BordersDAO();
		
	}
	
	public List <Country> getCountries(){
		if(countries == null){
			this.countries = dao.loadAllCountries();
		}
		return countries;
	}
	public List <CountryAndNum> creaGrafo(int anno){
		
		// inizializzo qui il grafo perche l'utente potrebbe premere piu volte il bottone calcola confini 
		// e quindi richiedere diversi grafi ogni volta
		this.grafo = new SimpleGraph<> (DefaultEdge.class);
		
		// aggiungi i vertici
		this.countryMap = new HashMap <>();
		
		for(Country c : this.getCountries()){
			this.countryMap.put(c.getcCode(), c);
		}
		Graphs.addAllVertices(grafo, this.getCountries());
		
		// aggiungi gli archi
		List <IntegerPair> confini = dao.getCountryPairs(anno);
		for(IntegerPair p : confini){
			grafo.addEdge(this.countryMap.get(p.getN1()), this.countryMap.get(p.getN2()));
		}
		
		List<CountryAndNum> lista = new ArrayList <>();
		for(Country c : this.getCountries()){
			// il numero di vicini corrisponde al numero di stati confinanti
			int confinanti = Graphs.neighborListOf(this.grafo,c).size(); 
			if(confinanti != 0){
				lista.add(new CountryAndNum (c,confinanti ));
			}
		}
		Collections.sort(lista);
		return lista;
	}
	
	public int simula(Country partenza) {
		Simulatore sim = new Simulatore (this.grafo);
		
		sim.inserisci(partenza);
		sim.run();
		this.stanziali = sim.getPresenti();
	
		return sim.getPassi();
	}
	
	public List<CountryAndNum> getStanziali() {
		return this.stanziali;
	}
	
	public static void main (String args[]){
		Model m = new Model ();
		List <CountryAndNum> lista = m.creaGrafo(2000);
		System.out.println(m.grafo);
		for(CountryAndNum cn : lista){
			System.out.format ("%s : %d\n", cn.getCountry().toString(), cn.getNum());
		}
	}

	

	
	

}
