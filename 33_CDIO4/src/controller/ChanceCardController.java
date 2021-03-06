package controller;

import entity.ChanceCardDeck;
import entity.Player;
import entity.chanceCard.*;
import controller.PrisonController;
import desktop_resources.GUI;
import controller.DebtController;

/**
 * This class is responsible for handling the chance cards when the player draws
 * them..
 * 
 * @author Gruppe33
 *
 */
public class ChanceCardController {

	// Instance variables.
	private ChanceCardDeck deck;
	private PrisonController prisonController;
	private DebtController bank;
	private MainController mainController;

	/**
	 * Constructor: Conctructs a ChanceCardController.
	 * 
	 * @param prisonController
	 *            The PrisonController.
	 * @param bank
	 * @param mainController
	 *            The MainController.
	 */
	ChanceCardController(PrisonController prisonController, DebtController bank, MainController mainController) {
		deck = new ChanceCardDeck();
		this.prisonController = prisonController;
		this.bank = bank;
		this.mainController = mainController;

	}

	/**
	 * Method draw: Decides what happens when a player draws a card from the
	 * chance card deck.
	 * 
	 * @param player
	 *            The player who draws a chance card.
	 */
	public void draw(Player player)

	{
		ChanceCard currentCard = deck.draw();

		String type = currentCard.getType();
		GUI.displayChanceCard(currentCard.getDescription());
		GUI.getUserButtonPressed("Du har trukket et Prøv-lykken kort \n" + currentCard.getDescription(), "Ok");
		switch (type) {
		case "Grant":
			drawGrant(currentCard, player);
			break;
		case "Payment":
			drawPayment(currentCard, player);
			break;
		case "MoveToNearestShipping":
			drawMoveToNearestShipping(currentCard, player);
			break;
		case "MoveToPrison":
			drawMoveToPrison(currentCard, player);
			break;
		case "MoveToField":
			drawMoveToField(currentCard, player);
			break;
		case "MoveThreeSteps":
			drawMoveThreeSteps(currentCard, player);
			break;
		// case "Prison": drawPrison(currentCard, player);
		// break;
		// case "Tax": drawTaxCard(currentCard, player);
		// break;
		// case "Party": drawParty(currentCard, player);
		// break;
		}
	}

	/**
	 * Method DrawMoveToNearestShipping: Decides what happens when a player
	 * draws a chance card of the type 'move to nearest shipping'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawMoveToNearestShipping(ChanceCard currentCard, Player player) {
		MoveToNearestShipping card = (MoveToNearestShipping) currentCard;
		int[] shippingPos = card.getShippingPositions();
		int currentPos = player.getPosition();

		// Sets the position of the player to the first shipping field if you
		// are passed the last shipping field.
		player.setPosition(shippingPos[0]);
		// Sets the position of the player to the next shipping field if you
		// haven't passed the last shipping field.
		for (int i = 0; i < shippingPos.length; i++) {
			if (shippingPos[i] > currentPos) {
				player.setPosition(shippingPos[i]);
				break;
			}
		}
		mainController.getLandOnFieldController().setDoubleRent(card.getDoubleRent());
		mainController.movePlayerOnGUI();
		// Hvis start passeres.
		if (currentPos > player.getPosition()) {
			mainController.givePlayer4000();
		}
		mainController.getLandOnFieldController().landOnField(player, 0);
	}

	/**
	 * Method drawMoveToPrison: Decides what happens when a player draws a
	 * chance card of the type 'move to prison'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawMoveToPrison(ChanceCard currentCard, Player player) {
		prisonController.sendToPrison(player);
	}

	/**
	 * Method drawMoveToField: Decides what happens when a player draws a chance
	 * card of the type 'move to field'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawMoveToField(ChanceCard currentCard, Player player) {
		mainController.movePlayerTo(((MoveToField) currentCard).getMoveTo());
		mainController.getLandOnFieldController().landOnField(player, 0);

	}

	/**
	 * Method drawGrant: Decides what happens when a player draws a chance card
	 * of the type 'grant'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawGrant(ChanceCard currentCard, Player player) {
		Grant grant = (Grant) currentCard;
		if (player.getFortune() <= 15000) {
			player.changeAccountBalance(grant.getAmount());
			GUI.setBalance(player.getName(), player.getAccountBalance());
		}
	}

	/**
	 * Method drawMoveThreeSteps: Decides what happens when a player draws a
	 * chance card of the type 'move three steps'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawMoveThreeSteps(ChanceCard currentCard, Player player) {
		MoveThreeSteps move = (MoveThreeSteps) currentCard;
		mainController.movePlayer(move.getSteps());
		mainController.getLandOnFieldController().landOnField(player, 0);
	}

	/**
	 * Method drawPayment: Decides what happens when a player draws a chance
	 * card of the type 'payment'.
	 * 
	 * @param currentCard
	 *            The card drawn.
	 * @param player
	 *            The player who draws the chance card.
	 */
	private void drawPayment(ChanceCard currentCard, Player player) {

		if (bank.playerAffordPayment(player, ((Payment) currentCard).getAmount())) {
			player.changeAccountBalance(((Payment) currentCard).getAmount());
			GUI.setBalance(player.getName(), player.getAccountBalance());
		}
	}

}
