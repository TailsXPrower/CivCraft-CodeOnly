package ru.tailsxcraft.civcraft.components;

import ru.tailsxcraft.civcraft.object.CultureChunk;

public abstract class AttributeBiomeBase extends Component {

	public AttributeBiomeBase() {
		this.typeName = "AttributeBiomeBase";
	}
	
	public abstract double getGenerated(CultureChunk cultureChunk);
	public abstract String getAttribute();
}
