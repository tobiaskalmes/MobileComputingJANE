package de.uni_trier.jane.simulation.parametrized.parameters;

public abstract class Parameter {

	private String key;
	private String description;

	public Parameter(String key, String description) {
		this.key = key;
		this.description = description.trim(); //.length() > 0 ? "\"" + description + "\"" : "";
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public abstract String toString();
	
	public void printUsage(String prefix) {
		System.out.println(prefix + toString() + " " + description);
	}
	
	public void printComment() {
		System.out.println("#");
		System.out.println("# " + key);
		System.out.println("#");
		if(description.length() == 0) {
			return;
		}
		String[] words = description.split("\\s+");
		int len = 0;
		for(int i=0; i<words.length; i++) {
			String word = words[i];
			if(len == 0) {
				System.out.print("# ");
			}
			len += word.length() + 1;
			if(len > 80) {
				len = word.length() + 1;
				System.out.println();
				System.out.print("# ");
			}
			System.out.print(word + " ");
		}
		System.out.println();
		System.out.println("#");
	}

}
