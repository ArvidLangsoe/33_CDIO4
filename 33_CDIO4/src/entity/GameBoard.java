package entity;

import entity.field.*;
import data.Reader;

public class GameBoard {
	private Field[] fields;
	private int fieldCounter;
	
	public GameBoard(){
		fields = new Field[40];
		fieldCounter = 1;
		Reader reader = new Reader("src/data/Feltliste.txt");
		String[][] fieldData = reader.readFile();
		fieldData = reader.formatFileData(fieldData);
		//oprette felterne
		for(int i = 0; i < fields.length; i++)
		{
			addField(fieldData[i]);
		}
	}

	public void addField(String[] information) {
		String fieldType = information[10];
//		System.out.println(fieldType);
		switch (fieldType) {
			case "Ejendom":
				addStreet(information);
				break;
			case "Rederi":
				addShipping(information);
				break;
			case "Tapperi":
				addBrewery(information);
				break;	
			case "Chancekort":
				addChance(information);
				break;
			case "Skat":
				addTax(information);
				break;
			case "Start":
			case "Besøg":
			case "Parkering":
			case "Fængsel":
				addNeutral(information);
				break;
			default:
				System.out.println("GameBoard: Not a valid field at field number: " + fieldCounter);
		}
		fieldCounter++;
	}
	
	public void addStreet(String[] information)
	{
		String name = information[0];
		String type = information[10];
		String description = information[3];
		int price = Integer.parseInt(information[2]);
		String colour = information[1];
		int baseRent = Integer.parseInt(information[3]);
		int housePrice = Integer.parseInt(information[11]);
		int[] houseRent = { Integer.parseInt(information[4]), Integer.parseInt(information[5]), Integer.parseInt(information[6]), Integer.parseInt(information[7]), Integer.parseInt(information[8]) };
		int pledge = Integer.parseInt(information[9]);
		fields[fieldCounter - 1] = new Street(name, type, description, price, colour, baseRent, housePrice, houseRent, pledge);
	}
	
	public void addShipping(String[] information)
	{
		String name = information[0];
		String type = information[10];
		String description = information[3];
		int price = Integer.parseInt(information[2]);
		fields[fieldCounter - 1] = new Shipping(name, type, description, price);
	}
	public void addBrewery(String[] information) 
	{
		String name = information[0];
		String type = information[10];
		String description = information[3];
		int price = Integer.parseInt(information[2]);
		fields[fieldCounter - 1] = new Brewery(name, type, description, price);
	}
	
	public void addChance(String[] information) 
	{
		String name = information[0];
		String type = information[10];
		String description = information[3];
		fields[fieldCounter - 1] = new ChanceField(name, type, description);
	}
	
	public void addTax(String[] information) 
	{
		String name = information[0];
		String type = information[10];
		String description = "";
		boolean rate = Boolean.parseBoolean(information[5]);
		int amount = Integer.parseInt(information[4]);
		fields[fieldCounter - 1] = new Tax(name, type, description, rate, amount);
	}
	
	public void addNeutral(String[] information) 
	{
		String name = information[0];
		String type = information[10];
		String description = "";
		fields[fieldCounter - 1] = new Neutral(name, description, type);
	}
		
	public Field getField(int fieldNum)
	{
		return fields[fieldNum-1];	
	}
}
