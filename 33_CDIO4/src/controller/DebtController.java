package controller;

import desktop_resources.GUI;
import entity.field.Ownable;
import entity.field.Street;
import entity.Player;

/**
 * This class is responsible for handling the debt of players when they owe the
 * bank or another player money.
 * 
 * @author Gruppe33
 *
 */
public class DebtController {

	// Instance variables
	private BuildingController buildingController;

	/**
	 * Constructor: Constructs a DebtController.
	 * 
	 * @param buildingController
	 *            The BuildingController.
	 */
	public DebtController(BuildingController buildingController) {
		this.buildingController = buildingController;
	}

	/**
	 * Checks if a Street field owned by a player can be sold. The field can not
	 * be sold if there are buildings on any of his fields with the same color.
	 * 
	 * @param street
	 *            The Street field to check for.
	 * @param player
	 *            The owner of the Street field.
	 * @return Whether the field can be sold or not.
	 */
	private boolean canStreetBeSold(Street street, Player player) {
		if (street.getNumbOfHouses() > 0) {
			return false;
		}

		for (int i = 0; i < player.getFields().length; i++) {
			if (player.getFields()[i].getColour().equals(street.getColour())) {
				if (((Street) player.getFields()[i]).getNumbOfHouses() > 0)
					return false;
			}
		}

		return true;

	}

	/**
	 * Gets the player's fields without buildings on them.
	 * 
	 * @param player
	 *            The player to get the fields from.
	 * @return an array of the player's fields.
	 */
	private Ownable[] getFieldsWithoutBuildings(Player player) {
		// All the player's fields.
		Ownable[] playerFields = player.getFields();

		if (playerFields == null) {
			return null;
		}
		// Array to hold the fields without buildings on them.
		Ownable[] fieldsWithoutBuildings = new Ownable[playerFields.length];

		// Counts the fields of the object type Street
		int j = 0;

		// Go through the player's fields.
		for (int i = 0; i < playerFields.length; i++) {
			// If a field is a Street object
			if (playerFields[i] instanceof Street) {

				if (canStreetBeSold((Street) playerFields[i], player)) {
					fieldsWithoutBuildings[j] = playerFields[i];
					j++;
				}

			} else {
				fieldsWithoutBuildings[j] = playerFields[i];
				j++;
			}
		}
		return fieldsWithoutBuildings;
	}

	/**
	 * Creates a String array with the fields names and price divided by 2.
	 * 
	 * @param fields
	 *            The fields to be operated.
	 * @return String array of the fields names and price.
	 */
	private String[] fieldsToString(Ownable[] fields) {

		if (fields == null) {
			return null;
		}
		String[] fieldsNames = new String[fields.length];
		int fieldsNamesLength = 0;
		// Go through the fields array
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null) {
				// Type the fields name and price into the String array.
				fieldsNames[i] = fields[i].getName() + " " + "(" + (fields[i].getPrice() / 2) + " kr.)";
				fieldsNamesLength++;
			}
		}

		if (fieldsNamesLength == 0) {
			return null;
		}
		String[] fieldsNamesNew = new String[fieldsNamesLength];
		for (int i = 0; i < fieldsNamesLength; i++) {
			fieldsNamesNew[i] = fieldsNames[i];
		}

		return fieldsNamesNew;
	}

	/**
	 * Sells the field owned by the player. The field can only be sold if the
	 * field itself and its color siblings does not have any buildings on them.
	 * 
	 * @param player
	 *            Owner of the field.
	 */
	public void sellField(Player player) {
		Ownable[] fieldsWithoutBuildings = getFieldsWithoutBuildings(player);
		String[] fieldsToString = fieldsToString(fieldsWithoutBuildings);
		String message = "Hvilken grund vil du sælge?";
		String failMessage = "Du har ikke nogle grunde at sælge";
		String[] options = MainController.addReturnToArray(fieldsToString);

		if (options.length == 1) {
			GUI.getUserSelection(failMessage, options);
			return;
		}

		String answer = GUI.getUserSelection(message, options);

		if (answer.equals("Gå tilbage")) {
			return;
		} else {
			// Index of the selected field in the list.
			int index = -1;
			for (int i = 0; i < fieldsToString.length; i++) {
				if (answer.equals(fieldsToString[i])) {
					index = i;
					break;
				}
			}

			player.changeAccountBalance(fieldsWithoutBuildings[index].getPrice() / 2);
			player.removeField(fieldsWithoutBuildings[index]);
			GUI.setBalance(player.getName(), player.getAccountBalance());
			GUI.removeOwner(fieldsWithoutBuildings[index].getFieldNumber());
		}
	}

	/**
	 * The method playerAffordPayment checks to see if a player can afford a
	 * payment. If the player can not afford the required payment, the player
	 * loses the game.
	 * 
	 * @param player
	 *            The player to be checked.
	 * @param payment
	 *            The payment to be withdrawn from the player's account.
	 * @return the current state of the player.setLost() condition. If true, the
	 *         player will lose. If false, the player may pay the payment, and
	 *         continue playing.
	 */
	public boolean playerAffordPayment(Player player, int payment) {
		if (player.getAccountBalance() <= payment) {
			return handleDebt(player, payment);

		} else
			return true;
	}

	/**
	 * The method playerHasLost is a method which, when a player loses, sets all
	 * of the player's owned fields free for other players to purchase again.
	 * 
	 * @param player
	 *            The affected player.
	 * 
	 */
	public void playerHasLost(Player player) {
		player.setHasLost(true);

		if (player.getFields() != null) {

			Ownable[] fields = new Ownable[player.getFields().length];
			fields = player.getFields();

			// Go through the fields array
			for (int i = 0; i < fields.length; i++) {
				// If the field is a Street field
				if (fields[i] instanceof Street) {
					Street streetField = (Street) fields[i];
					if (streetField.getNumbOfHouses() == 5) {
						buildingController.changeHotels(1);
					} else {
						buildingController.changeHouses(streetField.getNumbOfHouses());
					}
					streetField.changeNumbOfHouses(-streetField.getNumbOfHouses());
					GUI.setHouses(streetField.getFieldNumber(), streetField.getNumbOfHouses());
					GUI.setHotel(streetField.getFieldNumber(), false);
					GUI.removeOwner(streetField.getFieldNumber());
				}
				player.removeField(fields[i]);
			}
		}

		player.changeAccountBalance(-player.getAccountBalance() - 1);
		GUI.setBalance(player.getName(), player.getAccountBalance());
		GUI.getUserButtonPressed("Du tabte spillet.", "Ok");
	}

	/**
	 * Checks if a player can handle the debt he owes.
	 * 
	 * @param player
	 *            The player that owes debt.
	 * @param debt
	 *            The debt that is owed.
	 * @return Whether the player can handle the debt or not.
	 */
	public boolean handleDebt(Player player, int debt) {
		// If the player's debt is bigger than the players (fortune minus his
		// balance) divided by 2.
		if (((player.getFortune() - player.getAccountBalance()) / 2) + player.getAccountBalance() >= debt) {
			while (true) {
				String sellField = "Sælg grund";
				String sellHouse = "Sælg hus";
				String payDebt = "Betal gæld";
				String message = "Du har ikke råd til at betale din gæld, hvad vil du gøre?";
				String[] options = { sellField, sellHouse };

				if (player.getAccountBalance() >= debt) {
					String[] expandedOptions = { payDebt, options[0], options[1] };
					options = expandedOptions;
					message = "Du kan nu betale din gæld. Vil du sælge yderligere grunde eller bygninger?";
				}
				String userSelection = GUI.getUserSelection(message, options);

				if (userSelection.equals(sellHouse))
					buildingController.sellBuildingMenu(player);
				else if (userSelection.equals(sellField))
					sellField(player);
				else if (userSelection.equals(payDebt))
					break;
			}

			return true;
		}

		else {
			playerHasLost(player);
			return false;
		}
	}
}