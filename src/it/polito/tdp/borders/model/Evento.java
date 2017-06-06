package it.polito.tdp.borders.model;

public class Evento implements Comparable <Evento>  {
	
	//C'e un solo tipo di evento : INGRESSO
	
	private int num; 			//quante persone
	private Country country; 	//in quale stato
	private int time ;			//a quale istante di tempo
	
	public Evento(int num, Country country, int time) {
		super();
		this.num = num;
		this.country = country;
		this.time = time;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	// ordinamento per tempo crescente
	@Override
	public int compareTo(Evento o) {
		return this.time - o.time;
	} 

}
