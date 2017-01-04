package entity.field;

public class Neutral extends Field
{
/**
 * @param name The name of the field.
 * @param description The description of the field.
 * @param type The type of the field.
 */
	
	public Neutral (String name, String description, String type){
		super(name, description, type);
	}

@Override
public int getRent() {
	return 0;
}
}
