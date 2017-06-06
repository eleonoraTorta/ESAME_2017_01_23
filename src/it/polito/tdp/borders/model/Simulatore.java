package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//Parametri di simulazioni (cioe le costanti)
	private int INIZIALI = 1000;
	private double PERC_STANZIALI = 0.5;
	
	// Modello del mondo (World status)
	// per ciascuna nazione, quanti stanziali?
	private Map<Country, Integer> stanziali;
	private UndirectedGraph<Country, DefaultEdge> grafo;
	
	//Misure in uscita (variabili di interesse)
	private int passi;
	
	//Coda degli eventi
	private PriorityQueue <Evento> coda;
	
	public Simulatore(UndirectedGraph<Country, DefaultEdge> grafo){
		// inizializzo il grafo una volta passato da costruttore
		this.grafo = grafo;
		
		// inizializza a 0 il numero di stanziali in ogni stato
		this.stanziali = new HashMap <Country, Integer>();
		for(Country c : grafo.vertexSet()){
			this.stanziali.put(c, 0);
		}
		
		//inizializzo la coda
		this.coda = new PriorityQueue <Evento>();	
	}
	
	//metodo che riempie la coda inziale
	public void inserisci(Country c){
		Evento e = new Evento(INIZIALI, c, 1);
		coda.add(e);
	}
	
	
	public void run(){
		
		this.passi =0;
		while (!coda.isEmpty()){
			Evento e = coda.poll();
			this.passi = e.getTime(); // alla fine del while mi restituisce il valore massimo del tempo  
									  // (gli eventi sono ordinati in ordine di tempo)
			
			//stanziali
			int stanz = (int) (e.getNum() * this.PERC_STANZIALI);
			
			// nomadi = numero di persone che vanno in uno stato
			// quelli arrivati - quelli che si fermano , diviso il numero di stati confinanti
			int numDiConfinanti = Graphs.neighborListOf(grafo, e.getCountry()).size();
			int nomadi = (e.getNum() - stanz) / numDiConfinanti;
			
			//aggiungiamo ai stanziali anche i resti della divisione precedente
			// di fatto calcolo nuovamente da zero gli stanziali, per differenza dalle persone arrivate totali
			stanz = e.getNum() - (nomadi * numDiConfinanti);
			
			// aggiornare il modello del mondo
			// --> contabilizzare questi stanziali
			stanziali.put(e.getCountry(), stanziali.get(e.getCountry()) + stanz);
			
			if( nomadi >0){
				// schedulare gli eventi futuri
				//--> inserire desstinazione dei nomadi
				for (Country c : Graphs.neighborListOf(grafo, e.getCountry())){
					Evento e2 = new Evento (nomadi, c, e.getTime() +1);
					coda.add(e2);
				}
			}
		} //while coda
	}
	
	public int getPassi(){
		return this.passi;
	}
	
	public List <CountryAndNum> getPresenti(){
		List <CountryAndNum> lista = new ArrayList <>();
		
		for(Country c : stanziali.keySet()){
			if(stanziali.get(c) >0){
				lista.add(new CountryAndNum(c,stanziali.get(c)));
			}
		}
		
		Collections.sort(lista);
		return lista;
			
	}
	

}
