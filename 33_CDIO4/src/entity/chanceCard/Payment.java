package entity.chanceCard;

	public class Payment extends ChanceCard{

		//Instance variables
		private int amount;
		
		public Payment(String type, String description, int amount)
		{
			super(type, description);
			this.amount = amount;
		}
		
		/**
		 * Method getAmount: Returns the amount of the payment.
		 * @return The amount of the payment.
		 */
		public int getAmount()
		{
			return amount;
		}
		
}

